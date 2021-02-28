package com.qianyitian.hope2.analyzer.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class DemarkController {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    DemarkService demarkService;

    public DemarkController() {
    }

    @GetMapping(value = "/cache/stock/status")
    public String cacheStatus() {
        return demarkService.getStockCacheStatus();
    }

    @RequestMapping("/demark/{portfolio}")
    @CrossOrigin
    public String demark(@PathVariable String portfolio, @RequestParam(value = "days2Now", required = false) Integer days2Now) {
        return demarkService.demark(portfolio, days2Now);
    }

    @CrossOrigin
    @RequestMapping("/demark-backtrack/{code}")
    public String demarkBacktrack(@PathVariable String code, @RequestParam(value = "days2Now", required = false) Integer days2Now) {
        return demarkService.demarkBacktrack(code, days2Now);
    }
}
