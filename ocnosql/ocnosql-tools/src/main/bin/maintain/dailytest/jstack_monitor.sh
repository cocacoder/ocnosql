#set -x
if [ $# -lt 1 ]
   then
   echo "usage: sh jstat_monitor.sh <param1> <param2>
         status:like blocked GC etc 
         process:like datanode,master,regionserver,namenode,tasktracker"
fi
echo "!------begin jstack monitor,begin time is `date +'%Y-%m-%d %H:%M:%S'`---------!"
cmd=$1
process=$2
IP_Addr=`cat deploy.conf|grep -v '^#'|grep "$process"|awk -F',' '{print $1}'`
fileName=${process}_${cmd}_monitor.log.`date -u +%Y%m%d-%H:%M`
for value in ${IP_Addr} 
 do
        count=0
	cnt=0
	if [ $count -lt 3 ];then
		ssh $value "source ~/.bash_profile; jps | grep $process | awk  '{print \$1}' | xargs jstack" >> ${process}-$cmd-$value.log
        	real_cnt=`cat ${process}-$cmd-${value}.log|grep -i $cmd|wc -l`
		cnt=$(($cnt+$real_cnt))
		count=$(($count+1))
		sleep 1
	fi
        if [ $cnt -gt 0 ] ; then
                Tdate=` date +"%Y-%m-%d %H:%M:%S" `
                echo $Tdate";"$IP_Addr"; "$process" is unnormal!The "$cmd" result is print to "$fileName.$value
                cat ${process}-$cmd-${value}.log|grep -i $cmd >> $fileName.$value
        fi
        rm  $process-$cmd-$value.log
done
echo "!------end jstack monitor,end time is `date +'%s'` --------!"
#	sleep 3600
