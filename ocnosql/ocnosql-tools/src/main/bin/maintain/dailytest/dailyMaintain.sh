##!/bin/bash
#set -x

user=`whoami`
HADOOP_HOME=`ps -ef|grep $user|grep namenode|awk '{for(i=1;i<NF;i++) {if($i~"-Dhadoop.home.dir=") print substr($i,19)}}'`
if [ $HADOOP_HOME'a' == 'a' ];then
        HADOOP_HOME=`ps -ef|grep $host|grep datanode|awk '{for(i=1;i<NF;i++) {if($i~"-Dhadoop.home.dir=") print substr($i,19)}}'`
fi
HADOOP_MR1_HOME=`ps -ef|grep $user|grep jobtracker|awk '{for(i=1;i<NF;i++) {if($i~"-Dhadoop.home.dir=") print substr($i,19)}}'`
HBASE_HOME=`ps -ef|grep master|awk '{for(i=1;i<NF;i++) {if($i~"-Dhbase.home.dir=") print substr($i,18)}}'`

TIME_BEGIN=`date +'%s.%N'`
    echo -e "\033[31;40;1m !-----------begin to do daily maintain,time is `date +'%Y-%m-%d %H:%M:%S'` --------!\033[0m" 

export PATH=$HADOOP_HOME/bin:$HADOOP_HOME/sbin:$HADOOP_MR1_HOME/bin:$PATH
export PATH=$PATH:${HBASE_HOME}/bin:$HADOOP_HOME/bin
export PATH=$PATH:/home/ocnosql/util/bin
bin=`dirname $0`
bin=`cd $bin;pwd`
cd $bin
########### hdfs check ##########################
ifRun=`cat ifRun|grep -v '^#'|awk -F 'ifRun=' '{print $2}'`
if [ $ifRun'a' == "truea" ];then
    echo "  "`date +'%Y-%m-%d %H:%M:%S'`
	sh ha_daemon.sh 
fi
########### hdfs check ##########################
ifRun=`cat ifRun|grep -v '^#'|awk -F 'ifRun=' '{print $2}'`
if [ $ifRun'a' == "truea" ];then
    echo "  "`date +'%Y-%m-%d %H:%M:%S'`
	sh fsck.sh  
fi
########## hbase hbck  #########################
#
ifRun=`cat ifRun|grep -v '^#'|awk -F 'ifRun=' '{print $2}'`
if [ $ifRun'a' == "truea" ];then
    echo "  "`date +'%Y-%m-%d %H:%M:%S'`
	sh hbck.sh 
fi
#sh hbck_24times.sh 
########## balance hbase ########################
ifRun=`cat ifRun|grep -v '^#'|awk -F 'ifRun=' '{print $2}'`
if [ $ifRun'a' == "truea" ];then
    echo "  "`date +'%Y-%m-%d %H:%M:%S'`
	sh hbase_balance.sh 
fi
######### check zookeeper can be connected ##############
ifRun=`cat ifRun|grep -v '^#'|awk -F 'ifRun=' '{print $2}'`
if [ $ifRun'a' == "truea" ];then
    echo "  "`date +'%Y-%m-%d %H:%M:%S'`
	sh zk_check.sh  
fi
#######  check if client can connect to cluster ########
ifRun=`cat ifRun|grep -v '^#'|awk -F 'ifRun=' '{print $2}'`
if [ $ifRun'a' == "truea" ];then
    echo "  "`date +'%Y-%m-%d %H:%M:%S'`
	sh clientConToHbaseTest.sh 
fi
#
ifRun=`cat ifRun|grep -v '^#'|awk -F 'ifRun=' '{print $2}'`
if [ $ifRun'a' == "truea" ];then
    echo "  "`date +'%Y-%m-%d %H:%M:%S'`
	sh compact_log.sh 
fi
########### compact all table ##################
#ifRun=`cat ifRun|grep -v '^#'|awk -F 'ifRun=' '{print $2}'`
#if [ $ifRun'a' == "truea" ];then
#    echo "  "`date +'%Y-%m-%d %H:%M:%S'`
#	sh compact.sh
#fi
######### delete HDFS directory and file ####
ifRun=`cat ifRun|grep -v '^#'|awk -F 'ifRun=' '{print $2}'`
if [ $ifRun'a' == "truea" ];then
	    echo "  "`date +'%Y-%m-%d %H:%M:%S'`
	deleteHDFSemptyDirs.sh   /tmp/yujing 4320 
	deleteHDFSfile.sh       /tmp/yujing 4320  
fi
######## delete local dir 
ifRun=`cat ifRun|grep -v '^#'|awk -F 'ifRun=' '{print $2}'`
if [ $ifRun'a' == "truea" ];then
	    echo "  "`date +'%Y-%m-%d %H:%M:%S'`
	sh deleteLocalDirFile.sh   /yujing 4320  
fi
#############  
ifRun=`cat ifRun|grep -v '^#'|awk -F 'ifRun=' '{print $2}'`
if [ $ifRun'a' == "truea" ];then
    echo "  "`date +'%Y-%m-%d %H:%M:%S'`
	sh jstack_monitor.sh  blocked HMaster  
	sh jstack_monitor.sh  blocked DataNode
	sh jstack_monitor.sh  blocked NameNode
fi
################### create table if tommorow is 01 ##############
ifRun=`cat ifRun|grep -v '^#'|awk -F 'ifRun=' '{print $2}'`
if [ $ifRun'a' == "truea" ];then
	date=`date +%d`
	tommorrow=` date -d 'tomorrow' +%d`
	if [ tomorrow'a' == '01a' ];then
	    echo "  "`date +'%Y-%m-%d %H:%M:%S'`
			sh ./ocnosql-tools-V01B05C00/bin/ocnosql_generator.sh DRQUERY_ F 100
	fi
fi
################# snapshot all table #########################
ifRun=`cat ifRun|grep -v '^#'|awk -F 'ifRun=' '{print $2}'`
if [ $ifRun'a' == "truea" ];then
	    echo "  "`date +'%Y-%m-%d %H:%M:%S'`
	sh snapshot.sh
fi
TIME_END=`date +'%s.%N'`
TIME_RUN=`awk 'BEGIN {print '$TIME_END'-'$TIME_BEGIN'}'`
    echo -e "\033[31;40;1m !-----------end daily maintain,time is `date +'%Y-%m-%d %H:%M:%S'`,run time is $TIME_RUN --------!\033[0m"
