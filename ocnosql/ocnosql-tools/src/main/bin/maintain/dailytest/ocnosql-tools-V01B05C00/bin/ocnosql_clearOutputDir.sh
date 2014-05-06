#!/bin/sh

if [ $# == 1 ];than
  outputDir=$1
  tempFile=/tmp/ocnosql_clearOutput.temp
  hadoop fs -ls $outputDir | awk -F ' ' '{if($0!="")print $8}' > $tempFile # 获得output目录下的文件目录列表
fi
