'''
Created on 2013-11-8

@author: liuxiang2
'''

__author__="liuxiang2"
__date__ ="$2013-11-8 13:00:11$"
import os
import ConfigParser
import time

confFile = ConfigParser.ConfigParser();
confFile.read("../conf/ocnosql-test.conf");

ISOTIMEFORMAT='%X'
reportFileName="../report/OCNoSQLQueryTest.report";

HADOOP_HOME = confFile.get("OVERALL_CONF", "HADOOP_HOME");
HDFS_HOME = confFile.get("OVERALL_CONF", "HDFS_HOME");
HBASE_HOME = confFile.get("OVERALL_CONF", "HBASE_HOME");
JAVA_HOME = confFile.get("OVERALL_CONF", "JAVA_HOME");

OCNoSQLQueryJar  = confFile.get("OCNoSQL-QUERY-API-CASE", "OCNoSQLQueryJar");
tableName = confFile.get("OCNoSQL-QUERY-API-CASE", "tableName");
rowKey = confFile.get("OCNoSQL-QUERY-API-CASE", "rowKey");
rowKey2 = confFile.get("OCNoSQL-QUERY-API-CASE", "rowKey2");
rowKey3 = confFile.get("OCNoSQL-QUERY-API-CASE", "rowKey3");
rowKey4 = confFile.get("OCNoSQL-QUERY-API-CASE", "rowKey4");
preFixKey = confFile.get("OCNoSQL-QUERY-API-CASE", "preFixKey");
startKey = confFile.get("OCNoSQL-QUERY-API-CASE", "startKey");
endKey = confFile.get("OCNoSQL-QUERY-API-CASE", "endKey");
createCount = confFile.get("OCNoSQL-QUERY-API-CASE", "createCount");
allKey = [int(rowKey), int(rowKey2), int(rowKey3), int(rowKey4), int(startKey), int(endKey)];


os.chdir("../")
dirPath=os.getcwd();
os.chdir("./bin")

lib = HADOOP_HOME + "/conf:" + HBASE_HOME + "/conf"
lib = lib + ":" + dirPath + "/lib/" + OCNoSQLQueryJar 
for i in os.listdir(HADOOP_HOME + "/lib"):
    if i.endswith(".jar"):
        lib = lib + ":" + HADOOP_HOME + "/lib/" + i
for i in os.listdir(HADOOP_HOME ):
    if i.endswith(".jar"):
        lib = lib + ":" + HADOOP_HOME + "/" + i
for i in os.listdir(HBASE_HOME):
    if i.endswith(".jar"):
        lib = lib + ":" + HBASE_HOME + "/" + i
print "lib is " + lib
queryBySigleKeyAllColumn =  JAVA_HOME + "/bin/java -cp " + lib + "  com.ailk.oci.ocnosql.client.query.TestOCNoSqlQueryAPI Sigle NoColumn " + \
     tableName + " " + rowKey 
queryBySigleKeySpecilColumn =  JAVA_HOME + "/bin/java -cp " + lib + "  com.ailk.oci.ocnosql.client.query.TestOCNoSqlQueryAPI Sigle SpecliColumn " + \
     tableName + " " + rowKey 
queryByMultiKeyAllColumn =  JAVA_HOME + "/bin/java -cp " + lib + "  com.ailk.oci.ocnosql.client.query.TestOCNoSqlQueryAPI Multi NoColumn " + \
     tableName + " " + rowKey + " " + rowKey2 + " " + rowKey3 + " " + rowKey4 
queryByMultiKeySpecilColumn =  JAVA_HOME + "/bin/java -cp " + lib + "  com.ailk.oci.ocnosql.client.query.TestOCNoSqlQueryAPI Multi SpecliColumn " + \
     tableName + " " + rowKey + " " + rowKey2 + " " + rowKey3 + " " + rowKey4 
queryByPreFixKeyAllColumn =  JAVA_HOME + "/bin/java -cp " + lib + "  com.ailk.oci.ocnosql.client.query.TestOCNoSqlQueryAPI PreFix NoColumn " + \
     tableName + " " + preFixKey
