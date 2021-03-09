package com.qianyitian.hope2.analyzer.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig implements WebMvcConfigurer {

    @Bean
    public Cache<String, String> cache() {
        return Caffeine.newBuilder()
                .maximumSize(128).recordStats()
                .expireAfterAccess(60, TimeUnit.MINUTES)
                .expireAfterWrite(60, TimeUnit.MINUTES)
//            .refreshAfterWrite(30, TimeUnit.MINUTES)
                .build();
    }


}