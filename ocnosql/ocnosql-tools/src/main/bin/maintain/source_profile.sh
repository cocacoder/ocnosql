##!/bin/bash
##set -x
#echo "!-----source the envirentment variety-----!"
#if [ `uname` == Linux ];then
#	source ~/.bash_profile
#else if [ `uname` == SunOS ];then
#		source ~/.bash_profile
#     else if [ `uname` == AIX ];then
#		source ~/.bash_profile
#          else if [ `uname` == HP-UX ];then
#		source ~/.bash_profile
#               fi 
#          fi
#     fi
#fi
##echo "!-----end source envirentment variety -----!"
user=`whoami`
HADOOP_HOME=`ps -ef|grep $user|grep namenode|awk '{for(i=1;i<NF;i++) {if($i~"-Dhadoop.home.dir=") print substr($i,19)}}'`
if [ $HADOOP_HOME'a' == 'a' ];then
        HADOOP_HOME=`ps -ef|grep $host|grep datanode|awk '{for(i=1;i<NF;i++) {if($i~"-Dhadoop.home.dir=") print substr($i,19)}}'`
fi
HADOOP_MR1_HOME=`ps -ef|grep $user|grep jobtracker|awk '{for(i=1;i<NF;i++) {if($i~"-Dhadoop.home.dir=") print substr($i,19)}}'`
HBASE_HOME=`ps -ef|grep master|awk '{for(i=1;i<NF;i++) {if($i~"-Dhbase.home.dir=") print substr($i,18)}}'`
