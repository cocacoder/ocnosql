#!/bin/bash
TIME_BEGIN=`date +'%s.%N'`
echo "!-------begin to compact log of $HADOOP_HOME AND $HBASE_HOME,begin time is `date +'%Y-%m-%d %H:%M:%S'` ------!"
tomorrow=`date -d "tomorrow" +%d`
if [ $tomorrow  == '01' ];then
	runRemoteCmd.sh "source ~/.bash_profile;find  $HADOOP_HOME/logs/ -name 'hadoop-$user-*.log.*[!gz]' -exec gzip {} \;" all /home/ocnosql/bin/deploy.conf
	runRemoteCmd.sh "source ~/.bash_profile;find  $HBASE_HOME/logs/ -name 'hbase-$user-*.log.*[!gz]' -exec gzip {} \;" all /home/ocnosql/bin/deploy.conf
fi
TIME_END=`date +'%s.%N'`
TIME_RUN=`awk 'BEGIN {print '$TIME_END'-'$TIME_BEGIN'}'`
echo "!-------end compact log ,run time is $TIME_RUN-------!"
