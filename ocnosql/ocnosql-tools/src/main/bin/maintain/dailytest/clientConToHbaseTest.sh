#!/bin/bash
#set -x
lib=~/cdh43/hadoop-2.0.0-mr1-cdh4.2.1/conf:~/cdh43/hbase-0.94.6-cdh4.3.0/conf
for i in /home/ocnosql/cdh43/hadoop-2.0.0-mr1-cdh4.2.1/lib/*.jar;
	do 
		lib=$lib:$i
	done
for i in /home/ocnosql/cdh43/hadoop-2.0.0-cdh4.3.0/lib/*.jar;
	do 
		lib=$lib:$i
	done
for i in /home/ocnosql/cdh43/hadoop-2.0.0-mr1-cdh4.2.1/*.jar;
	do 
		lib=$lib:$i
	done
for i in /home/ocnosql/cdh43/hadoop-2.0.0-cdh4.3.0/*.jar;
	do 
		lib=$lib:$i
	done
for i in /home/ocnosql/cdh43/hbase-0.94.6-cdh4.3.0/*.jar;
	do 
		lib=$lib:$i
	done
lib=$lib:/home/ocnosql/yj/HbaseConnectTest.jar
#echo $lib
java -cp $lib com.ailk.oci.ocnosql.client.config.spi.HbaseConnectTest>list.log.tmp 2>&1
count=`cat list.log.tmp|grep "list table is success"|wc -l`
if [ $count -eq 0 ];then
	echo "client can not connect to hbase,please check right now!"
	rm ./list.log.tmp
else
	rm ./list.log.tmp
fi
