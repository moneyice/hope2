package com.qianyitian.hope2.analyzer.engine;

import com.alibaba.fastjson.JSON;
import com.google.common.io.Files;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.qianyitian.hope2.analyzer.funds.model.FundProfileInfo;
import com.qianyitian.hope2.analyzer.model.Stock;
import com.qianyitian.hope2.analyzer.service.MyFavoriteStockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class RuleEngineController {
    private Logger logger = LoggerFactory.getLogger(getClass());
    String rule_folder_path = "rule";
    @Autowired
    private MyFavoriteStockService favoriteStockService;

    @Autowired
    public Map<String, String> reportMapDB;

    public RuleEngineController() {
        List<AbstractFunction> functions = FunctionUtil.findAllFunctions();
        for (AbstractFunction abstractFunction : functions) {
            AviatorEvaluator.addFunction(abstractFunction);
        }
        //        AviatorEvaluator.setFunctionMissing(JavaMethodReflectionFunctionMissing.getInstance());
    }

    @PostMapping(value = "/saveScript/{ruleName}")
    public String saveScript(@PathVariable String ruleName, String data) throws IOException {
        String filename = ruleName + ".av";
        writeFile(filename, data);
        executeRule(ruleName);
        return "OK";
    }

    @PostMapping(value = "/loadScript/{ruleName}")
    public String loadScript(@PathVariable String ruleName) throws IOException {
        String filename = ruleName + ".av";
        return readFile(filename);
    }

    @PostMapping(value = "/saveXML/{ruleName}")
    public String saveXML(@PathVariable String ruleName, String data) throws IOException {
        String filename = ruleName + ".xml";
        writeFile(filename, data);
        return "OK";
    }

    @PostMapping(value = "/loadXML/{ruleName}")
    public String loadXML(@PathVariable String ruleName) throws IOException {
        String filename = ruleName + ".xml";
        return readFile(filename);
    }

    @PostMapping(value = "/executeRule")
    public List<FundProfileInfo> executeRule(String data) throws IOException {
        List<FundProfileInfo> list = new LinkedList<>();
        Expression compile = AviatorEvaluator.getInstance().compile(data, false);
        Map<String, Object> env = AviatorEvaluator.newEnv();

        String content = reportMapDB.get("fundProfile");
        List<FundProfileInfo> fundProfileInfos = JSON.parseArray(content, FundProfileInfo.class);



        for (FundProfileInfo fund : fundProfileInfos) {
            env.put("fund", fund);
            Long execute = (Long) compile.execute(env);
            if (execute == 1) {
                list.add(fund);
            }
        }
        return list;
    }

    private String readFile(String fileName) throws IOException {
        File from = new File(rule_folder_path, fileName);
        return Files.asCharSource(from, Charset.forName("UTF-8")).read();
    }

    private void writeFile(String fileName, String content) throws IOException {
        File to = new File(rule_folder_path, fileName);
        Files.asCharSink(to, Charset.forName("UTF-8")).write(content);
    }

}
