#!/bin/sh

if [ $# == 1 ];than
  outputDir=$1
  tempFile=/tmp/ocnosql_clearOutput.temp
  hadoop fs -ls $outputDir | awk -F ' ' '{if($0!="")print $8}' > $tempFile # ���outputĿ¼�µ��ļ�Ŀ¼�б�
fi
