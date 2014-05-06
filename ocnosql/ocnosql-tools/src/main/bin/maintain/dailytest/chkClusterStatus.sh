#!/bin/bash
#set -x
source ~/.bash_profile


user=`whoami`
HADOOP_HOME=`ps -ef|grep $user|grep namenode|awk '{for(i=1;i<NF;i++) {if($i~"-Dhadoop.home.dir=") print substr($i,19)}}'`
if [ $HADOOP_HOME'a' == 'a' ];then
	HADOOP_HOME=`ps -ef|grep $host|grep datanode|awk '{for(i=1;i<NF;i++) {if($i~"-Dhadoop.home.dir=") print substr($i,19)}}'`
fi
HADOOP_MR1_HOME=`ps -ef|grep $user|grep jobtracker|awk '{for(i=1;i<NF;i++) {if($i~"-Dhadoop.home.dir=") print substr($i,19)}}'`
HBASE_HOME=`ps -ef|grep master|awk '{for(i=1;i<NF;i++) {if($i~"-Dhbase.home.dir=") print substr($i,18)}}'`
localhost=`hostname`

##################### check java version ##################################################

java -version > ./java_version.tmp 2>&1
version=`cat java_version.tmp|grep "java version"|awk -F 'java version' '{print $2}'|sed  's/"//g'|awk '{print substr($0,2,3)}'`
if [ `echo "$version < 1.6|bc"` ];then
	echo "java version must be 1.6 at least!"
fi
bit=`cat java_version.tmp|grep "64-Bit"|wc -l`
if [ $bit -lt 1 ];then
	echo "java must be 64 bit!"
fi
CO=`cat java_version.tmp|grep HotSpot|wc -l`
if [ $CO -lt 1 ];then
	echo "company of java must be cat  HotSpot!"
fi
if [ -f java_version.tmp ];then
	rm ./java_version.tmp
fi
###################### check if HADOOP_HOME JAVA_HOME HBASE_HOME is set########################
string_hadoop="$HADOOP_HOME/etc/hadoop/hadoop-env.sh $HADOOP_MR1_HOME/conf/hadoop-env.sh"
for j in $string_hadoop
        do
        cmd_string="HADOOP_HOME"
		for cmd in $cmd_string;do
#                echo $cmd       
                runRemoteCmd.sh "source ~/.bash_profile;cat $j|grep  '$cmd='|grep -v ^#" all|awk '{print $0}'>./cmd_result$cmd
                count1=`cat cmd_result$cmd|wc -l`
		i=1
		while [ $i -lt $count1 ];do
		        host_tmp=`sed -n "$i,1p" cmd_result$cmd`
			result1=`sed -n "$(($i+1)),1p" cmd_result$cmd|awk -F '' '{print $1}'`
	                host=`echo "$host_tmp"|sed 's/*//g'`
			if [ $result1'a' == '*a' ];then
				echo "$cmd is not set in $j of $host"
				i=$(($i+1))
			else if [ $i -le $count1 ];then
				cmd_home=`sed -n "$(($i+1)),1p" cmd_result$cmd|awk '{print $2}'`
	                        if [ $cmd_home'a' == 'a' ];then
	                                echo -e "\033[31;40;1m the "$j" in "$host" does not set "$cmd"\033[0m"
	                        fi
				i=$(($i+2))
			fi
                	fi
		done
                rm ./cmd_result$cmd
	done
done
string_hbase="$HBASE_HOME/conf/hbase-env.sh"
for j in $string_hbase
        do
        cmd_string="HBASE_HOME"
                for cmd in $cmd_string;do
#			echo $cmd       
	                runRemoteCmd.sh "source ~/.bash_profile;cat $j|grep '$cmd='|grep -v ^#" all|awk '{print $0}'>cmd_result$cmd
	                count1=`cat cmd_result$cmd|wc -l`
	                i=1
       	                while [ $i -lt $count1 ];do
    	                    host_tmp=`sed -n "$i,1p" cmd_result$cmd`
    	                    result1=`sed -n "$(($i+1)),1p" cmd_result$cmd|awk -F '' '{print $1}'`
    	                    host=`echo "$host_tmp"|sed 's/*//g'`
    	                    if [ $result1'a' == '*a' ];then
    	                            echo "$cmd is not set in $j of $host"
    	                            i=$(($i+1))
    	                    else if [ $i -le $count1 ];then
    	                            cmd_home=`sed -n "$(($i+1)),1p" cmd_result$cmd|awk '{print $2}'`
    	                            if [ $cmd_home'a' == 'a' ];then
    	                                    echo -e "\033[31;40;1m the "$j" in "$host" does not set "$cmd"\033[0m"
    	                            fi
    	                            i=$(($i+2))
				fi
    	                    fi
    	    	        done
    	            	rm ./cmd_result$cmd
                done
