# To change this license header, choose License Headers in Project Properties.
# To change this template file, choose Tools | Templates
# and open the template in the editor.

__author__="liuxiang2"
__date__ ="$2013-11-6 13:00:11$"
import os
import ConfigParser
import time

confFile = ConfigParser.ConfigParser();
confFile.read("../conf/ocnosql-test.conf");

ISOTIMEFORMAT='%X'
reportFileName="../report/OCNoSQLPerformanceTest.report";

HADOOP_HOME = confFile.get("OVERALL_CONF", "HADOOP_HOME");
HDFS_HOME = confFile.get("OVERALL_CONF", "HDFS_HOME");
HBASE_HOME = confFile.get("OVERALL_CONF", "HBASE_HOME");
JAVA_HOME = confFile.get("OVERALL_CONF", "JAVA_HOME");

HbasePutJar = confFile.get("OCNoSQL-PUT-API-CASE", "HbasePutJar");
multipleTableName = confFile.get("OCNoSQL-LOAD-API-CASE", "multipleTableName");
sigleTableName = confFile.get("OCNoSQL-LOAD-API-CASE", "sigleTableName");
dataPath = confFile.get("OCNoSQL-PUT-API-CASE", "dataPath");
fileName = confFile.get("OCNoSQL-PUT-API-CASE", "dataName"); 
flushNum = confFile.get("OCNoSQL-PUT-API-CASE", "flushNum"); 

os.chdir("../")
dirPath=os.getcwd();
os.chdir("./bin")

lib = HADOOP_HOME + "/conf:" + HBASE_HOME + "/conf"
lib = lib + ":" + dirPath + "/lib/" + HbasePutJar
for i in os.listdir(HADOOP_HOME + "/lib"):
#     if "jar" in i
    if i.endswith(".jar"):
        lib = lib + ":" + HADOOP_HOME + "/lib/" + i
for i in os.listdir(HADOOP_HOME ):
#     if "jar" in i:
    if i.endswith(".jar"):
        lib = lib + ":" + HADOOP_HOME + "/" + i
for i in os.listdir(HBASE_HOME):
    if i.endswith(".jar"):
        lib = lib + ":" + HBASE_HOME + "/" + i
print "lib is " + lib
ocnosqlMultiPut =  JAVA_HOME + "/bin/java -cp " + lib + " com.ailk.oci.ocnosql.client.put.MultipleHbasePutAutoTest" + \
    " " + multipleTableName + " " + dataPath + fileName +  " " + flushNum
ocnosqlSiglePut =  JAVA_HOME + "/bin/java -cp " + lib + " com.ailk.oci.ocnosql.client.put.SingleHbasePutAutoTest" + \
    " " + sigleTableName + " " + dataPath + fileName +  " " + flushNum

caseName = [["ocnosqlMultiPut",ocnosqlMultiPut],["ocnosqlSiglePut",ocnosqlSiglePut]]

def makeDir():
    if os.path.exists("../report"):
        print "path exisit"
    else:
        print "path not exist"
        os.mkdir("../report")
    if os.path.exists(reportFileName):
        print "file of %s  exisit" % reportFileName
    else:
        report_file=open(reportFileName,'a');
        print >> report_file,"[",str(time.strftime('%Y-%m-%d',time.localtime(time.time()))),"]"
        print >> report_file, "%-25s %8s %15s %15s %23s %23s" % ("case_name", "start_time", "duration(s)",  "fileSize(MB)", "performance(MB/s)", "performance(count/s)")
        report_file.close();  

def lineCount(filename):
    count = 0
    thefile = open(filename,'rb')
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

def ocnosqlPutTest(case):
    print "****************************ONCNOSQL MULTIPLE BEGIN MULTIPLE PUT  **********************************"
    startTime = time.time();
    beginDate = str(time.strftime( ISOTIMEFORMAT, time.localtime( time.time() ) ));
    os.system(case[1]);
    endTime = time.time();
    computePerform(case[0], startTime, endTime, fileSize, fileCount, reportFileName, beginDate);
    print "****************************ONCNOSQL MULTIPLE PUT COMPLETE **********************************"

if __name__ == "__main__":
    fileSize = os.path.getsize("../data/" + fileName) / 1024 / 1024;
    fileCount = lineCount("../data/" + fileName);
    makeDir()
    for  i in range(0,len(caseName)):
        ocnosqlPutTest(caseName[i])
#        print caseName[i][1]