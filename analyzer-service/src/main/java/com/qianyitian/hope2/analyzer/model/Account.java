package com.qianyitian.hope2.analyzer.model;

import com.qianyitian.hope2.analyzer.util.BusinessException;
import com.qianyitian.hope2.analyzer.util.Utils;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by bing.a.qian on 10/2/2017.
 */
public class Account {
    //模拟base 100万

   static double base=1000000;
   static double fee_rate=0.0003;

    public double getAvailableValue() {
        return availableValue;
    }

    public void setAvailableValue(double availableValue) {
        this.availableValue = availableValue;
    }

    //可用金额
    double availableValue=base;
    //证券市值
    double marketValue=0;

    //总市值
    double totalValue=availableValue;

    List<TradeItem> tradeItemList=new LinkedList<>();

    Map<String,PositionItem> positionItemMap=new HashMap<>();

    public Account() {
    }

    public Account(double availableValue) {
        this.availableValue = availableValue;
    }

    public double getBase() {
        return base;
    }

    public void setBase(double base) {
        this.base = base;
    }

    public double getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(double totalValue) {
        this.totalValue = totalValue;
    }

    public double getMarketValue() {
        return marketValue;
    }

    public PositionItem getPosition(String code){
        return positionItemMap.get(code);
    }

    public List<TradeItem> getTradeItemList() {
        return tradeItemList;
    }


    public void setTradeItemList(List<TradeItem> tradeItemList) {
        this.tradeItemList = tradeItemList;
    }

    public void deal(String code, double tradeQuantity, double tradePrice, ETradeType tradeType, LocalDate tradeDate) throws BusinessException {
        String ok=validate(code,tradeType,tradeQuantity,tradePrice);
        if(ok!=null){
            throw new BusinessException(ok);
        }
        TradeItem tradeItem=new TradeItem();
        getTradeItemList().add(tradeItem);
        tradeItem.setCode(code);
        tradeItem.setTradeType(tradeType);
        tradeItem.setDate(tradeDate);
        tradeItem.setTradePrice(tradePrice);
        tradeItem.setQuantity(tradeQuantity);
        double tradeValue=tradeQuantity*tradePrice;
        double fee=tradeValue*fee_rate;

        if(tradeType==ETradeType.Buy){
            availableValue=availableValue-tradeValue-fee;

            //set position,计算仓位
            PositionItem positionItem= positionItemMap.get(code);
            if(positionItem==null){
                positionItem=new PositionItem();
                positionItem.setCode(code);
                positionItemMap.put(code,positionItem);
            }
            positionItem.setQuantity(positionItem.getQuantity()+tradeQuantity);
            positionItem.setValue(positionItem.getQuantity()*tradePrice);

            marketValue=calcMarketValue(code,tradePrice);
            totalValue=availableValue+marketValue;

            //set this deal's trading record
            tradeItem.setTradeValue(tradeValue);
            tradeItem.setFee(fee);
            tradeItem.setAvailableValue(availableValue);
            tradeItem.setMarketValue(marketValue);
            tradeItem.setTotalValue(totalValue);
        }if(tradeType==ETradeType.Sell) {
            availableValue=availableValue+tradeValue-fee;

            //set position,计算仓位
            PositionItem positionItem= positionItemMap.get(code);
            positionItem.setQuantity(positionItem.getQuantity()-tradeQuantity);
            positionItem.setValue(positionItem.getQuantity()*tradePrice);

            marketValue=calcMarketValue(code,tradePrice);
            totalValue=availableValue+marketValue;

            //set this deal's trading record
            tradeItem.setTradeValue(tradeValue);
            tradeItem.setFee(fee);
            tradeItem.setAvailableValue(availableValue);
            tradeItem.setMarketValue(marketValue);
            tradeItem.setTotalValue(totalValue);
        }else{
            //hode current positions
        }
    }

    /**
     * The holding securities number is only one at one time
     * @param code
     * @param tradePrice
     */
    public double calcMarketValue( String code, double tradePrice) {
        PositionItem positionItem= positionItemMap.get(code);
        if(positionItem==null){
            return 0;
        }
        return positionItem.getQuantity()*tradePrice;
    }

    private String validate(String code, ETradeType tradeType, double tradeQuantity,double tradePrice)  {
        String result=null;
        if(tradeType==ETradeType.Buy){
            double tradeValue=tradeQuantity*tradePrice;
            double fee=tradeValue *fee_rate;

            if(availableValue<tradeValue+fee){
                result="可用资金额不够";
            }
        }if(tradeType==ETradeType.Sell) {
            //因为目前一次只买一个标的

            PositionItem positionItem= positionItemMap.get(code);
            if(positionItem==null||positionItem.getQuantity()<tradeQuantity){
                result=code + " 可卖份数不足";
            }
        }else{

        }
        return result;
    }

    //Sell to 0% position
    public void clear(String code, double price,LocalDate tradeDate) throws BusinessException {
        PositionItem positionItem= positionItemMap.get(code);
        if(positionItem!=null){
            deal(code, positionItem.getQuantity(),price,ETradeType.Sell , tradeDate);
        }
    };

    //buy to 100% position
    public void allIn(String code,double price,LocalDate tradeDate) throws BusinessException {
        double availableNetValue=availableValue-availableValue*fee_rate;
        double tradeQuantity = Utils.get2Double(availableNetValue / price) ;

        deal(code,tradeQuantity,price,ETradeType.Buy,tradeDate);
    }


    public void deposit(double fixedInvestimentValue) {
        availableValue=availableValue+fixedInvestimentValue;
    }
}
