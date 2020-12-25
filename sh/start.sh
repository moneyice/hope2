#!/bin/bash
nohup java -jar -Dspring.profiles.active=prod stock-service-1.0.jar >> ./log/stock.log 2>&1 &
nohup java -jar -Dspring.profiles.active=prod spider-service-1.0.jar >> ./log/spider.log 2>&1 &
nohup java -jar -Dspring.profiles.active=prod analyzer-service-1.0.jar >> ./log/analyzer.log 2>&1 &
