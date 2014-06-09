'''
Created on 2013-11-18

@author: liuxiang2
'''
import ConfigParser
import os
import time

confFile = ConfigParser.ConfigParser();
confFile.read("../conf/ocnosql-test.conf");

ISOTIMEFORMAT='%Y%m%d-%X'
reportFileName="../report/OCNoSQLQueryTest.report";

HADOOP_HOME = confFile.get("OVERALL_CONF", "HADOOP_HOME");
HDFS_HOME = confFile.get("OVERALL_CONF", "HDFS_HOME");
HBASE_HOME = confFile.get("OVERALL_CONF", "HBASE_HOME");
JAVA_HOME = confFile.get("OVERALL_CONF", "JAVA_HOME");
OCNOSQL_TOOLS = confFile.get("OVERALL_CONF", "OCNOSQL_TOOLS");

multipleTableName = confFile.get("OCNoSQL-LOAD-API-CASE", "multipleTableName");
sigleTableName = confFile.get("OCNoSQL-LOAD-API-CASE", "sigleTableName");
tableName = confFile.get("OCNoSQL-QUERY-API-CASE", "tableName");


dateTime = str(time.strftime( ISOTIMEFORMAT, time.localtime( time.time() ) ));
print dateTime;

for i in os.listdir("../report"):
    if i.endswith(".report"):
#         print "lx"
        os.rename("../report/" + i, "../report/" + i + "_bak_"+ dateTime);

CreateTable1 = OCNOSQL_TOOLS + "/bin/ocnosqlAutoTestCreateTable.sh " + multipleTableName + " F 30 " 
CreateTable2 = OCNOSQL_TOOLS + "/bin/ocnosqlAutoTestCreateTable.sh " + sigleTableName + " F 30 "
CreateTable3 = OCNOSQL_TOOLS + "/bin/ocnosqlAutoTestCreateTable.sh " + tableName + " F 30 "
print CreateTable1,CreateTable2,CreateTable3
dirPath=os.getcwd();
print "dirPath =", dirPath;
print "****************create Table for autoTest******************"
os.chdir(OCNOSQL_TOOLS + "/bin");
print "now the dir is ", os.getcwd();
os.system(CreateTable1);
os.system(CreateTable2);
os.system(CreateTable3);
os.chdir(dirPath);
print "now the dir is ", os.getcwd();

