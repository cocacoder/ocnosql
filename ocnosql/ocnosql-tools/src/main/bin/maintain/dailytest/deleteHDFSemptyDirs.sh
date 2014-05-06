#!/bin/bash
#set -x
# Description:  Delete old empty directorys from HDFS
#               Delete diretorys from $destDir which modification time are little than $currentTime-$interval 
# Author:       Zigao Wang <wangzg@asiainfo-linkage.com>

#       usage: $0 destDir interval(minutes)
#
export HADOOP_HOME=/home/ocnosql/cdh43/hadoop-2.0.0-cdh4.3.0
 if [ $# -ne 2 ]
 then 
    echo "usage:$0 destDir intervalTime(minutes)"
    exit 1
 else
    destDir=$1
    interval=$2
 fi
TIME_BEGIN=`date +'%s.%N'`
echo "!-------begin to delete HDFS empty dir of $destDir-------!"
#sh source_profile.sh
 currentTime=`date +%s` 
 ${HADOOP_HOME}/bin/hdfs dfs -ls -R $destDir >/tmp/all.$$.$currentTime
 count=`cat /tmp/all.$$.$currentTime|wc -l`
echo $count
 if [ $count -lt 1 ]
 then
    echo -e "\033[31;40;1m Error! please check if $destDir exists! \033[0m"
    cat /tmp/all.$$.$currentTime
    exit 1
 fi

 cat /tmp/all.$$.$currentTime|grep '^d' >/tmp/directorys.$$.$currentTime
 dirCount=`cat /tmp/directorys.$$.$currentTime|wc -l`
 if [ $dirCount -ne 0 ]
 then
     while read line
     do
        dirname=`echo $line|awk '{print $8}'`
        dirTime=`echo $line|awk '{print $6" "$7":00"}'`
        timeDiff=$(($currentTime-$(date +%s -d "$dirTime")))
        intervalSec=$(($interval*60))
        filenum=`${HADOOP_HOME}/bin/hdfs dfs -ls $dirname|wc -l`
        if [ $filenum -eq 0 ] && [ $timeDiff -gt $intervalSec ]
        then
            echo "deleting $timeDiff $intervalSec $dirTime    $dirname"
            ${HADOOP_HOME}/bin/hdfs dfs -rmr $dirname
        fi
     done </tmp/directorys.$$.$currentTime

 fi
TIME_END=`date +'%s.%N'`
TIME_RUN=`awk 'BEGIN {print '$TIME_END'-'$TIME_BEGIN'}'`
echo "!-------end delete HDFS empty dir $destDir ,run time is $TIME_RUN-----------!"
