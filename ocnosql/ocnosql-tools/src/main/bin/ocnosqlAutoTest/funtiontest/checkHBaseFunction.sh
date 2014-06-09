#!/bin/sh
#for check the functional of hbase opearation
file_name=lx_test.log
table_name=auto_func_test
date=`date +"20%y%m%d%H%M%S"`
report_file=./report/report_$date

 > $file_name

hbase shell  hbaseFuncthionTest.sh > $file_name 

create_table=`cat $file_name |grep $table_name|wc -l`
put_data=`cat $file_name |grep "0 row(s) in"|wc -l`
describe_table=`cat $file_name |grep "DESCRIPTION  ENABLED"|wc -l`
scan_data=`cat $file_name |grep "column=f:"|wc -l`
regioninfo=`cat $file_name |grep "regioninfo"|grep $table_name|wc -l`
table_count=`cat $file_name |grep "22 row(s) in"|wc -l`

if [ $create_table -lt 1 ] ; then
#    echo "!!!!!!!!!!!ERROR!!!!!!!***********CAN NOT CREATE TABLE**************" >> $report_file
    echo "!!!!!!!!!!!ERROR!!!!!!!***********CAN NOT CREATE TABLE**************" 
else
    echo "***********************HBASE CREATE TABLE OK***************************"
fi

if [ $put_data -lt 572 ] ; then
#    echo "!!!!!!!!!!!ERROR!!!!!!!***********put data error, number of $put_data have been put**************" >> $report_file
    echo "!!!!!!!!!!!ERROR!!!!!!!***********put data error, number of $put_data have been put**************" 
else
    echo "***********************HBASE PUT DATA OK***************************"
fi

if [ $describe_table -ne 1 ] ; then
    echo "describe_table = $describe_table"
#    echo "!!!!!!!!!!!ERROR!!!!!!!***********CAN NOT DESCRIBE TABLE**************" >> $report_file
    echo "!!!!!!!!!!!ERROR!!!!!!!***********CAN NOT DESCRIBE TABLE**************" 
else
    echo "***********************HBASE DESCRIBE TABLE OK***************************"
fi

if [ $scan_data -lt 260 ] ; then
    echo "scan_data = $scan_data"
#    echo "!!!!!!!!!!!ERROR!!!!!!!***********CAN NOT SCAN TABLE**************" >> $report_file
    echo "!!!!!!!!!!!ERROR!!!!!!!***********CAN NOT SCAN TABLE**************" 
else
    echo "***********************HBASE SCAN TABLE OK***************************"
fi

if [ $regioninfo -ne 3 ] ; then
    echo "regioninfo = $regioninfo"
#    echo "!!!!!!!!!!!ERROR!!!!!!!***********SPLIT REGION ERROR**************" >> $report_file
    echo "!!!!!!!!!!!ERROR!!!!!!!***********SPLIT REGION ERROR**************" 
else
    echo "***********************HBASE REGION SPLIT OK***************************"
fi

if [ $table_count -lt 1 ] ; then
    echo "table_count = $table_count"
#    echo "!!!!!!!!!!!ERROR!!!!!!!***********ROWS COUNT ERROR**************" >> $report_file
    echo "!!!!!!!!!!!ERROR!!!!!!!***********ROWS COUNT ERROR**************" 
else
    echo "***********************HBASE TABLE COUNT OK***************************"
fi
rm $file_name
