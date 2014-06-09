#!/bin/sh
if [ "$#" = "" ];then
	echo "Usage: ./ocnosql_clearHistoryTable.sh <tablePrefix> <date> eg.: ./ocnosql_clearHistoryTable.sh dr_query 20120901"
	echo "       ./ocnosql_clearHistoryTable.sh <tablePrefix> <startDate> <endDate> eg.: ./ocnosql_clearHistoryTable.sh dr_query 20120901 20120930"
	exit 1
fi


if [ $# == 2 ];then  # 参数：<表前缀> <日期>
  date==`echo $2|grep -e '^[0-9]\{4\}[0-9]\{2\}[0-9]\{2\}$'`
  echo $date
  if [ date == "" ];then # 日期参数验证
    echo "$2 must match format ：YYYYMMDD."
    exit 1
  fi
  
  tableName=$1$2
  echo $tableName
  echo 'echo "disable $tableName;drop $tableName" | hbase shell' # 删除表
  exit 1
fi


if [ $# == 3 ];then  # 参数：<表前缀> <开始日期> <结束日期>
  startDate=`echo $2|grep -e '^[0-9]\{4\}[0-9]\{2\}[0-9]\{2\}$'`
  echo $startDate
  endDate=`echo $3|grep -e '^[0-9]\{4\}[0-9]\{2\}[0-9]\{2\}$'`
  echo $endDate
  if [ startDate == "" ] || [ endDate == "" ];then  # 开始日期和结束日期参数验证
    echo "$2 or $3 must match format ：YYYYMMDD."
    exit 1
  fi
  
  startTime=`date --date="$2" +%s`
  endTime=`date --date="$3" +%s`
  if [ startTime > endTime ];then
    echo "startDate must less than endDate."
    exit 1
  fi
  
  time=`date --date="$2" +%Y%m%d`
  while [startDate <= endDate]
  do
    tableName=$1$time
    echo 'echo "disable $tableName;drop $tableName" | hbase shell' # 删除表
    $time=`date --date="$time -24 hour" +%Y%m%d` 
  done
fi