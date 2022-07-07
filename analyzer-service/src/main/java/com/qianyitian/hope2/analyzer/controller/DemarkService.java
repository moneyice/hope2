package com.qianyitian.hope2.analyzer.controller;

import com.alibaba.fastjson.JSON;
import com.github.benmanes.caffeine.cache.Cache;
import com.qianyitian.hope2.analyzer.analyzer.DemarkAnalyzer;
import com.qianyitian.hope2.analyzer.analyzer.StockAnalyzerFacotry;
import com.qianyitian.hope2.analyzer.analyzer.UnDemarkAnalyzer;
import com.qianyitian.hope2.analyzer.config.Constant;
import com.qianyitian.hope2.analyzer.funds.model.FundBar;
import com.qianyitian.hope2.analyzer.funds.model.FundInfo;
import com.qianyitian.hope2.analyzer.model.*;
import com.qianyitian.hope2.analyzer.service.MyFavoriteStockService;
import com.qianyitian.hope2.analyzer.service.StockSelecter;
import com.qianyitian.hope2.analyzer.util.AlgorithmUtil;
import com.qianyitian.hope2.analyzer.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DemarkService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private MyFavoriteStockService favoriteStockService;

    @Autowired
    StockAnalyzerFacotry stockAnalyzerFacotry;

    @Autowired
    private Cache<String, String> cache;


    public String getStockCacheStatus() {
        return favoriteStockService.getCacheStatus();
    }

    public String demark(String portfolio, Integer days2Now) {
        String report = null;
        final int days2NowNumber = days2Now == null ? DemarkAnalyzer.DEFAULT_DAYS2NOW : days2Now;
        try {
            report = cache.get("demark" + portfolio + days2NowNumber, key -> {
                return portfolioReportJson(portfolio, days2NowNumber);
            });
        } catch (NullPointerException e) {
            report = e.toString();
            logger.error("demark-" + portfolio, e);
        }
        return report;
    }

    public String fundDemark(String portfolio, Integer days2Now) {
        String report = null;
        final int days2NowNumber = days2Now == null ? DemarkAnalyzer.DEFAULT_DAYS2NOW : days2Now;
        try {
            report = cache.get("demarkfund" + portfolio + days2NowNumber, key -> {
                return fundPortfolioReportJson(portfolio, days2NowNumber);
            });
        } catch (NullPointerException e) {
            report = e.toString();
            logger.error("demarkfund-" + portfolio, e);
        }
        return report;
    }

    //生成组合的报告
    protected String portfolioReportJson(String portfolio, Integer days2Now) {
        AnalyzeResult result = portfolioReport(portfolio, days2Now);
        String content = JSON.toJSONString(result);
        return content;
    }

    //生成组合的报告
    protected String fundPortfolioReportJson(String portfolio, Integer days2Now) {
        AnalyzeResult result = fundPortfolioReport(portfolio, days2Now);
        String content = JSON.toJSONString(result);
        return content;
    }

    //生成组合的报告
    public AnalyzeResult portfolioReport(String portfolio, Integer days2Now) {
//        DemarkAnalyzer stockAnalyzer = (DemarkAnalyzer) stockAnalyzerFacotry.getStockAnalyzer(EStockAnalyzer.Demark);
        DemarkAnalyzer stockAnalyzer = new DemarkAnalyzer();
        if (days2Now != null) {
            stockAnalyzer.setDaysToNow(days2Now);
        }
        SymbolList symbols = favoriteStockService.getSymbols(portfolio);
        StockSelecter hs = new StockSelecter(symbols.getSymbols(), favoriteStockService);
        hs.setFilterEmptyResult(false);
        hs.addAnalyzer(stockAnalyzer);
        hs.startAnalyze(Constant.TYPE_DAILY);
        AnalyzeResult result = hs.getAnalyzeResult();
        logger.info("get portfolioReport " + portfolio + " from system");
        return result;
    }

    //生成组合的报告
    public AnalyzeResult fundPortfolioReport(String portfolio, Integer days2Now) {
        DemarkAnalyzer stockAnalyzer = new DemarkAnalyzer();
        if (days2Now != null) {
            stockAnalyzer.setDaysToNow(days2Now);
        }
        SymbolList symbols = favoriteStockService.getSymbols(portfolio);
        StockSelecter hs = new StockSelecter(symbols.getSymbols(), favoriteStockService) {
            @Override
            public Stock getStock(String code, String klineType) {
                FundInfo fund = getFund(code);
                if (fund == null) {
                    return null;
                }
                Stock stock = convert2StockFormat(fund);
                return stock;
            }
        };
        hs.setFilterEmptyResult(false);
        hs.addAnalyzer(stockAnalyzer);
        hs.startAnalyze(Constant.TYPE_DAILY);
        AnalyzeResult result = hs.getAnalyzeResult();
        logger.info("get portfolioReport " + portfolio + " from system");
        return result;
    }

    private Stock getStock(String code, String rehabType, String t) {
        Stock stock = null;
        if (Constant.TYPE_WEEKLY.equals(t)) {
            stock = favoriteStockService.getStockWeekly(code);
        } else if (Constant.TYPE_MONTHLY.equals(t)) {
            stock = favoriteStockService.getStockMonthly(code);
        } else {
            if ("ea".equals(rehabType)) {
                stock = favoriteStockService.getStockDailySA(code);
            } else {
                stock = favoriteStockService.getStockDaily(code);
            }
        }
        return stock;
    }


    private FundInfo getFund(String code) {
        String content = favoriteStockService.getFundDetail(code);
        FundInfo fundInfo = JSON.parseObject(content, FundInfo.class);
        return fundInfo;
    }

    public String demarkBacktrack(String code, Integer days2Now, String rehabilitation) {
        return demarkBacktrack(code, days2Now, rehabilitation, Constant.TYPE_DAILY);
    }

    public String demarkBacktrack(String code, Integer t2Now, String rehabilitation, String t) {
        String seriesData = null;
        final int days2NowNumber = t2Now == null ? DemarkAnalyzer.DEFAULT_DAYS2NOW + 100 : t2Now;
        final String rehabType = "ea".equals(rehabilitation) ? "ea" : "non";
        try {
            seriesData = cache.get("demark" + code + days2NowNumber + t + rehabType, key -> {
                return stockReport(code, days2NowNumber, rehabType, t);
            });
        } catch (Exception e) {
            seriesData = e.toString();
            logger.error("demark error " + code, e);
        }
        return seriesData;
    }

    public String fundDemarkBacktrack(String code, Integer days2Now) {
        String seriesData = null;
        final int days2NowNumber = days2Now == null ? DemarkAnalyzer.DEFAULT_DAYS2NOW + 100 : days2Now;
        try {
            seriesData = fundReport(code, days2NowNumber);
        } catch (Exception e) {
            seriesData = e.toString();
            logger.error("fund demark error " + code, e);
        }
        return seriesData;
    }

    //生成单个证券的报告
    protected String stockReport(String code, int t2NowNumber, String rehabType, String t) {
        Stock stock = getStock(code, rehabType, t);
        if (stock == null) {
            return "stock not exists " + code;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("code", stock.getCode());
        map.put("name", stock.getName());

        List<Number[]> bars = convert2ChartFormat(stock);
        map.put("bars", bars);

        StockSelecter hs = new StockSelecter(null, null);
        ResultInfo resultInfo = null;
        //buy flags
        {
            //DemarkAnalyzer stockAnalyzer = (DemarkAnalyzer) stockAnalyzerFacotry.getStockAnalyzer(EStockAnalyzer.Demark);
            DemarkAnalyzer stockAnalyzer = new DemarkAnalyzer();
            stockAnalyzer.setDaysToNow(t2NowNumber);
            resultInfo = hs.analyze(stockAnalyzer, stock);
            if (resultInfo != null) {
                map.put("flag", resultInfo.getData().get("flag"));
            }
        }
        //sell flags
        {
//            UnDemarkAnalyzer unStockAnalyzer = (UnDemarkAnalyzer) stockAnalyzerFacotry.getStockAnalyzer(EStockAnalyzer.UnDemark);
            UnDemarkAnalyzer unStockAnalyzer = new UnDemarkAnalyzer();
            unStockAnalyzer.setDaysToNow(t2NowNumber);
            ResultInfo unResultInfo = hs.analyze(unStockAnalyzer, stock);
            if (unResultInfo != null) {
                map.put("flagSell", unResultInfo.getData().get("flag"));
            }
        }

        //计算收益
        {
            if (resultInfo != null) {
                FixInvestmentAccount sameCostAccountDay = new FixInvestmentAccount(t2NowNumber);
                sameCostAccountDay.run(bars, FixInvestmentAccount.SAME_COST_EVERY_DAY);
                Profit sameCostProfitDay = sameCostAccountDay.getProfit();


                FixInvestmentAccount sameAmountAccountWeek = new FixInvestmentAccount(t2NowNumber);
                sameAmountAccountWeek.run(bars, FixInvestmentAccount.SAME_COST_EVERY_WEEK);
                Profit sameAmountProfitWeek = sameAmountAccountWeek.getProfit();

                FixInvestmentAccount sameAmountAccountMonth = new FixInvestmentAccount(t2NowNumber);
                sameAmountAccountMonth.run(bars, FixInvestmentAccount.SAME_COST_EVERY_MONTH);
                Profit sameAmountProfitMonth = sameAmountAccountMonth.getProfit();


                List<DemarkFlag> flagList = (List<DemarkFlag>) resultInfo.getData().get("flag");
                List<Long> bsBuyPointDateList = flagList.stream().map(DemarkFlag::getSetup).collect(Collectors.toList());
                List<Long> bcBuyPointDateList = flagList.stream().map(DemarkFlag::getCountdown).collect(Collectors.toList());

                FixInvestmentAccount demarkAccount = new FixInvestmentAccount(t2NowNumber);
                List<Long> all = new ArrayList<>();
                all.addAll(bsBuyPointDateList);
                all.addAll(bcBuyPointDateList);
                demarkAccount.run(bars, all);
                Profit demarkProfit = demarkAccount.getProfit();

                FixInvestmentAccount demarkAccountWithoutBC = new FixInvestmentAccount(t2NowNumber);
                demarkAccountWithoutBC.run(bars, bsBuyPointDateList);
                Profit demarkProfitWithoutBC = demarkAccountWithoutBC.getProfit();


                map.put("profit1", demarkProfit);
                map.put("profit2", sameCostProfitDay);
                map.put("profit3", sameAmountProfitWeek);
                map.put("profit4", sameAmountProfitMonth);
                map.put("profit5", demarkProfitWithoutBC);
            }
        }

        String content = JSON.toJSONString(map);
        return content;
    }

    //生成单个基金的报告
    protected String fundReport(String code, int days2NowNumber) {
        FundInfo fund = getFund(code);
        if (fund == null) {
            return "fund not exists " + code;
        }

        Stock stock = convert2StockFormat(fund);

        Map<String, Object> map = new HashMap<>();
        map.put("code", stock.getCode());
        map.put("name", stock.getName());
        List<Number[]> bars = convert2ChartFormat(stock);
        map.put("bars", bars);
        StockSelecter hs = new StockSelecter(null, null);
        ResultInfo resultInfo = null;

        //buy flags
        {
            DemarkAnalyzer stockAnalyzer = new DemarkAnalyzer();
            stockAnalyzer.setDaysToNow(days2NowNumber);
            resultInfo = hs.analyze(stockAnalyzer, stock);
            if (resultInfo != null) {
                map.put("flag", resultInfo.getData().get("flag"));
            }
        }


        //sell flags
        {
            UnDemarkAnalyzer unStockAnalyzer = new UnDemarkAnalyzer();
            unStockAnalyzer.setDaysToNow(days2NowNumber);
            ResultInfo unResultInfo = hs.analyze(unStockAnalyzer, stock);
            if (unResultInfo != null) {
                map.put("flagSell", unResultInfo.getData().get("flag"));
            }
        }
        //计算收益
        {
            if (resultInfo != null) {
                FixInvestmentAccount sameCostAccountDay = new FixInvestmentAccount(days2NowNumber);
                sameCostAccountDay.run(bars, FixInvestmentAccount.SAME_COST_EVERY_DAY);
                Profit sameCostProfitDay = sameCostAccountDay.getProfit();

                FixInvestmentAccount sameCostAccountWeek = new FixInvestmentAccount(days2NowNumber);
                sameCostAccountWeek.run(bars, FixInvestmentAccount.SAME_COST_EVERY_WEEK);
                Profit sameCostProfitWeek = sameCostAccountWeek.getProfit();

                FixInvestmentAccount sameCostAccountMonth = new FixInvestmentAccount(days2NowNumber);
                sameCostAccountMonth.run(bars, FixInvestmentAccount.SAME_COST_EVERY_MONTH);
                Profit sameCostProfitMonth = sameCostAccountMonth.getProfit();

                List<DemarkFlag> flagList = (List<DemarkFlag>) resultInfo.getData().get("flag");
                List<Long> bsBuyPointDateList = flagList.stream().map(DemarkFlag::getSetup).collect(Collectors.toList());
                List<Long> bcBuyPointDateList = flagList.stream().map(DemarkFlag::getCountdown).collect(Collectors.toList());

                FixInvestmentAccount demarkAccount = new FixInvestmentAccount(days2NowNumber);
                List<Long> all = new ArrayList<>();
                all.addAll(bsBuyPointDateList);
                all.addAll(bcBuyPointDateList);
                demarkAccount.run(bars, all);
                Profit demarkProfit = demarkAccount.getProfit();

                FixInvestmentAccount demarkAccountWithoutBC = new FixInvestmentAccount(days2NowNumber);
                demarkAccountWithoutBC.run(bars, bsBuyPointDateList);
                Profit demarkProfitWithoutBC = demarkAccountWithoutBC.getProfit();


                map.put("profit1", demarkProfit);
                map.put("profit2", sameCostProfitDay);
                map.put("profit3", sameCostProfitWeek);
                map.put("profit4", sameCostProfitMonth);
                map.put("profit5", demarkProfitWithoutBC);
            }
        }
        maxBackdraw(fund, days2NowNumber, map);
        String content = JSON.toJSONString(map);
        logger.error("get stockReport " + code + " from system");
        return content;
    }

    public void maxBackdraw(FundInfo fund, Integer days, Map map) {
        FundBar[] fundBars = AlgorithmUtil.maxDrawback(fund.getFundBarList(), days);
        long startPoint = Constant.ONE_DAY_MILLISECONDS * fundBars[0].getDate().toEpochDay();
        long endPoint = Constant.ONE_DAY_MILLISECONDS * fundBars[1].getDate().toEpochDay();
        map.put("maxBackdrawStartPoint", startPoint);
        map.put("maxBackdrawEndPoint", endPoint);
        String maxDrawback = Utils.calcRangeLabel(fundBars[0].getValue(), fundBars[1].getValue());
        map.put("maxBackdraw", maxDrawback);
    }


    private Stock convert2StockFormat(FundInfo fund) {
        Stock stock = new Stock();
        stock.setCode(fund.getCode());
        stock.setName(fund.getName());
        List<FundBar> fundBarList = fund.getFundBarList();

        List<KLineInfo> collect = fundBarList.stream().map(bar -> {
            KLineInfo kLineInfo = new KLineInfo();
            kLineInfo.setDate(bar.getDate());
            double close = bar.getValue();
            kLineInfo.setOpen(close);
            kLineInfo.setHigh(close);
            kLineInfo.setLow(close);
            kLineInfo.setClose(close);
            return kLineInfo;
        }).collect(Collectors.toList());
        stock.setkLineInfos(collect);
        return stock;
    }

    private boolean buy(long dateLong, List<DemarkFlag> flag) {
        for (DemarkFlag demarkFlag : flag) {
            if (demarkFlag.getSetup() == dateLong || demarkFlag.getCountdown() == dateLong) {
                return true;
            }
        }
        return false;
    }

    protected List<Number[]> convert2ChartFormat(List<ProfitDaily> dailyProfit) {
        List<Number[]> collect = dailyProfit.stream().map(bar -> {
            Number[] row = new Number[]{bar.getDate(), bar.getProfit() * 100};
            return row;
        }).collect(Collectors.toList());
        return collect;
    }

    protected List<Number[]> convert2ChartFormat(Stock stock) {
        List<Number[]> collect = stock.getkLineInfos().stream().map(bar -> {
            long dateMilliSeconds = Constant.ONE_DAY_MILLISECONDS * bar.getDate().toEpochDay();
            Number[] row = new Number[]{dateMilliSeconds, bar.getOpen(), bar.getHigh(), bar.getLow(), bar.getClose()};
            return row;
        }).collect(Collectors.toList());
        return collect;
    }


}




