#!/bin/bash
########
#
# import.sh
# 
# Imports the given input directory of TSV data into the specified table
# You can change 7 arguments to control the way of importing data you need.
#	1.tablename 
#	2.inputPath
#	3.outputPath
#   4.jarPath
#   5.seperator 
#   6.type	
#   7.columns
#
# For example: ./import.sh tableA /test/input/.All data will be /test/input/ directory under 
# the data imported into the table tableA
#
#
#########

# table name
tablename=$1
# input path
inputPath=$2
# output path
outputPath=/cdh4test/output/
# jar path
jarPath=/data1/zhuangyang/hbase-0.94.2-cdh4.2.0.jar
# seperator
seperator=";"
# import type ,singleimporttsv or mutipleimporttsv
type=mutipleimporttsv
# columns
columns=f:a0,f:a1,f:a2,f:a3,f:a4,f:a5,f:a6,HBASE_ROW_KEY,f:a8,f:a9,f:a10,f:a11,f:a12,f:a13,f:a14,f:a15,f:a16,f:a17,f:a18

hadoop jar ${jarPath} ${type} -Dimporttsv.separator="${seperator}" -Dimporttsv.bulk.output=${outputPath}${tablename} -Dimporttsv.columns=${columns} ${tablename} ${inputPath}>>./${tablename}.log 2>&1;hadoop jar ${jarPath} completebulkload ${outputPath}${tablename} ${tablename} >> ./${tablename}.log 2>&1