done
################################# check the config file is the same ############################################
localhost=`hostname`
user=`whoami`
string="$HADOOP_HOME/etc/hadoop/hdfs-site.xml $HADOOP_HOME/etc/hadoop/core-site.xml $HADOOP_MR1_HOME/conf/mapred-site.xml $HBASE_HOME/conf/hbase-site.xml $HADOOP_HOME/etc/hadoop/hadoop-env.sh $HADOOP_MR1_HOME/conf/hadoop-env.sh $HBASE_HOME/conf/hbase-env.sh"
for j in $string
	do 
	runRemoteCmd.sh "md5sum $j" all|awk '{print $1}'>tmp1
	count1=`cat tmp1|wc -l`
	md5sum0=`sed -n "2,1p" tmp1`
#	echo $md5sum0
	for((i=2;i<$count1;i=i+2));
		do
			md5sum=`sed -n "$i,1p" tmp1`
			if [ $md5sum'a' != $md5sum0'a' ];then
					tmp=`sed -n "$(($i-1)),1p" tmp1`
					host=`echo "$tmp"|sed 's/*//g'`
					echo -e "\033[31;40;1m the conf "$j" of "$host" is not the same of "$localhost"\n\033[0m"
					scp  $user@$host:$j ./remote_conf
					diff $j ./remote_conf>./diff
					if [ -f diff_$j ];then
						diff=`diff ./diff ./diff_$j`
					else
						cp diff diff_$j
						diff=`cat diff_$j`
					fi
					if [ $diff'a' != 'a' ];then	
						echo -e "the different of the two $host and localhost in $j is \n**********\n"`cat ./diff_$j`"\n*********************\n"
					fi
			fi
		done
	if [ -f ./tmp1 ];then
		rm ./tmp1
	fi
	if [ -f ./diff ];then
		rm ./diff
	fi
	if [ -f ./remote_conf ];then
		rm ./remote_conf
	fi
	done


Xms_cons=1024M
Xmx_cons=4096M
Xmn_cons=128M
####################### check the Xms Xmx Xmn GC setting ##################################################
master=`cat ./deploy.conf|grep master|grep -v '^#'|awk -F',' '{print $1}'`
node=`cat ./deploy.conf|grep regionserver|grep -v '^#'|awk -F',' '{print $1}'`
ps_master="NameNode HMaster"
ps_node="DataNode TaskTracker HRegionServer"
GC="-XX:+UseParNewGC -XX:+UseConcMarkSweepGC"
echo "*************************master*************************************"
for master in $master
do 
	
	echo "*************************$master*************************************"
	for m in $ps_master
	do
		mPid=`ssh $master "jps|grep $m"|awk '{print $1}'`
		ssh $master "ps -ef|grep $mPid|grep -v grep" > ps.tmp
		Xms=`cat ps.tmp|awk '{for ( i=1;i<NF;i++) { if( $i ~"-Xms") {xms=$i}}} END {print substr(xms,5)}'|tr 'a-z' 'A-Z'`
		Xmx=`cat ps.tmp|awk '{for ( i=1;i<NF;i++) { if( $i ~"-Xmx") {xmx=$i}}} END {print substr(xmx,5)}'|tr 'a-z' 'A-Z'`
		#Xmn=`cat ps.tmp|awk '{for ( i=1;i<NF;i++) { if( $i ~"-Xmn") {xmn=$i}}} END {print substr(xmn,5)}'|tr 'a-z' 'A-Z'`
		if [ $Xms'a' != $Xms_cons'a' ];then
			echo -e "\033[31;40;1m Xms setting of $m is $Xmx ,which is not correct,config given is $Xms_cons\033[0m"
		fi
		if [ $Xmx'a' != $Xmx_cons'a' ];then
			echo -e "\033[31;40;1m Xmx setting of $m is $Xmx ,which is not correct,config given is $Xmx_cons\033[0m"
		fi
		#if [ $Xmn'a' != $Xmn_cons'a' ];then
		#	echo -e "\033[31;40;1m Xmn setting of $m is $Xmn ,which is not correct,config given is $Xmn_cons\033[0m"
		#fi

		#for gc in $GC;do
		#	count=`cat ps.tmp|awk -v gc=$gc 'count=0;{for( i=1;i<NF;i++) if( $i ==$gc) {count++}} END {print count}'`
		#	if [ $count'a' == '0a' ];then
		#		echo -e "\033[31;40;1m the GC setting  $gc of $m is null!\033[0m"
		#	fi
		#done
		#count_CMS=`cat ps.tmp|awk 'count=0;{for ( i=1;i<NF;i++) { if( $i ~"-XX:CMSInitiatingOccupancyFraction") {count++}}} END {print count}'`
		#if [ $count_CMS'a' == '0a' ];then
		#	echo -e "\033[31;40;1m GC setting of -XX:CMSInitiatingOccupancyFraction of $m is null!\033[0m"
		#fi		


	done	
