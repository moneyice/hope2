package com.qianyitian.hope2.analyzer.analyzer;


import com.qianyitian.hope2.analyzer.config.Constant;
import com.qianyitian.hope2.analyzer.model.DemarkFlag;
import com.qianyitian.hope2.analyzer.model.KLineInfo;
import com.qianyitian.hope2.analyzer.model.ResultInfo;
import com.qianyitian.hope2.analyzer.model.Stock;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

//1、Buy-Setup：买入结构的条件是连续9T或以上的收盘价低于先前第4个T的收盘价，视为一个完整的买入结构。
//（注：这里的限定条件是“连续”9T或以上，期间不能中断）
//2、Buy-Countdown：每当某个收盘价低于先前第2个T的最低价时计数增加1，计数可以不连续，当计数增加到13个交易日意味着卖盘动能已经耗尽，往往是下跌趋势的反转点。
//（注：这里的限定条件是累计13个T，不要求是“连续”的，期间可以中断）



//1、Sell-Setup：卖出结构的条件是连续9T或以上的收盘价高于先前第4个T的收盘价，视为一个完整的卖出结构。
//（注：这里的限定条件是“连续”9T或以上，期间不能中断）
//2、Sell-Countdown：每当某个收盘价高于先前第2个T的最高价时计数增加1，计数可以不连续，当计数增加到13个交易日意味着买盘动能已经耗尽，往往是上升趋势的反转点。
//（注：这里的限定条件是累计13个T，不要求是“连续”的，期间可以中断）

//目前不支持并发
public class DemarkAnalyzer extends AbstractStockAnalyzer {
    public static int DEFAULT_DAYS2NOW = 200;
    //计算距今多少天的日K线
    int daysToNow = DEFAULT_DAYS2NOW;
    // 连续9T或以上
    int buySetupDays = 9;
    // 收盘价低于先前第X个T的收盘价
    int buySetupBeforeDay = 4;

    // demark is 13
    int countDownNumber = 13;
    private boolean setupReady = false;

    private List<DemarkSelect> selectList = null;

    public DemarkAnalyzer() {
    }

    public void setDaysToNow(int daysToNow) {
        this.daysToNow = daysToNow;
    }

    @Override
    public boolean analyze(ResultInfo resultInfo, Stock stock) {
        boolean ok = false;
        selectList = new LinkedList<>();
        setStock(stock);

        List<KLineInfo> infos = stock.getkLineInfos();
        if (infos.size() <= daysToNow + buySetupBeforeDay) {
            // 元数据天数要求大于考察天数
            return ok;
        }

        int setupTimes = 0;

        for (int i = infos.size() - daysToNow - 1; i < infos.size(); i++) {
            if (infos.get(i).getClose() < infos.get(i - buySetupBeforeDay)
                    .getClose()) {
                setupTimes++;
            } else {
                if (setupTimes >= buySetupDays) {
                    DemarkSelect sr = new DemarkSelect();
                    sr.setStock(stock);
                    // use now field to store the setup point info
                    sr.setSetupPoint(infos.get(i - 1));
                    sr.setSetupNumber(setupTimes);
                    selectList.add(sr);
                }
                setupTimes = 0;
            }
        }



//        for (int i = infos.size() - daysToNow - 1; i < infos.size(); i++) {
//            if (infos.get(i).getClose() < infos.get(i - buySetupBeforeDay)
//                    .getClose()) {
//                setupTimes++;
//            } else {
//                if (setupReady) {
//                    DemarkSelect sr = new DemarkSelect();
//                    sr.setStock(stock);
//                    // use now field to store the setup point info
//                    sr.setSetupPoint(infos.get(i - 1));
//                    sr.setSetupNumber(setupTimes);
//                    selectList.add(sr);
//                    setupReady = false;
//                }
//                setupTimes = 0;
//            }
//            if (setupTimes >= buySetupDays) {
//                setupReady = true;
//            }
//
//        }

        prepareCountDownData();
        //如果需要setup的结果的话，就不需要过滤countdown 不存在的情况了
//        filter(selectList);

        if (selectList.isEmpty()) {
            return ok;
        }
        String msg = format(selectList);
        resultInfo.appendMessage(msg);
        ok = true;

        Map map = getFlags();
        resultInfo.setData(map);
        return ok;
    }

    protected Map getFlags() {
        Map map = new HashMap<>();

        List<DemarkFlag> list = new ArrayList<>();
        for (int i = 0; i < selectList.size(); i++) {
            DemarkFlag demarkFlag = convert(selectList.get(i));
            list.add(demarkFlag);
        }
        map.put("flag", list);
        return map;
    }

