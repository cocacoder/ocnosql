#!/bin/bash
#set -x
TIME_BEGIN=`date +'%s.%N'`
echo "!------ begin to fsck ,begin time is `date +'%Y-%m-%d %H:%M:%S'`-------!"

	user=`whoami`
	HADOOP_HOME=`ps -ef|grep $user|grep namenode|awk '{for(i=1;i<NF;i++) {if($i~"-Dhadoop.home.dir=") print substr($i,19)}}'`
	if [ $HADOOP_HOME'a' == 'a' ];then
	        HADOOP_HOME=`ps -ef|grep $host|grep datanode|awk '{for(i=1;i<NF;i++) {if($i~"-Dhadoop.home.dir=") print substr($i,19)}}'`
	fi
	HADOOP_MR1_HOME=`ps -ef|grep $user|grep jobtracker|awk '{for(i=1;i<NF;i++) {if($i~"-Dhadoop.home.dir=") print substr($i,19)}}'`
	HBASE_HOME=`ps -ef|grep master|awk '{for(i=1;i<NF;i++) {if($i~"-Dhbase.home.dir=") print substr($i,18)}}'`
    

	$HADOOP_HOME/bin/hdfs fsck / > fsck.tmp 2>&1 
    mis_rep_blk=`cat fsck.tmp|grep "Mis-replicated blocks:"|awk  '{print $3}'`
    min_rep_rate=`cat fsck.tmp|grep "Minimally replicated blocks:"|awk -F'(' '{if(NC<4) print substr($2,0,5)}'`
    avg_rep_blk=`cat fsck.tmp|grep "Average block replication:"|awk  '{print $4}'`
    #echo "Mis-replicated blocks:"$mis_rep_blk
    if [ $mis_rep_blk'a' != "0a" ]
        then 
    	echo "miss block already! the number is "$mis_rep_blk
    fi
    
    echo $min_rep_rate 99.99|awk '{if($1<$2) print "minimally replication blocks "$1" is smaller than 99.99%"}'
    echo $avg_rep_blk 3.0|awk '{if($1!=$2) print "the number of average replication block is "$1" ,not 3.0"}'
    #if [ $min_rep_rate -lt  99.99 ] #    then 
    #	echo "min_rep_rate is fewer than 99.99%"
    #fi
   # period=`date +%H`
   # if [ 'a'$period'a' == 'a00a' ]
   #     then 
   # 	echo "the number of average replication block is "$avg_rep_blk
   # fi
    rm ./fsck.tmp
#    sleep 3600
TIME_END=`date +'%s.%N'`
TIME_RUN=`awk 'BEGIN {print '$TIME_END'-'$TIME_BEGIN'}'`
echo "!-------end fsck ,run time is $TIME_RUN--------!"
