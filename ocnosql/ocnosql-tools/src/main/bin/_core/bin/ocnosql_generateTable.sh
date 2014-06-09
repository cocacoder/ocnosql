#!/bin/sh
########
#
# generateTable.sh
#
#   Automatic generate table of next month
#   You can change 3 argument to control the way of generate table you need.
#	1.table_prefix 
#	2.columnName
#	3.regionNum 	
#
#   For example: If you execute this script, this script will use system date
# to generate table in pattern like <[table_prefix][yyyyMMdd]> from the 
# beginning to the end of next month.eg. ./generateTable.sh dr_query f 10
#
#########

. ~/.bash_profile

table_prefix=$1
columnName=$2
regionNum=$3

date=`date -d "+1 month" +%Y%m`"01"
nextdate=`date -d "+2 month" +%Y%m`"01"

OCNOSQL_TOOL_HOME=`cd ..;pwd`
echo OCNOSQL_TOOL_HOME=$OCNOSQL_TOOL_HOME
export OCNOSQL_TOOL_HOME
for f in $OCNOSQL_TOOL_HOME/lib/*.jar; 
do
        CLASSPATH=${CLASSPATH}:$f;
done
CLASSPATH=$OCNOSQL_TOOL_HOME/conf:${CLASSPATH}
export CLASSPATH

while [ $date -lt $nextdate ] 
do
        tableName="$table_prefix$date"
        java -cp $CLASSPATH com.ailk.oci.ocnosql.tools.region.CreateWithRegion -DcompressType=SNAPPY -DdataBlockEncoding=FAST_DIFF -DbloomFilter=NONE $tableName $columnName $regionNum
        date=`date -d "+1 day $date" +%Y%m%d`
done