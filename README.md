# hope2

hope的改进版，删除了spring cloud的依赖，网络调用直接配置在各个spring boot里面。

```
hope2:
  service:
    spider: http://127.0.0.1:8001
    stock: http://127.0.0.1:8002
    analyzer: http://127.0.0.1:8003
```

直接启动
```
SpiderApplication
StockApplication
AnalyzerApplication
```


开始运行爬虫
```
http://127.0.0.1:8001/force_refresh_stocks
```

查看分析报告
```
http://127.0.0.1:8003/
```

股票数据默认存储在磁盘。
查看报告需要配置redis.