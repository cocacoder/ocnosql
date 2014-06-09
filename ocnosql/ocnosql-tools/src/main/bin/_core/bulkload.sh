#!/bin/bash
#set -x
hadoop fs -rm -R /output/testbulkload
tableName=test
inputPath=/test
outputPath=/output/testbulkload
separator=";"
columns=F:PHONE_NO,F:TERM_MODEL_ID,F:C_GGSN_IP,F:BUSI_TYPE_ID,F:APP_ID,F:TERM_MANUF_ID,F:TERM_TYPE_ID,F:LAC,F:CI,F:ACCESS_MODE_ID,F:START_TIME,F:SITE_NAME_ID,F:SITE_CHANNEL_ID,F:EX_COMP_DOMAIN,F:CHARGING_ID,F:UP_FLOW,F:DOWN_FLOW,F:ACCE_URL,F:FLOW,F:REV_01,F:REV_02,F:REV_03,F:REV_04,F:REV_05
rowkeycolumn=PHONE_NO,START_TIME
rowkeyGenerator=md5
algocolumn=PHONE_NO
callback=com.ailk.oci.ocnosql.client.rowkeygenerator.GenRKCallBackDefaultImpl
HADOOP_CLASSPATH=`hbase classpath`
HADOOP_CLASSPATH=$HADOOP_CLASSPATH:./conf
echo $HADOOP_CLASSPATH
bin=`dirname $0`
bin=`cd $bin;pwd`
cd $bin
#for i in ./*.jar; do
#    HADOOP_CLASSPATH=$HADOOP_CLASSPATH:$i
#done
for i in /home/ocnosql/cdh42/hadoop-2.0.0-mr1-cdh4.2.1/lib/*.jar; do
    HADOOP_CLASSPATH=$HADOOP_CLASSPATH:$i
done
for i in /home/ocnosql/cdh42/hadoop-2.0.0-cdh4.2.1/lib/*.jar; do
    HADOOP_CLASSPATH=$HADOOP_CLASSPATH:$i
done
for i in /home/ocnosql/cdh42/hbase-0.94.12-security/lib/*.jar; do
    HADOOP_CLASSPATH=$HADOOP_CLASSPATH:$i
done
export HADOOP_CLASSPATH

echo $HADOOP_CLASSPATH
/home/ocnosql/cdh42/hadoop-2.0.0-mr1-cdh4.2.1/bin/hadoop jar /home/ocnosql/app/ocnosql-tools.jar mutipleimporttsv -Dimporttsv.columns=${columns} -Dimporttsv.bulk.output=${outputPath} -Dimporttsv.rowkeycolumn=${rowkeycolumn} -Dimporttsv.algocolumn=${algocolumn} -Dimporttsv.separator=${separator} -Dimporttsv.callback=${callback}  -Dimporttsv.notloadcolumns=tel,name -Dimporttsv.rowkeyGenerator=${rowkeyGenerator} ${tableName} ${inputPath}
