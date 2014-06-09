#!/bin/bash
#set -x
if [ $# -lt 2 ];then
  echo "usage:$0 dirName intervalTime(minutes)"
  exit 0
fi
dir=$1
interval=$2
TIME_BEGIN=`date +'%s.%N'`
echo "!------- begin delete local directory $dir which is modified  $interval minutes ago,begin time is `date +'%Y-%m-%d %H:%M:%S'`---------!"
#source ~/.bash_profile
currentTime=`date +'%s'`
ls -lrt $dir|grep '^-'>/tmp/file.$$.$currentTime
ls -lrt $dir|grep '^d'>/tmp/dir.$$.$currentTime
fileCount=`cat /tmp/file.$$.$currentTime|wc -l`
dirCount=`cat /tmp/dir.$$.$currentTime|wc -l`
#******************delete the file which is longer than expected ****************
if [ $fileCount -ne 0 ];then
  while read line
   do
	fileName=`echo $line|awk '{print $9}'`
	fileTime=`echo $line|awk '{print $6" "$7" "$8":00"}'`
	timeDiff=$(($currentTime-$(date -d "$fileTime" +%s)))
	intervalSec=$(($interval*60))
	if [ $intervalSec -lt $timeDiff ];then
           rm $dir/$fileName
	    echo "begin to rm file "$fileName
	fi
   done </tmp/file.$$.$currentTime
fi

#*******************delete the directory which is longer than expected **********
if [ $dirCount -ne 0 ];then
   while read line
      do
	dirName=`echo $line|awk '{print $9}'`
	dirTime=`echo $line|awk '{print $6" "$7" "$8":00"}'`
	timeDiff=$(($currentTime-$(date -d "$dirTime" +%s)))
	intervalSec=$(($interval*60))
	if [ $intervalSec -lt $timeDiff ];then
	    echo "begin to delete dir "$dirName
	    rm -r $dir/$dirName
	fi
      done </tmp/dir.$$.$currentTime
fi
TIME_END=`date +'%s.%N'`
TIME_RUN=`awk 'BEGIN {print '$TIME_END'-'$TIME_BEGIN'}'`
echo "!----- end delete directory and files of Local directory $dir ,run time is $TIME_RUN ------!"
