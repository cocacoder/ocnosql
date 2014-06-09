#!/bin/bash
#NameNode HA
#ben@ailk
#set -x
#app/hadoop/sbin/hadoop-daemon.sh start namenode
#app/hadoop/sbin/hadoop-daemon.sh start zkfc
#app/hadoop-mr1/bin/hadoop-daemon.sh start jobtrackerha
#app/hadoop-mr1/bin/hadoop-daemon.sh start mrzkfc
#sh source_profile.sh
export HADOOP_MR1_HOME=/home/ocnosql/cdh43/hadoop-2.0.0-mr1-cdh4.2.1
if [ ! -d /tmp/log ];then
  mkdir /tmp/log
fi
echo " ">/tmp/log/namenode-check-online.log
echo " ">/tmp/log/JobTrackerHADaemon-check-online.log
echo " ">/tmp/log/DFSZKFailoverController-check-online.log
echo " ">/tmp/log/MRZKFailoverController-check-online.log
echo " ">/tmp/log/HMaster-check-online.log
echo " ">/tmp/log/QuorumPeer-check-online.log
jps  | grep -w NameNode | grep -v grep 1>& /dev/null 
if [ $? == 0 ] 
 then
    echo "!-------NameNode is ok--------------------------!" |tee -a /tmp/log/namenode-check-online.log 
else    
	echo "!-------NameNode is dead ----------!" | tee -a /tmp/log/namenode-check-online.log   
	echo "Check date:`date '+%Y-%m-%d %H:%M:%S'`" | tee -a  /tmp/log/namenode-check-online.log    
	echo "!-------NameNode  Restart----------!" | tee -a /tmp/log/namenode-check-online.log   
	# Restart
	cd $HADOOP_HOME/sbin
	./hadoop-daemon.sh start namenode
	echo "!-------NameNode  OK    -----------!" | tee -a /tmp/log/namenode-check-online.log 
fi


#JobTrackerHADaemon
jps  | grep -w JobTrackerHADaemon | grep -v grep 1>& /dev/null 
if [ $? == 0 ] 
then
    echo "!-------JobTrackerHADaemon is ok----------------!" |tee -a /tmp/log/JobTrackerHADaemon-check-online.log    
else
	echo "!-------JobTrackerHADaemon is dead ----------!" | tee -a /tmp/log/JobTrackerHADaemon-check-online.log   
	echo "Check date:`date '+%Y-%m-%d %H:%M:%S'`" | tee -a  /tmp/log/JobTrackerHADaemon-check-online.log    
	echo "!-------JobTrackerHADaemon Restart ----------!" | tee -a /tmp/log/JobTrackerHADaemon-check-online.log   
	# Restart
	cd $HADOOP_MR1_HOME/bin
	./hadoop-daemon.sh start jobtrackerha
	echo "!-------JobTrackerHADaemon OK     -----------!" | tee -a /tmp/log/JobTrackerHADaemon-check-online.log 
fi

jps  | grep -w DFSZKFailoverController | grep -v grep 1>& /dev/null
if [ $? == 0 ]
then
    echo "!-------DFSZKFailoverController is ok-----------!" |tee -a /tmp/log/DFSZKFailoverController-check-online.log 
else
        echo "!-------DFSZKFailoverController is dead ----------!" | tee -a /tmp/log/DFSZKFailoverController-check-online.log
        echo "Check date:`date '+%Y-%m-%d %H:%M:%S'`" | tee -a  /tmp/log/DFSZKFailoverController-check-online.log
        echo "!-------DFSZKFailoverController Restart ----------!" | tee -a /tmp/log/DFSZKFailoverController-check-online.log
        # Restart
        cd $HADOOP_HOME/sbin
        ./hadoop-daemon.sh start zkfc
        echo "!-------DFSZKFailoverController OK     -----------!" | tee -a /tmp/log/DFSZKFailoverController-check-online.log
fi 

#MRZKFailoverController
jps  | grep -w MRZKFailoverController| grep -v grep 1>& /dev/null 
if [ $? == 0 ] 
then
    echo "!-------MRZKFailoverController is ok------------!" |tee -a /tmp/log/MRZKFailoverController-check-online.log 
else    
        echo "!-------MRZKFailoverController is dead ----------!" | tee -a /tmp/log/MRZKFailoverController-check-online.log   
        echo "Check date:`date '+%Y-%m-%d %H:%M:%S'`" | tee -a  /tmp/log/MRZKFailoverController-check-online.log    
        echo "!-------MRZKFailoverController Restart----------!" | tee -a /tmp/log/MRZKFailoverController-check-online.log   
        # Restart
        cd $HADOOP_MR1_HOME/bin 
        ./hadoop-daemon.sh start mrzkfc
        echo "!-------MRZKFailoverController OK    -----------!" | tee -a /tmp/log/MRZKFailoverController-check-online.log 
fi

#HMaster
jps  | grep -w HMaster| grep -v grep 1>& /dev/null
if [ $? == 0 ]
then
    echo "!-------HMaster is ok---------------------------!" |tee -a /tmp/log/HMaster-check-online.log
else
        echo "!-------HMaster is dead ----------!" | tee -a /tmp/log/HMaster-check-online.log
        echo "Check date:`date '+%Y-%m-%d %H:%M:%S'`" | tee -a  /tmp/log/HMaster-check-online.log
        echo "!-------HMaster Restart----------!" | tee -a /tmp/log/HMaster-check-online.log
        # Restart
        #source /home/ocnoql/.bash_profile
        cd $HBASE_HOME/bin/
        ./hbase-daemon.sh start master
	echo "!-------HMaster OK    -----------!" | tee -a /tmp/log/HMaster-check-online.log
fi
jps  | grep -w QuorumPeerMain | grep -v grep 1>& /dev/null 
if [ $? == 0 ] 
 then
    echo "!-------QuorumPeerMain is ok--------------------!" |tee -a /tmp/log/QuorumPeerMain-check-online.log 
else    
	echo "!-------QuorumPeerMain is dead ----------!" | tee -a /tmp/log/QuorumPeerMain-check-online.log   
	echo "Check date:`date '+%Y-%m-%d %H:%M:%S'`" | tee -a  /tmp/log/QuorumPeerMain-check-online.log    
	echo "!-------QuorumPeerMain  Restart----------!" | tee -a /tmp/log/QuorumPeerMain-check-online.log   
	# Restart
	cd $ZOOKEEPER_HOME/bin
	./zkServer.sh start
	echo "!-------QuorumPeerMain  OK    -----------!" | tee -a /tmp/log/QuorumPeerMain-check-online.log 
fi
