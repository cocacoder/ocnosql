#!/bin/bash
#set -x
#sh source_profile.sh
TIME_BEGIN=`date +'%s.%N'`
echo "!-------- begin to do zookeeper check ,begin time is $TIME_BEGIN---------!"
count=0
#rsString=`cat deploy.conf|grep -v '^#'|grep ',regionserver,'|awk -F',' '{print $1}'`
#cat deploy.conf|grep -v '^#'|grep ',regionserver,'|awk -F',' '{print $1}'>rs.tmp
#rsString1=(`echo $(<rs.tmp)`)
#rm rs.tmp
#hbClientCount=0
#maxClientCount=0
zkString=`cat deploy.conf|grep -v '^#'|grep ',zookeeper,'|awk -F',' '{print $1}'`
cat deploy.conf|grep -v '^#'|grep ',zookeeper,'|awk -F',' '{print $1}'>zk.tmp
zkString1=(`echo $(<zk.tmp)`)
rm zk.tmp
for zk in $zkString 
  do
	#ruok=`echo ruok|nc $zk 2485`
	#ruok7=`echo ruok | nc ocdata07 2485`
	#ruok9=`echo ruok | nc ocdata09 2485`
	echo "ls /hbase/rs"|hbase zkcli -server ${zk}:2485 > ./zk_info.tmp 2>&1
	ruok=`cat zk_info.tmp|grep zk|grep CONNECTED|wc -l`
	if [ $ruok -lt 1 ];then
	   echo -e "\033[31;40;1m zookeeper cluster is unnormal! zk "$zk" is not running\033[0m"
	   count=$(($count+1))
	else
	   zk1=$zk
	fi
	rm ./zk_info.tmp
  done
#for rs in $rsString	
#  do
# 	   rsClientCount=`ssh $rs "netstat|grep 60327|wc -l"`
#	   hbClientCount=`expr $hbClientCount + $rsClientCount`
#  done
#	   if [ $hbClientCount -gt $maxClientCount ];then
#		echo -e "\033[31;40;1m the count of the client connecting to hbase is more than 300!\033[0m"
#	   fi
#*************check whether rs is ok*************************************
zkLen=${#zkString1[*]};
#rsLen=${#rsString1[*]};
#if [ $count -le $zkLen ];then
#  echo "ls /hbase/rs"|hbase zkcli -server ${zk1}:2485 > ./zk_live_info.tmp 2>&1
#  rs_list=(`cat zk_live_info.tmp|grep zk|awk -F']' '{if(NR>1) print substr($2,3,length($2)-1)}'|awk -F',' '{printf $1","$4","$7}'|awk -F', ' '{print $1" "$2" "$3}'`)
#  rs_listLen=${#rs_list[*]};
#  if [ $rs_listLen -ne $rsLen ];then
#	echo -e "\033[31;40;1m regionserver is unnormal,rs list should be "$rsString1" but now is "$rs_list" now,please check cluster right now\033[0m"
#  fi
#  rm ./zk_live_info.tmp
#else
if [ $count -ge $zkLen ];then
   echo "all the zookeeper is dead!"
fi
TIME_END=`date +'%s.%N'`
TIME_RUN=`awk 'BEGIN {print '$TIME_END'-'$TIME_BEGIN'}'`
echo "!-------------- end zookeeper check ,end time is $TIME_END,run time is $TIME_RUN--------------------!"
