#!/bin/bash
set -x
HADOOP_HOME=/home/ocnosql/cdh43/hadoop-2.0.0-cdh4.3.0
HADOOP_MR1_HOME=/home/ocnosql/cdh43/hadoop-2.0.0-mr1-cdh4.2.1
HBASE_HOME=/home/ocnosql/cdh43/hbase-0.94.6-cdh4.3.0
TIME_BEGIN=`date +'%s.%N'`
echo "!-------begin to compact log of $HADOOP_HOME AND $HBASE_HOME ------!"
count_o=0
count=0
count2=0
count3=0
while [ $count_o -lt 3 ]
	do
	sum_hadoop=`find $HADOOP_HOME/logs -name 'hadoop-ocnosql*[!gz]'|xargs du|awk '{sum=sum+$1} END{print sum}'`;
	hadoop_log=`find $HADOOP_HOME/logs -name 'hadoop-ocnosql*log*[!gz]'`;
	sleep 10;
	sum_hadoop2=`find $HADOOP_HOME/logs -name 'hadoop-ocnosql*[!gz]'|xargs du|awk '{sum=sum+$1} END{print sum}'`;
	a=`expr $sum_hadoop2 - $sum_hadoop`
	if [ $a -ge 0 ];then
		count=$(($count+1))
		if [ $count -eq 1 ];then
		        cp $HADOOP_HOME/logs/hadoop-ocnosql-*.log $HADOOP_HOME/logs/hadoop-ocnosql-*.log_bak_`date +'%Y-%m-%d %H:%M:%S'`
		fi
		if [ $count -eq 2 ];then
		        >hadoop-ocnosql-*.log
		fi
	
	fi
#	sum_mr1=`find $HADOOP_MR1_HOME/logs -name 'hadoop-ocnosql*[!gz]'|xargs du|awk '{sum=sum+$1} END{print sum}'`;
#	sleep 10;
#	sum_mr1=`find $HADOOP_MR1_HOME/logs -name 'hadoop-ocnosql*[!gz]'|xargs du|awk '{sum=sum+$1} END{print sum}'`;
#	if [ `expr $sum_mr1 - $sum_mr1` -gt 1000 ];then
#	        count3=$(($count3+1))
#	        sleep 10
#	        if [ $count3 -eq 1 ];then
#	                cp $HADOOP_MR1_HOME/logs/hadoop-ocnosql-*.log $HADOOP_MR1_HOME/logs/hadoop-ocnosql-*.log_bak_`date +'%Y-%m-%d %H:%M:%S'`
#	        fi
#	        if [ $count3 -eq 2 ];then
#	                >hadoop-ocnosql-*.log
#	        fi
#	
#	fi
#	sum_hbase=`find $HBASE_HBASE/logs -name 'hbase-ocnosql*[!gz]'|xargs du|awk '{sum=sum+$1} END{print sum}'`;
#	sleep 10;
#	sum_hbase2=`find $HBASE_HOME/logs -name 'hbase-ocnosql*[!gz]'|xargs du|awk '{sum=sum+$1} END{print sum}'`;
#	if [ `expr $sum_hbase2 - $sum_hbase` -gt 1000 ];then
#	        count2=$(($count2+1))
#	        sleep 10
#	        if [ $count2 -eq 1 ];then
#	                cp $HBASE_HOME/logs/hadoop-ocnosql-*.log $HBASE_HOME/logs/hadoop-ocnosql-*.log_`date +'%Y-%m-%d %H:%M:%S'`
#	        fi
#	        if [ $count2 -eq 2 ];then
#	                >hbase-ocnosql-*.log
#	        fi
#	
#	fi
#	count_o+=1
done