done
echo "***********************************node*******************************"
for node in $node
do
	echo "***********************************$node*******************************"
	for m in $ps_node
	do
		mPid=`ssh $node "jps|grep $m"|awk '{print $1}'`
		ssh $node "ps -ef|grep $mPid|grep -v grep" > ps_node.tmp
		Xms=`cat ps_node.tmp|awk '{for ( i=1;i<NF;i++) { if( $i ~"-Xms") {xms=$i}}} END {print substr(xms,5)}'|tr 'a-z' 'A-Z'`
		Xmx=`cat ps_node.tmp|awk '{for ( i=1;i<NF;i++) { if( $i ~"-Xmx") {xmx=$i}}} END {print substr(xmx,5)}'|tr 'a-z' 'A-Z'`
		#Xmn=`cat ps_node.tmp|awk '{for ( i=1;i<NF;i++) { if( $i ~"-Xmn") {xmn=$i}}} END {print substr(xmn,5)}'|tr 'a-z' 'A-Z'`
		if [ $Xms'a' != $Xms_cons'a' ];then
			echo -e "\033[31;40;1m Xms setting of $m is $Xmx ,which is not correct,config given is $Xms_cons\033[0m"
		fi
		if [ $Xmx'a' != $Xmx_cons'a' ];then
			echo -e "\033[31;40;1m Xmx setting of $m is $Xmx ,which is not correct,config given is $Xmx_cons\033[0m"
		fi
	#	if [ $Xmn'a' != $Xmn_cons'a' ];then
	#		echo -e "\033[31;40;1m Xmn setting of $m  is $Xmn ,which is not correct,config given is $Xmn_cons\033[0m"
	#	fi
		if [ $m == "HRegionServer" ];then
			count_NewGC=`cat ps_node.tmp|awk  'count=0;{for(i=1;i<NF;i++) if($i=="-XX:+UseParNewGC" ) {count++}} END {print count}'`
                	count_SweepGC=`cat ps_node.tmp|awk  'count=0;{for(i=1;i<NF;i++) if($i=="-XX:+UseConcMarkSweepGC") {count++}} END {print count}'`
                	count_CMS=`cat ps_node.tmp|awk 'count=0;{for (i=1;i<NF;i++) { if($i ~"-XX:CMSInitiatingOccupancyFraction") {count++}}} END {print count}'`
                	if [ $count_CMS'a' == '0a' ];then
                	        echo -e "\033[31;40;1m the GC setting of -XX:CMSInitiatingOccupancyFraction of $m in $node is null!\033[0m"
                	fi
                	if [ $count_NewGC'a' == '0a' ];then
                	        echo -e "\033[31;40;1m the GC setting  -XX:+UseParNewGC of $m in $node is null!\033[0m"
                	fi
                	if [ $count_SweepGC'a' == '0a' ];then
                	        echo -e "\033[31;40;1m the GC setting  -XX:+UseConcMarkSweepGC of $m in $node is null!\033[0m"
                	fi
		fi
	done	
done
if [ -f ./ps.tmp ];then
	rm ./ps.tmp
fi
if [ -f ./ps_node.tmp ];then
	rm ./ps_node.tmp
fi
