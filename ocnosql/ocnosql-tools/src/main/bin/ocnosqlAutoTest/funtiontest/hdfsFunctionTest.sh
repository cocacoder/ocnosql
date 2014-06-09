#!/bin/sh
#author liuxiang2

hdfs dfs -rm -R /func_test
hdfs dfs -mkdir /func_test
echo "liuxiang test hdfs func"|hdfs dfs -put - /func_test/file_put.txt
put_data=`hdfs dfs -cat  /func_test/file_put.txt|grep "liuxiang test"|wc -l`
if [ $put_data -ne 1 ] ; then
	echo "!!!!!ERROR!!!!!********put file to hdfs error********************"
else
	echo "*************hdfs put ok****************"
fi
hdfs dfs -mv /func_test/file_put.txt /func_test/file_mv.txt
mv1=`hdfs dfs -ls -R /func_test|grep file_put|wc -l`
mv2=`hdfs dfs -ls -R /func_test|grep file_mv|wc -l`
if [ $mv1 -eq 1 ] ; then
	echo "!!!!!ERROR!!!!!********hdfs mv failed*********************"
elif [ $mv1 -eq 0 ] ; then
	if [ $mv2 -eq 1  ] ; then
		echo "*************hdfs mv ok*****************"
	else
		echo "!!!!!ERROR!!!!!********hdfs mv failed*********************"
	fi
else
	echo "!!!!!ERROR!!!!!********hdfs  mv failed******************"
fi

echo "liuxiang test hdfs func"|hdfs dfs -put - /func_test/file_rm.txt
rm_data=`hdfs dfs -rm -R /func_test/file_rm.txt|grep Deleted|grep file_rm|wc -l`
if [ $rm_data -ne 1 ] ; then
	echo "!!!!!ERROR!!!!!********rm hdfs file error********************"
else
	echo "*************hdfs rm ok****************"
fi
hdfs dfs -copyToLocal /func_test/file_mv.txt ./
get_data=`cat file_mv.txt|grep liuxiang|wc -l`
if [ $get_data -ne 1 ] ; then
	echo "!!!!!ERROR!!!!!********get hdfs file error********************"
else
	echo "*************hdfs get ok****************"
fi
rm ./file_mv.txt
