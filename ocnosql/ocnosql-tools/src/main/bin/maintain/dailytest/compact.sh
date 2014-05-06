#!/bin/bash
#set -x
###################
# usage:$0 sleep_time(defalut:100 seconds ) root_dir(default:hbase) column_family(default:F)
#
#
###################
user=`whoami`
HADOOP_HOME=`ps -ef|grep $user|grep namenode|awk '{for(i=1;i<NF;i++) {if($i~"-Dhadoop.home.dir=") print substr($i,19)}}'`
if [ $HADOOP_HOME'a' == 'a' ];then
        HADOOP_HOME=`ps -ef|grep $host|grep datanode|awk '{for(i=1;i<NF;i++) {if($i~"-Dhadoop.home.dir=") print substr($i,19)}}'`
fi
HADOOP_MR1_HOME=`ps -ef|grep $user|grep jobtracker|awk '{for(i=1;i<NF;i++) {if($i~"-Dhadoop.home.dir=") print substr($i,19)}}'`
HBASE_HOME=`ps -ef|grep master|awk '{for(i=1;i<NF;i++) {if($i~"-Dhbase.home.dir=") print substr($i,18)}}'`

if [ 'a'$1'a'=='aa' ]
 then
	sleep_time=1
else
	sleep_time=$1
fi
#hbase dir in hdfs
if [ 'a'$2'a'=='aa' ]
 then
	root_dir="hbase_test2"
else
	root_dir=$2
fi
#column_family 
if [ 'a'$3'a'=='aa' ]
 then
	column_family="F"
else
	column_family=$3
fi
if [ 'a'$4'a'=='aa' ];then
	plus=50	
else
	plus=$4
fi
TIME_BEGIN=`date +'%s.%N'`
echo "!------ begin  compact all table,begin time is `date +'%Y-%m-%d %H:%M:%S'` -------------------------!"
#****************************get the tableName list******************************
#sh source_profile.sh
$HBASE_HOME/bin/hbase hbck>hbase_hbck_compact.tmp 2>&1 
cat hbase_hbck_compact.tmp|grep 'is ok'|grep -v 'ROOT'|grep -v 'META'|awk '{print $1}'>tableName.tmp
#****************************determine if the compact is nessessary*************
while read line
do
  hfile_num=`$HADOOP_HOME/bin/hdfs dfs -ls /$root_dir/$line/*/$column_family | grep -v "Found"|wc -l`>/dev/null 2>&1 
  region_num=`$HADOOP_HOME/bin/hdfs dfs -ls /$root_dir/$line |grep -v '\/\.'| wc -l` >/dev/null 2>&1
  a=$[$plus+$region_num]
  echo "table="$line",hfile_num="$hfile_num",region_num="$region_num
  if [ $hfile_num -gt $a ];then
#  echo "do major compact table : "$line
   echo "major_compact '$line'"
   echo "major_compact '$line'" | hbase shell > /dev/null 2>&1
    sleep $sleep_time
  fi
done < ./tableName.tmp

rm -r ./hbase_hbck_compact.tmp
rm -r ./tableName.tmp
TIME_END=`date +'%s.%N'`
TIME_RUN=`awk 'BEGIN {print '$TIME_END'-'$TIME_BEGIN'}'`
echo "!------ end compact all table,end time is `date +'%Y-%m-%d %H:%M:%S'`,run time is $TIME_RUN ------------!"
