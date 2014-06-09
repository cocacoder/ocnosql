#!/bin/bash
#HRegionServer & ZKFC HA
#ben@ailk
#set -x
#sh source_profile.sh
if [ ! -d /tmp/log ];then
  mkdir /tmp/log
fi
HDFS_HOME=/home_app/ocnosql/app/hadoop
HBASE_HOME=/home_app/ocnosql/app/hbase
HADOOP_HOME=/home_app/ocnosql/app/hadoop-mr1
#echo " ">/tmp/log/DFSZKFailoverController-check-online.log
#echo " ">/tmp/log/HRegionServer-check-online.log

check_zkfc() {
	jps  | grep -w DFSZKFailoverController | grep -v grep 1>& /dev/null
	if [ $? == 0 ]
	then
	    #echo "!-------DFSZKFailoverController is ok-------------!" |tee -a /tmp/log/DFSZKFailoverController-check-online.log 
	    echo "!-------DFSZKFailoverController is ok-------------!" 
	else
	        echo "!-------DFSZKFailoverController is dead ----------!" | tee -a /tmp/log/DFSZKFailoverController-check-online.log
	        echo "Check date:`date '+%Y-%m-%d %H:%M:%S'`" | tee -a  /tmp/log/DFSZKFailoverController-check-online.log
	        echo "!-------DFSZKFailoverController Restart ----------!" | tee -a /tmp/log/DFSZKFailoverController-check-online.log
	        # Restart
	        cd $HDFS_HOME/sbin
	        ./hadoop-daemon.sh start zkfc
	        echo "!-------DFSZKFailoverController OK     -----------!" | tee -a /tmp/log/DFSZKFailoverController-check-online.log
	fi 
}
#HRegionServer
check_regionserver() {
	jps  | grep -w HRegionServer| grep -v grep 1>& /dev/null
	if [ $? == 0 ]
	then
	    #echo "!-------HRegionServer is ok-------------!" |tee -a /tmp/log/HRegionServer-check-online.log
	    echo "!-------HRegionServer is ok-------------!"
	else
	        echo "!-------HRegionServer is dead ----------!" | tee -a /tmp/log/HRegionServer-check-online.log
	        echo "Check date:`date '+%Y-%m-%d %H:%M:%S'`" | tee -a  /tmp/log/HRegionServer-check-online.log
	        echo "!-------HRegionServer Restart----------!" | tee -a /tmp/log/HRegionServer-check-online.log
	        # Restart
	        #source /home/ocnoql/.bash_profile
	        cd $HBASE_HOME/bin/
	        ./hbase-daemon.sh start regionserver
		echo "!-------HRegionServer OK    -----------!" | tee -a /tmp/log/HRegionServer-check-online.log
	fi
}

hostname=`hostname`

if [ "xfxwmage" == ${hostname:0:8} ] ; then
	check_zkfc
elif [ "xfxwdnod" == ${hostname:0:8} ] ; then
	check_regionserver	
fi
