#!/usr/bin/env python

__author__="liuxiang2"
__date__ ="$2013-8-15 14:30:08$"

import ConfigParser
import os
import time

confFile = ConfigParser.ConfigParser();
confFile.read("../conf/ocnosql-test.conf");

ISOTIMEFORMAT='%X'
reportFileName="../report/OCNoSQLPerformanceTest.report";
ISOTIMEFORMAT2='%Y%m%d'
dateTime = str(time.strftime( ISOTIMEFORMAT2, time.localtime( time.time() ) ));
print dateTime;
# overall config
HADOOP_HOME = confFile.get("OVERALL_CONF", "HADOOP_HOME");
HDFS_HOME = confFile.get("OVERALL_CONF", "HDFS_HOME");
HBASE_HOME = confFile.get("OVERALL_CONF", "HBASE_HOME");
JAVA_HOME = confFile.get("OVERALL_CONF", "JAVA_HOME");

multipleTableName = confFile.get("OCNoSQL-LOAD-API-CASE", "multipleTableName");
sigleTableName = confFile.get("OCNoSQL-LOAD-API-CASE", "sigleTableName");
inputPath = confFile.get("OCNoSQL-LOAD-API-CASE", "inputPath");
outputPath = confFile.get("OCNoSQL-LOAD-API-CASE", "outputPath");
dataFile = confFile.get("OCNoSQL-LOAD-API-CASE", "dataFile");
seperator = confFile.get("OCNoSQL-LOAD-API-CASE", "seperator");
columns = confFile.get("OCNoSQL-LOAD-API-CASE", "columns");
rowkeyGenerator = confFile.get("OCNoSQL-LOAD-API-CASE", "rowkeyGenerator");
rowkeycolumn = confFile.get("OCNoSQL-LOAD-API-CASE", "rowkeycolumn"); 
algocolumn = confFile.get("OCNoSQL-LOAD-API-CASE", "algocolumn"); 
compressType = confFile.get("OCNoSQL-LOAD-API-CASE", "compressType");
callback = confFile.get("OCNoSQL-LOAD-API-CASE", "callback");
native_lib = confFile.get("OCNoSQL-LOAD-API-CASE", "native_lib");
lib = HADOOP_HOME + "/conf:" + HBASE_HOME + "/conf"
for i in os.listdir(HDFS_HOME + "/share/hadoop"):
    if os.path.exists(HDFS_HOME + "/share/hadoop/" + i):
        for j in os.listdir(HDFS_HOME + "/share/hadoop/" + i):
#             if "jar" in j:
            if j.endswith(".jar"):
                lib = lib + ":" + HDFS_HOME + "/share/hadoop/" + i + "/" +j
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

# multipleLoad = JAVA_HOME + "/bin/java -cp " + lib + " com.ailk.oci.ocnosql.client.load.mutiple.MutipleColumnImportTsv \
#  -Dimporttsv.columns=" + columns + " -Dimporttsv.bulk.output=" + outputPath + " -DrowkeyGenerator=" \
#   + rowkeyGenerator + " -DcompressType=" + compressType + " " + multipleTableName + " " + inputPath+"/"+dataFile
# 
# multipleComplete = JAVA_HOME + "/bin/java -cp " + lib + " -Djava.library.path=" + native_lib + \
#     " com.ailk.oci.ocnosql.client.load.LoadIncrementalHFiles " + outputPath + " " +multipleTableName
# 
# sigleLoad = JAVA_HOME + "/bin/java -cp "+ lib + " com.ailk.oci.ocnosql.client.load.single.SingleColumnImportTsv \
#  -Dimporttsv.columns=" + columns + " -Dimporttsv.bulk.output=" + outputPath + "2 -DrowkeyGenerator=" \
#  + rowkeyGenerator + " -DcompressType=" + compressType + " " + sigleTableName + " " + inputPath+"/"+dataFile
# 
# sigleComplete = JAVA_HOME + "/bin/java -cp " + lib + " -Djava.library.path=" + native_lib + \
#     " com.ailk.oci.ocnosql.client.load.LoadIncrementalHFiles " + outputPath + "2 " +sigleTableName

newMultipleLoad = JAVA_HOME + "/bin/java -cp " + lib + " com.ailk.oci.ocnosql.client.load.mutiple.MutipleColumnImportTsv -Dimporttsv.columns=" \
+ columns + " -Dimporttsv.bulk.output=" + outputPath +  " -DcompressType=" + compressType  + " -Dimporttsv.rowkeycolumn=" + rowkeycolumn + \
 "  -Dimporttsv.rowkeyGenerator=" + rowkeyGenerator + "  -Dimporttsv.algocolumn="  + algocolumn + " -Dimporttsv.callback=" + callback +\
  " " + multipleTableName + " " + inputPath 

