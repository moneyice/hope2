package com.qianyitian.hope2.stock.config;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

@Configuration
public class MapdbConfig {

    @Bean
    public DB mapdb() {
        DB db = DBMaker.fileDB("db/stock.mapdb")
                .checksumHeaderBypass()
                .fileMmapEnableIfSupported()//1
                .fileMmapPreclearDisable()//2
                .cleanerHackEnable()//3
                .closeOnJvmShutdown()//4
//                .transactionEnable()//5
                .concurrencyScale(128)//6
                .make();
        return db;
    }


    @Bean
    public Map<String, String> fundProfileMapDB(DB mapdb) {
        ConcurrentMap<String, String> map = mapdb.hashMap("fundProfile")
                .keySerializer(Serializer.STRING)
                .valueSerializer(Serializer.STRING)
                .createOrOpen();
        return map;
    }
}