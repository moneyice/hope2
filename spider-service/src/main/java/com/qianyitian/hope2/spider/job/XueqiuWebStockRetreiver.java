package com.qianyitian.hope2.spider.job;



import com.qianyitian.hope2.spider.model.Stock;

import java.io.IOException;

public class XueqiuWebStockRetreiver extends WebStockRetreiver {
    String codeListHTML = "http://quote.eastmoney.com/stocklist.html";

    @Override
    public Stock getStockInfo(Stock stock) throws IOException {
//        String symbol = stock.getMarket() + stock.getCode();
//        String url = "http://xueqiu.com/S/#{symbol}/historical.csv";
//        url = url.replace("#{symbol}", symbol);
//        BufferedReader br = null;
//        InputStreamReader is = null;
//        try {
//            // Retrieve CSV File
//            URL xueqiuurl = new URL(url);
//            URLConnection connection = xueqiuurl.openConnection();
//            is = new InputStreamReader(connection.getInputStream());
//            br = new BufferedReader(is);
//            // pass the first head line
//            // symbol, date, open, high, low, close, volume
//            // SH600340 2003-12-30 12:00 AM 8.7 9.16 8.68 8.92 16666958
//            String line = br.readLine();
//            // Parse CSV Into Array
//            while ((line = br.readLine()) != null) {
//                DailyInfo daily = new DailyInfo();
//                try {
//                    line = line.replaceAll("\"", "");
//                    String[] result = line.split(",");
//                    double open = Utils.handleDouble(result[2]);
//                    if (open < 0.01) {
//                        continue;
//                    }
//                    double high = Utils.handleDouble(result[3]);
//                    double low = Utils.handleDouble(result[4]);
//                    double close = Utils.handleDouble(result[5]);
//                    long volume = Utils.handleLong(result[6]);
//                    LocalDate date = Utils.parseDate(result[1]);
//                    daily.setOpen(open);
//                    daily.setHigh(high);
//                    daily.setLow(low);
//                    daily.setClose(close);
//                    daily.setDate(date);
//                    daily.setVolume(volume);
//                } catch (Exception e) {
//                    System.out.println("process error " + line);
//                    throw new RuntimeException("process error " + line);
//                }
//                // by asc order
//                stock.getDailyinfos().add(daily);
//            }
//        } catch (IOException e) {
//            Logger log = Logger.getLogger("sa");
//            log.log(Level.SEVERE, e.toString() + "  " + symbol);
//            throw e;
//        } finally {
//            if (is != null) {
//                is.close();
//            }
//        }
        return stock;
    }


}
