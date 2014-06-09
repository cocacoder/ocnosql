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
compressType=$4
if [ 'a'$4'a' == 'aa' ]
 then 
     compressType='snappy'
fi

OCNOSQL_TOOL_HOME=`cd ../..;pwd`
echo OCNOSQL_TOOL_HOME=$OCNOSQL_TOOL_HOME
export OCNOSQL_TOOL_HOME
for f in $OCNOSQL_TOOL_HOME/*.jar; 
do
        CLASSPATH=${CLASSPATH}:$f;
done

for m in $HBASE_HOME/lib/*.jar; 
do
        CLASSPATH=${CLASSPATH}:$m;
done
for n in $HBASE_HOME/*.jar; 
do
        CLASSPATH=${CLASSPATH}:$n;
done
for i in /home/ocnosql/cdh43/hadoop-2.0.0-mr1-cdh4.2.1/lib/*.jar; 
do
        CLASSPATH=${CLASSPATH}:$i;
done
for i in ../lib/*.jar; 
do
        CLASSPATH=${CLASSPATH}:$i;
done
CLASSPATH=../conf:${CLASSPATH}
export CLASSPATH
echo $CLASSPATH
tableName="$table_prefix"
java -cp $CLASSPATH  com.ailk.oci.ocnosql.tools.CreateWithRegion $tableName $columnName $regionNum $compressType 
