#!/bin/bash
APP_NAME=stock-service-1.0.jar
PID=$(ps -ef | grep $APP_NAME| grep -v grep|grep -v kill| awk '{ print $2 }')
if [ -z "$PID" ]
then
    echo Application is already stopped
else
    echo kill $PID
    kill $PID
fi

APP_NAME=spider-service-1.0.jar
PID=$(ps -ef | grep $APP_NAME| grep -v grep|grep -v kill| awk '{ print $2 }')
if [ -z "$PID" ]
then
    echo Application is already stopped
else
    echo kill $PID
    kill $PID
fi

APP_NAME=analyzer-service-1.0.jar
PID=$(ps -ef | grep $APP_NAME| grep -v grep|grep -v kill| awk '{ print $2 }')
if [ -z "$PID" ]
then
    echo Application is already stopped
else
    echo kill $PID
    kill $PID
fi

