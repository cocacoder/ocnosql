#!/bin/bash
#set -x
#IP_Addr="10.9.131.156"
#filePath=/home/ocnosql/ocnosql/log/hbase
#destPath=/home/ocnosql/
if [ 'a'$1'a' == 'aa' ];then
	interval=10
else
	interval=$1
fi
#bin=`dirname "$0"`
#bin=`cd $bin;pwd`
#cd $bin
#ifRun=`cat ifRun|grep -v '^#'|awk -F 'ifRun=' '{print $2}'`
#echo $ifRun
#if [ $ifRun'a' == "truea" ];then
sh source_profile.sh
TIME_BEGIN=`date +'%s.%N'`
echo "!-------- begin to do hbase hbck.begin time is `date +'%Y-%m-%d %H:%M:%S'` ----------!"
   hbase hbck > hbase_hbck.tmp 2>&1 
#----------------------------get all monitor stuff ---------------------------------------   
   incons=`cat hbase_hbck.tmp |grep "inconsistencies detected"|awk '{print $1}'`
   hStatus=`cat hbase_hbck.tmp |grep "Status:"|awk '{print $2}'`
   tbl_num=`cat hbase_hbck.tmp |grep "Number of Tables:"|awk '{print $4}'`
   rs_num=`cat hbase_hbck.tmp |grep "Number of live region servers:"|awk '{print $6}'`
   dead_rs=`cat hbase_hbck.tmp |grep "Number of dead region servers:"|awk '{print $6}'`
   master=`cat hbase_hbck.tmp |grep "Master:"|awk '{print $2}'|awk -F ',' '{print $1}'`
   backup_master=`cat hbase_hbck.tmp |grep "Number of backup masters:"|awk '{print $5}'`
#---------------------------check master status ------------------------------------------- 
   if [ -f ./former_master ];then
    former_master=`cat ./former_master`
    if [ $former_master'a' != $master'a' ];then
      echo -e "\033[31;40;1m master has transfered,the master is "$master "right now!\033[0m"
      echo $master>./former_master
    fi
   else
    echo $master>./former_master
   fi
#---------------------------check whether there are inconsistencies -----------------------
   if [ $incons'a' !=  "0a"  ]
    then
    echo -e "\033[31,40,1m hbase service is unnormal!$incons inconsistencies detected !\033[0m"
   fi
   if [ $hStatus'a' != 'OKa' ]
   then
   echo "hbase service is unnormal!;hbase hbck stauts ="$hStatus
   fi
#--------------------------check whether there is dead regionserver ----------------------
   if [ $dead_rs'a' != "0a" ]
    then
        echo -e "\033[31;40;1m there are "$dead_rs" regionservers is dead!\033[0m"
   fi
 
  rm ./hbase_hbck.tmp

#----------------------------check client number of connecting to hbase ----------------!
rsString=`cat deploy.conf|grep -v '^#'|grep ',regionserver,'|awk -F',' '{print $1}'`
cat deploy.conf|grep -v '^#'|grep ',regionserver,'|awk -F',' '{print $1}'>rs.tmp
rsString1=(`echo $(<rs.tmp)`)
rm rs.tmp
hbClientCount=0
maxClientCount=0
for rs in $rsString
  do
           rsClientCount=`ssh $rs "netstat|grep 60327|grep ESTABLISHED|wc -l"`
           hbClientCount=`expr $hbClientCount + $rsClientCount`
  done
if [ $hbClientCount -gt $maxClientCount ];then
           echo -e "\033[31;40;1m the count of the client connecting to hbase is $hbClientCount,more than $maxClientCount!\033[0m"
fi

TIME_END=`date +'%s.%N'`
TIME_RUN=`awk 'BEGIN {print '$TIME_END'-'$TIME_BEGIN'}'`
echo "!--------end hbase hbck.end time is `date +'%Y-%m-%d %H:%M:%S'`,run time is $TIME_RUN -----------!"
#sleep $interval
#echo "Number of Tables:"$tbl_num" regionserver number is "$rs_num",master is "$master",backup_master number is "$backup_master
