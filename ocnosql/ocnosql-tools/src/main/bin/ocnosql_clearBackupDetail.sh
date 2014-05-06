#!/bin/sh
#set -x
## clear original detail record
if [ $# == 2 ];then
  path=$1
  day=$2
  tempFile=/tmp/ocnosql_clearDetail.tmp
  
  if [ `expr match "$path" "^/*"` -lt 1 ];then
    echo "Error: param $path must start with '/'."
    exit 1
  fi
  if [ `expr match "$day" "[0-9]*$"` -lt 1 ];then
    echo "Error: param $day is not number."
    exit 1
  fi
  
  delDate=`date --date="-${day} day" +%Y%m%d` # 删除的截止日期
  hadoop fs -ls $path | grep -v "Cannot access" | grep -v "Found" | awk -F ' ' '{if($0!="")print $6,$8}' > $tempFile  #遍历path目录下的全部文件
  
  while read line
  do
    createDate=`echo "$line" | awk -F ' ' '{print $1}'`
    filePath=`echo "$line" | awk -F ' ' '{print $2}'`
    createDate=`date --date="$createDate" +%Y%m%d`
    if [ $createDate -le $delDate ];then
      echo "hadoop fs -rm $filePath"
    fi
  done < $tempFile

  rm -r $tempFile
  exit 1
fi

echo "Usage: ./ocnosql_clearBackupDetail.sh <path> <dayNumber>"
echo "Description: ocnosql_clearBackupDetail.sh used to remove the original deatil file which specified by <path> before <day> days"