    private DemarkFlag convert(DemarkSelect demarkSelect) {
        DemarkFlag df = new DemarkFlag();

        df.setSetup(Constant.ONE_DAY_MILLISECONDS * demarkSelect.getSetupPoint().getDate().toEpochDay());
        df.setSetupDate(demarkSelect.getSetupPoint().getDate().toString());
        df.setSetupNumber(demarkSelect.getSetupNumber());

        if (demarkSelect.getCountDownPoint() != null) {
            df.setCountdown(Constant.ONE_DAY_MILLISECONDS * demarkSelect.getCountDownPoint().getDate().toEpochDay());
            df.setCountdownDate(demarkSelect.getCountDownPoint().getDate().toString());
            df.setCountdownNumber(demarkSelect.getCountDownNumber());
        }
        return df;
    }

    private void filter(List<DemarkSelect> selectList2) {
        for (Iterator iterator = selectList2.iterator(); iterator.hasNext(); ) {
            DemarkSelect demarkSelect = (DemarkSelect) iterator.next();
            if (demarkSelect.getCountDownPoint() == null) {
                iterator.remove();
            }
        }

    }

    private void prepareCountDownData() {
        if (selectList.isEmpty()) {
            return;
        }
        List<KLineInfo> infos = getStock().getkLineInfos();
        for (int i = 0; i < selectList.size(); i++) {
            findCountDownResult(selectList.get(i), infos);
        }
    }

    private void findCountDownResult(DemarkSelect demarkSelect,
                                     List<KLineInfo> infos) {
        int index = infos.indexOf(demarkSelect.getSetupPoint());
        int number = 0;
        KLineInfo countDownPoint = null;
        for (int i = index + 1; i < infos.size(); i++) {
            if (infos.get(i).getClose() < infos.get(i - 2).getLow()) {
                number++;
                countDownPoint = infos.get(i);
            }
            if (number >= countDownNumber) {
                demarkSelect.setCountDownNumber(number);
                // use to field to store the count down point info
                demarkSelect.setCountDownPoint(countDownPoint);
                break;
            }
        }
    }

    @Override
    public String getDescription() {
        String des = "1、Buy-Setup：买入结构的条件是连续9T或以上的收盘价低于先前第4个T的收盘价，视为一个完整的买入结构。（注：这里的限定条件是“连续”9T或以上，期间不能中断）" +
                "2、Buy-Countdown：每当某个收盘价低于先前第2个T的最低价时计数增加1，计数可以不连续，当计数增加到13个交易日意味着卖盘动能已经耗尽，往往是下跌趋势的反转点。" +
                "（注：这里的限定条件是累计13个T，不要求是“连续”的，期间可以中断）";


        return "Demark 指标\n" + des;
    }

    public String format(List<DemarkSelect> selectList) {
        StringBuilder sb = new StringBuilder();
        // 000001 平安银行
        // 2012-2-12 (9) --- 2012-3-30 (13)
        // 2013-1-12 (9) --- 2013-2-28 (13)
        for (int i = 0; i < selectList.size(); i++) {
            sb.append(" ").append(selectList.get(i).getSetupPoint().getDate().toString())
                    .append("(").append(selectList.get(i).getSetupNumber())
                    .append(",SETUP)->");
            if (selectList.get(i).getCountDownPoint() != null) {
                sb.append(
                        selectList.get(i).getCountDownPoint()
                                .getDate().toString()).append("(")
                        .append(selectList.get(i).getCountDownNumber())
                        .append(",BUY) #");
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public class DemarkSelect {
        Stock stock;
        String msg;
        private int setupNumber = 0;
        private KLineInfo setupPoint = null;
        private KLineInfo countDownPoint = null;
        private int countDownNumber = 0;

        public KLineInfo getCountDownPoint() {
            return countDownPoint;
        }

        public void setCountDownPoint(KLineInfo countDownPoint) {
            this.countDownPoint = countDownPoint;
        }

        public KLineInfo getSetupPoint() {
            return setupPoint;
        }

        public void setSetupPoint(KLineInfo setupPoint) {
            this.setupPoint = setupPoint;
        }

        public int getCountDownNumber() {
            return countDownNumber;
        }

        public void setCountDownNumber(int countDownNumber) {
            this.countDownNumber = countDownNumber;
        }

        public int getSetupNumber() {
            return setupNumber;
        }

        public void setSetupNumber(int setupNumber) {
            this.setupNumber = setupNumber;
        }

        public Stock getStock() {
            return stock;
        }

        public void setStock(Stock stock) {
            this.stock = stock;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

    }
}
