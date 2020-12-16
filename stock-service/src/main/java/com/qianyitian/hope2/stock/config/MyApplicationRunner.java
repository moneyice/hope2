package com.qianyitian.hope2.stock.config;

import com.qianyitian.hope2.stock.search.EffectiveWordMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class MyApplicationRunner implements ApplicationRunner {

  @Autowired
  EffectiveWordMatcher matcher;

  @Override
  public void run(ApplicationArguments applicationArguments) throws Exception {
    matcher.init();
  }
  
}