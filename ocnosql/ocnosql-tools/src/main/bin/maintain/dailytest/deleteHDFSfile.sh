#!/bin/bash
#set -x
# Description:  Delete old files from HDFS
#               Delete files from $destDir whose modification time is little than $currentTime-$interval 
# Author:       Zigao Wang <wangzg@asiainfo-linkage.com>

#       usage: $0 destDir interval(minutes)
#
HADOOP_HOME=/home/ocnosql/cdh43/hadoop-2.0.0-cdh4.3.0
 if [ $# -ne 2 ]
 then 
    echo "usage:$0 destDir intervalTime(minutes)"
    exit 1
 else
    destDir=$1
    interval=$2
 fi
TIME_BEGIN=`date +'%s.%N'`
echo "!------begin to delete HDFS file of $destDir whose modification time is $interval minutes ago-------------!"
 batchCount=100 #execute delete command while file numbers reach batchCount
# sh source_profile.sh
 currentTime=`date +%s` 
 ${HADOOP_HOME}/bin/hdfs dfs -ls $destDir >/tmp/all.$$.$currentTime
 count=`cat /tmp/all.$$.$currentTime|wc -l`
 if [ $count -lt 1 ]
 then
    echo -e "\033[31;40;1m Error! please check if $destDir exists or it's empty! \033[0m"
    cat /tmp/all.$$.$currentTime
    exit 1
 fi

 cat /tmp/all.$$.$currentTime|grep '^-' >/tmp/files.$$.$currentTime
 cat /tmp/all.$$.$currentTime|grep '^d' >/tmp/directorys.$$.$currentTime
 fileCount=`cat /tmp/files.$$.$currentTime|wc -l`
 dirCount=`cat /tmp/directorys.$$.$currentTime|wc -l`
 if [ $fileCount -ne 0 ]
 then
     count=0
     deleteFiles=""
     while read line
     do
        filename=`echo $line|awk '{print $8}'`
        fileTime=`echo $line|awk '{print $6" "$7":00"}'`
        timeDiff=$(($currentTime-$(date +%s -d "$fileTime")))
        intervalSec=$(($interval*60))
#***************************if the period between current time and the file changed *********************
        if [ $timeDiff -gt $intervalSec ]
        then
            echo "deleting $timeDiff $intervalSec $fileTime  $filename"
            deleteFiles=$deleteFiles" $filename"
            count=$(($count+1))
        fi
#**************************if the count of file waiting for delete is more than batchCount***************
        if [ $count -ge $batchCount ]
        then
           ${HADOOP_HOME}/bin/hdfs dfs -rm $deleteFiles
           deleteFiles=""
           count=0
        fi 
     done </tmp/files.$$.$currentTime
     if [ `expr length "1$deleteFiles"` -ne 1 ] 
     then
           ${HADOOP_HOME}/bin/hdfs dfs -rm $deleteFiles
           deleteFiles=""
           count=0
     fi
 fi
#*****************
 if [ $dirCount -ne 0 ]
 then
     while read line
     do
        directory=`echo $line|awk '{print $8}'`
        $0 $directory $interval 
     done </tmp/directorys.$$.$currentTime
 fi
TIME_END=`date +'%s.%N'`
TIME_RUN=`awk 'BEGIN {print '$TIME_END'-'$TIME_BEGIN'}'`
echo "!------end delete HDFS file of $destDir ,run time is $TIME_RUN-------!"
