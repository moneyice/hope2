#!/bin/sh
APP_NAME=spider-service-1.0

tpid=`ps -ef|grep $APP_NAME|grep -v grep|grep -v kill|awk '{print $2}'`
if [ ${tpid} ]; then
        echo $APP_NAME' is running.'
else
        echo $APP_NAME' is NOT running.'
fi

APP_NAME=stock-service-1.0

tpid=`ps -ef|grep $APP_NAME|grep -v grep|grep -v kill|awk '{print $2}'`
if [ ${tpid} ]; then
        echo $APP_NAME' is running.'
else
        echo $APP_NAME' is NOT running.'
fi

APP_NAME=analyzer-service-1.0

tpid=`ps -ef|grep $APP_NAME|grep -v grep|grep -v kill|awk '{print $2}'`
if [ ${tpid} ]; then
        echo $APP_NAME' is running.'
else
        echo $APP_NAME' is NOT running.'
fi