newSigleLoad = JAVA_HOME + "/bin/java -cp " + lib + " com.ailk.oci.ocnosql.client.load.single.SingleColumnImportTsv -Dimporttsv.columns=" \
+ columns + " -Dimporttsv.bulk.output=" + outputPath +  "2 -DcompressType=" + compressType  + " -Dimporttsv.rowkeycolumn=" + rowkeycolumn + \
 "  -Dimporttsv.rowkeyGenerator=" + rowkeyGenerator + "  -Dimporttsv.algocolumn="  + algocolumn + " -Dimporttsv.callback=" + callback +\
  " " + sigleTableName + " " + inputPath 
 
# newSigleLoad = JAVA_HOME + "/bin/java -cp " + lib + " com.ailk.oci.ocnosql.client.load.single.SingleColumnImportTsv \
#  -Dimporttsv.columns=" + columns + " -Dimporttsv.bulk.output=" + outputPath +  " -DcompressType=" + compressType \
#   + " -Dimporttsv.rowkeycolumn=" + rowkeycolumn + "  -Dimporttsv.rowkeyGenerator= " + rowkeyGenerator + " -Dimporttsv.algocolumn=" \
#    + algocolumn + " -Dimporttsv.callback=" + callback + " " + multipleTableName + " " + inputPath 

# print "newMultipleLoad is ", newMultipleLoad
# caseName = [["multipleLoad", multipleLoad, multipleComplete],["sigleLoad", sigleLoad, sigleComplete]];
caseName = [["newMultipleLoad", newMultipleLoad],["newSigleLoad", newSigleLoad]];

def makeDir():
    if os.path.exists("../report"):
        print "path exisit"
    else:
        print "path not exist"
        os.mkdir("../report")
    report_file=open(reportFileName,'a');
    print >> report_file,"[",str(time.strftime('%Y-%m-%d',time.localtime(time.time()))),"]"
    print >> report_file, "%-25s %8s %15s %15s %23s %23s" % ("case_name", "start_time", "duration(s)",  "fileSize(MB)", "performance(MB/s)", "performance(count/s)")

    report_file.close();  

def lineCount(fileName):
    count = 0
    thefile = open(fileName,'rb')
    while 1:
        buf = thefile.read(65536)
        if not buf:break
        count += buf.count('\n')
    return count

def computePerform(case_name,start_time, end_time, filesize, filecount, reportfile, begin_date):
    perf1 = filesize  /(end_time - start_time);
    perf2 = filecount /(end_time - start_time);
    report_file=open(reportfile,'a');
    print >> report_file, "%-25s %8s %12.3f %15s %20.3f %20.3f" % (case_name, begin_date, end_time-start_time, str(filesize), perf1, perf2)
    report_file.close();    
    return perf1;

def prepareData():
    print "****************************ONCNOSQL API LOAD BEGIN PREPARE DATA**********************************"
    os.system(HDFS_HOME + "/bin/hdfs dfs -rm -R " + outputPath);
    os.system(HDFS_HOME + "/bin/hdfs dfs -rm -R " + outputPath+"2");
    os.system(HDFS_HOME + "/bin/hdfs dfs -rm -R " + inputPath);
    os.system(HDFS_HOME + "/bin/hdfs dfs -mkdir " + inputPath);
    os.system(HDFS_HOME + "/bin/hdfs dfs -copyFromLocal ../data/" + dataFile + " " + inputPath);
    print "****************************ONCNOSQL API LOAD COMPLETE PREPARE DATA**********************************"

fileSize = os.path.getsize("../data/" + dataFile) / 1024 / 1024;
fileCount = lineCount("../data/" + dataFile);
# fileSize=24
# fileCount=100
def ocnosqlAPILoad(case):
    print "****************************ONCNOSQL API MULTIPLE BEGIN MULTIPLE LOAD  **********************************"
    startTime = time.time();
    beginDate = str(time.strftime( ISOTIMEFORMAT, time.localtime( time.time() ) ));
    os.system(case[1]);
#     os.system(case[2]);
    endTime = time.time();
    computePerform(case[0], startTime, endTime, fileSize, fileCount, reportFileName, beginDate);
    print "****************************ONCNOSQL API MULTIPLE LOAD COMPLETE **********************************"

if __name__ == '__main__':
    makeDir();
    prepareData();
    for  i in range(0,len(caseName)):
        ocnosqlAPILoad(caseName[i]);
#         print caseName[i][2]
