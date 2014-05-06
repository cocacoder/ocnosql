#!/bin/bash
#set -x
####################################
#  usage: $0 $1(default:30;meaning delete the snapshot of $1 days ago )
#  run time is 219.264
####################################
TIME_BEGIN=`date +'%s.%N'`
echo "!-------begin to do hbase snapshot,begin time is `date +'%Y-%m-%D %H:%M:%S'` -----!"
date=`date +%Y%m%d`
hbase_dir="hbase"
if [ $# -eq 1 ]
then
	date2=`date -d "-$1 day" +%Y%m%d`
else
	date2=`date -d "-1 month" +%Y%m%d`
fi
#sh source_profile.sh
#echo "******************************get the table list****************************"
${HBASE_HOME}/bin/hbase hbck > hbase_hbck.log 2>&1
cat hbase_hbck.log|grep "is okay."|grep -v "ROOT"|grep -v "META"|awk '{print $1}'>table_list
table_number=`wc -l table_list`

#echo "******************************generate snapshot shell************************"
cat table_list|awk -v date2=$date2 '{print "delete_snapshot '\''"$1"_snapshot_"date2"'\''"}'>delete_snapshot
cat table_list|awk -v date=$date '{print "snapshot '\''"$1"'\'','\''"$1"_snapshot_"date"'\''"}'>snapshot_shell
echo exit>>delete_snapshot
echo exit>>snapshot_shell

#echo "*****************************begin to delete former snapshot "$table_number" tables*******"
	echo "!------- delete $date2's snapshot ------!"
	${HBASE_HOME}/bin/hbase shell delete_snapshot>delete_snapshot.tmp 2>&1
#echo "*****************************begin to snapshot "$table_number" tables*******"
	echo "!------ create  $date's snapshot -----!"
	${HBASE_HOME}/bin/hbase shell snapshot_shell>snapshot.tmp 2>&1
#echo "*****************************delelte tmp file*******************************"

rm hbase_hbck.log
rm table_list
rm snapshot_shell
rm delete_snapshot
rm delete_snapshot.tmp
rm snapshot.tmp
#echo "****************************back up .snapshot directory*********************"
dirCount=`${HADOOP_HOME}/bin/hdfs dfs -ls /snapshot/$date|wc -l` > /dev/null 2>&1
if [ $dirCount -gt 0 ];then
	${HADOOP_HOME}/bin/hdfs dfs -rm -R /snapshot/$date/* >/dev/null 2>&1 
	${HADOOP_HOME}/bin/hdfs dfs -cp /$hbase_dir/.hbase-snapshot/* /snapshot/$date >/dev/null 2>&1
else
	${HADOOP_HOME}/bin/hdfs dfs -mkdir /snapshot/$date >/dev/null 2>&1
	${HADOOP_HOME}/bin/hdfs dfs -cp /$hbase_dir/.hbase-snapshot/* /snapshot/$date >/dev/null 2>&1
fi
TIME_END=`date +'%s.%N'`
TIME_RUN=`awk 'BEGIN {print '$TIME_END'-'$TIME_BEGIN'}'`
echo "!-------end hbase snapshot,end time is `date +'%Y-%m-%D %H:%M:%S'` ,run time is $TIME_RUN-----!"
