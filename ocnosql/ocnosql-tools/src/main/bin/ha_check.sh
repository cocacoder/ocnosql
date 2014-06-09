#!/bin/bash
#HRegionServer & ZKFC HA
#set -x
#sh source_profile.sh
if [ ! -d /tmp/log ];then
  mkdir /tmp/log
fi
HDFS_HOME=/home_app/ocnosql/app/hadoop
HBASE_HOME=/home_app/ocnosql/app/hbase
HADOOP_HOME=/home_app/ocnosql/app/hadoop-mr1
HMC_HOME=/home_app/ocnosql/app/hmc/agent
#echo " ">/tmp/log/DFSZKFailoverController-check-online.log
#echo " ">/tmp/log/HRegionServer-check-online.log

check_hdfs() {
	processName=$1
	jps  | grep -i $processName | grep -v grep 1>& /dev/null
	if [ $? == 0 ]
	then
	    echo "!------- $1 is ok-------------!" 
	else
	        echo "!-------$1 dead ----------!" | tee -a /tmp/log/$1-check-online.log
	        echo "Check date:`date '+%Y-%m-%d %H:%M:%S'`" | tee -a  /tmp/log/$1-check-online.log
	        echo "!-------$1 Restart ----------!" | tee -a /tmp/log/$1-check-online.log
	        # Restart
	        cd $HDFS_HOME/sbin
	        ./hadoop-daemon.sh start $1 
	        echo "!-------$1 OK     -----------!" | tee -a /tmp/log/$1-check-online.log
	fi 
}

check_mr() {
	processName=$1
	jps  | grep -i $processName | grep -v grep 1>& /dev/null
	if [ $? == 0 ]
	then
	    echo "!------- $1 is ok-------------!" 
	else
	        echo "!-------$1 dead ----------!" | tee -a /tmp/log/$1-check-online.log
	        echo "Check date:`date '+%Y-%m-%d %H:%M:%S'`" | tee -a  /tmp/log/$1-check-online.log
	        echo "!-------$1 Restart ----------!" | tee -a /tmp/log/$1-check-online.log
	        # Restart
	        cd $HADOOP_HOME/bin
	        ./hadoop-daemon.sh start $1 
	        echo "!-------$1 OK     -----------!" | tee -a /tmp/log/$1-check-online.log
	fi 
}

check_hbase() {
	processName=$1
	jps  | grep -i $processName | grep -v grep 1>& /dev/null
	if [ $? == 0 ]
	then
	    echo "!------- $1 is ok-------------!" 
	else
	        echo "!-------$1 dead ----------!" | tee -a /tmp/log/$1-check-online.log
	        echo "Check date:`date '+%Y-%m-%d %H:%M:%S'`" | tee -a  /tmp/log/$1-check-online.log
	        echo "!-------$1 Restart ----------!" | tee -a /tmp/log/$1-check-online.log
	        # Restart
	        cd $HBASE_HOME/bin
	        ./hbase-daemon.sh start $1 
	        echo "!-------$1 OK     -----------!" | tee -a /tmp/log/$1-check-online.log
	fi 
}
check_hmc() {
	processName=$1
	jps  | grep -i $processName | grep -v grep 1>& /dev/null
	if [ $? == 0 ]
	then
	    echo "!------- $1 is ok-------------!" 
	else
	        echo "!-------$1 dead ----------!" | tee -a /tmp/log/$1-check-online.log
	        echo "Check date:`date '+%Y-%m-%d %H:%M:%S'`" | tee -a  /tmp/log/$1-check-online.log
	        echo "!-------$1 Restart ----------!" | tee -a /tmp/log/$1-check-online.log
	        # Restart
	        cd $HMC_HOME/bin
		./hq-agent.sh start
	        echo "!-------$1 OK     -----------!" | tee -a /tmp/log/$1-check-online.log
	fi 
}

check_zookeeper() {
	jps  | grep -w QuorumPeerMain | grep -v grep 1>& /dev/null
	if [ $? == 0 ]
	 then
	    echo "!-------QuorumPeerMain is ok-------------!" 
	else
	        echo "!-------QuorumPeerMain is dead ----------!" | tee -a /tmp/log/QuorumPeerMain-check-online.log
	        echo "Check date:`date '+%Y-%m-%d %H:%M:%S'`" | tee -a  /tmp/log/QuorumPeerMain-check-online.log
	        echo "!-------QuorumPeerMain  Restart----------!" | tee -a /tmp/log/QuorumPeerMain-check-online.log
	        # Restart
	        cd $ZOOKEEPER_HOME/bin
	        ./zkServer.sh start
	        echo "!-------QuorumPeerMain  OK    -----------!" | tee -a /tmp/log/QuorumPeerMain-check-online.log
	fi
}

hostname=`hostname`

if [ "xfxwmage" == ${hostname:0:8} ] ; then
	check_hdfs zkfc	
	check_hmc WrapperStartStopApp
elif [ "xfxwdnod" == ${hostname:0:8} ] ; then
	check_hdfs datanode
	check_hbase regionserver	
	check_mr tasktracker
	check_hmc WrapperStartStopApp
#elif [ ${hostname} in ( "xfxwdnod1" "xfxwdnod2" "xfxwdnod3" "xfxwdnod4" "xfxwdnod5" ) ] ; then
#	check_hbase master
fi

