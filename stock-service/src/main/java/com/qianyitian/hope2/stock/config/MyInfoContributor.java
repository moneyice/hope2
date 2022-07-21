package com.qianyitian.hope2.stock.config;

import java.util.Collections;

import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.Info;
import org.springframework.stereotype.Component;

@Component
public class MyInfoContributor implements org.springframework.boot.actuate.info.InfoContributor {

    @Autowired
    private ApplicationContext context;

    @Override
    public void contribute(Info.Builder builder) {

        String[] activeProfiles = context.getEnvironment().getActiveProfiles();
        String profile="dev";

        if(activeProfiles.length>0){
            profile=activeProfiles[0];
        }

        builder.withDetail("environment",
                Collections.singletonMap("active profile", profile));
    }

}