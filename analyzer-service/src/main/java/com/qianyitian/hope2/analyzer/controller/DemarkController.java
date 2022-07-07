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

    @RequestMapping("/demark/fund/{portfolio}")
    @CrossOrigin
    public String fundDemark(@PathVariable String portfolio, @RequestParam(value = "days2Now", required = false) Integer days2Now) {
        return demarkService.fundDemark(portfolio, days2Now);
    }

    @CrossOrigin
    @RequestMapping("/demark-backtrack/{code}")
    public String demarkBacktrack(@PathVariable String code, @RequestParam(value = "days2Now", required = false) Integer days2Now, @RequestParam(value = "rehabilitation", required = false) String rehabilitation) {
        return demarkService.demarkBacktrack(code, days2Now, rehabilitation);
    }

    @CrossOrigin
    @RequestMapping("/demark-backtrack-advance/{code}")
    public String demarkBacktrackAdvance(@PathVariable String code, @RequestParam(value = "t2Now", required = false) Integer t2Now, @RequestParam(value = "t", required = true) String t) {
        return demarkService.demarkBacktrack(code, t2Now, "ea", t);
    }

    @CrossOrigin
    @RequestMapping("/demark-backtrack/weekly/{code}")
    public String demarkBacktrackWeekly(@PathVariable String code, @RequestParam(value = "days2Now", required = false) Integer days2Now, @RequestParam(value = "rehabilitation", required = false) String rehabilitation) {
        return demarkService.demarkBacktrack(code, days2Now, rehabilitation);
    }

    @CrossOrigin
    @RequestMapping("/demark-backtrack/fund/{code}")
    public String fundDemarkBacktrack(@PathVariable String code, @RequestParam(value = "days2Now", required = false) Integer days2Now) {
        return demarkService.fundDemarkBacktrack(code, days2Now);
    }
}