queryByPreFixKeySpecilColumn =  JAVA_HOME + "/bin/java -cp " + lib + "  com.ailk.oci.ocnosql.client.query.TestOCNoSqlQueryAPI PreFix SpecliColumn " + \
     tableName + " " + preFixKey
queryByKeyRangeAllColumn =  JAVA_HOME + "/bin/java -cp " + lib + "  com.ailk.oci.ocnosql.client.query.TestOCNoSqlQueryAPI Range NoColumn " + \
     tableName + " " + startKey + " " + endKey
queryByKeyRangeSpecilColumn =  JAVA_HOME + "/bin/java -cp " + lib + "  com.ailk.oci.ocnosql.client.query.TestOCNoSqlQueryAPI Range SpecliColumn " + \
     tableName + " " + startKey + " " + endKey 

caseName = [["queryBySigleKeyAllColumn", queryBySigleKeyAllColumn ], ["queryBySigleKeySpecilColumn", queryBySigleKeySpecilColumn], 
            ["queryByMultiKeyAllColumn", queryByMultiKeyAllColumn], ["queryByMultiKeySpecilColumn", queryByMultiKeySpecilColumn],
            ["queryByPreFixKeyAllColumn", queryByPreFixKeyAllColumn], ["queryByPreFixKeySpecilColumn", queryByPreFixKeySpecilColumn],
            ["queryByKeyRangeAllColumn", queryByKeyRangeAllColumn], ["queryByKeyRangeSpecilColumn", queryByKeyRangeSpecilColumn]];

def makeDir():
    if os.path.exists("../report"):
        print "path exisit"
    else:
        print "path not exist"
        os.mkdir("../report")
#     if os.path.exists(reportFileName):
#         print "file of %s  exisit" % reportFileName
#     else:
    report_file=open(reportFileName,'a');
    print >> report_file,"[",str(time.strftime('%Y-%m-%d',time.localtime(time.time()))),"]"
    print >> report_file, "%-35s %8s %15s %15s %20s " % ("case_name", "start_time", "duration(s)",  "returntRows", "returnColumns")
    report_file.close();  

def computePerform(case_name,start_time, end_time, reportfile, begin_date):
    if("AllColumn" in case_name):
        columnCount = 175
    elif("SpecilColumn" in case_name):
        columnCount = 3;
    else:
        columnCount = 0;
    if("SigleKey" in case_name):
        rowCount = 1;
    elif("MultiKey" in case_name):
        rowCount = len(allKey) - 2;
    elif("KeyRange" in case_name):
        rowCount = int(endKey) - int(startKey) + 1;
    elif("PreFix" in case_name):
        rowCount = max(allKey) - min(allKey) + int(createCount);
    else:
        rowCount = 0;
    
    report_file=open(reportfile,'a');
    print >> report_file, "%-35s %8s %12.3f %15s %20s" % (case_name, begin_date, end_time-start_time, str(rowCount), str(columnCount))
    report_file.close();    

def ocnosqlPutTest(case):
    print "****************************ONCNOSQL  BEGIN " + case[0] +"**********************************"
    startTime = time.time();
    beginDate = str(time.strftime( ISOTIMEFORMAT, time.localtime( time.time() ) ));
    os.system(case[1]);
    endTime = time.time();
    computePerform(case[0], startTime, endTime, reportFileName, beginDate);
    print "****************************ONCNOSQL COMPLETE " + case[0] +" **********************************"

def createData():
    hbasePut = "for i in " + str(min(allKey) - int(createCount)/2) + ".." + str(max(allKey) + int(createCount)/2) +" do for j in 1..175 do put '" + tableName + \
     "', \\\"#{i}\\\",\\\"F:A#{j}\\\",\\\"value#{j}\\\" end end"
    cmdShell = "echo \" " + hbasePut + " \" | hbase shell" 
    print "cmd Shell is " , cmdShell;
    os.system(cmdShell);

if __name__ == "__main__":
    makeDir();
    createData();
    for  i in range(0,len(caseName)):
        ocnosqlPutTest(caseName[i])
    
    
