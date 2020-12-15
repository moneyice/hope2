package com.qianyitian.hope2.analyzer.controller;


import com.alibaba.fastjson.JSON;
import com.qianyitian.hope2.analyzer.config.PropertyConfig;
import com.qianyitian.hope2.analyzer.job.backtracking.BacktrackingJob;
import com.qianyitian.hope2.analyzer.job.backtracking.BuyAndHoldBacktrackingPolicy;
import com.qianyitian.hope2.analyzer.job.backtracking.MacdAndMean30BacktrackingPolicy;
import com.qianyitian.hope2.analyzer.model.Stock;
import com.qianyitian.hope2.analyzer.model.ValueLog;
import com.qianyitian.hope2.analyzer.service.DefaultStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
public class BacktrackingController {
	@Autowired
	private DefaultStockService stockService;
	@Autowired
	private PropertyConfig propertyConfig;

	@RequestMapping("/backtracking/{code}")
	public String compare(@PathVariable String code ,@RequestParam String startDate) {
		LocalDate fromDate=null;
		try{
			fromDate=LocalDate.parse(startDate);
		}catch (Exception e){
			//300 index starts from 2002 01
			//500 index starts from here
			fromDate=LocalDate.parse("2005-02-04");
		}

		Stock stock= stockService.getStockDaily(code);

		BacktrackingJob macdAndMean30PolicyJob= new BacktrackingJob(stock,new MacdAndMean30BacktrackingPolicy());
		macdAndMean30PolicyJob.setFromDate(fromDate);
		macdAndMean30PolicyJob.run();
		List<ValueLog> jobR1ValueLogList=macdAndMean30PolicyJob.getAccountSummary().getValueLogList();

		BacktrackingJob buyAndHoldPolicyJob= new BacktrackingJob(stock,new BuyAndHoldBacktrackingPolicy());
		buyAndHoldPolicyJob.setFromDate(fromDate);
		buyAndHoldPolicyJob.run();
		List<ValueLog> jobR2ValueLogList=buyAndHoldPolicyJob.getAccountSummary().getValueLogList();

		List<String> categoryList=new LinkedList<>();
		List<Double> r1DataList=new LinkedList<>();
		List<Double> r2DataList=new LinkedList<>();


		for (int i=0;i<jobR1ValueLogList.size();i++){
				ValueLog wanted=jobR1ValueLogList.get(i);
				categoryList.add(getCategoryLabel(wanted));
				r1DataList.add(wanted.getTotalValue());

				r2DataList.add(jobR2ValueLogList.get(i).getTotalValue());
		}

		Map<String,List> map=new HashMap<String,List>();
        map.put("category",categoryList);
        map.put("r1",r1DataList);
        map.put("r2",r2DataList);
		return JSON.toJSONString(map);
	}

	private String getCategoryLabel(ValueLog wanted){
		return wanted.getDate().getYear()+"-"+wanted.getDate().getMonthValue()+ "-"+wanted.getDate().getDayOfMonth();
	}
}
