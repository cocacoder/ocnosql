#!/usr/bin/env python

__author__="liuxiang2"
__date__ ="$2013-8-15 14:30:08$"

import time
import os
import ConfigParser

confFile = ConfigParser.ConfigParser();
confFile.read("../conf/ocnosql-test.conf");

ISOTIMEFORMAT='%X'
report_file_name="../report/hadoopPerformanceTest.report";

# overall config
HADOOP_HOME = confFile.get("OVERALL_CONF", "HADOOP_HOME");
HDFS_HOME = confFile.get("OVERALL_CONF", "HDFS_HOME");
HADOOP_EXAMPLES_JAR = HADOOP_HOME + "/hadoop-examples-*.jar"
TestDFSIO = HDFS_HOME + "/share/hadoop/mapreduce/hadoop-mapreduce-client-jobclient-*-tests.jar TestDFSIO "

#hdfs read & write case config
files = confFile.get("HDFS-CASE", "files"); 
size = confFile.get("HDFS-CASE", "size");

# wordcount case config
WORDCOUNT_INPUT = confFile.get("WORDCOUNT-CASE", "WORDCOUNT_INPUT");
WORDCOUNT_OUTPUT = confFile.get("WORDCOUNT-CASE", "WORDCOUNT_OUTPUT");
wordCount_bytes_per_map = confFile.get("WORDCOUNT-CASE", "wordCount_bytes_per_map");
maps_per_host = confFile.get("WORDCOUNT-CASE", "maps_per_host");
number_of_map = confFile.get("WORDCOUNT-CASE", "number_of_map");

rows=10;
clients=100;

#case scripts
hadoop_write = HADOOP_HOME + "/bin/hadoop jar " + TestDFSIO + " -write -nrFiles " + files + " -size " + size;
hadoop_read = HADOOP_HOME + "/bin/hadoop jar " + TestDFSIO + " -read -nrFiles " + files + " -size " + size;
wordCount = HADOOP_HOME + "/bin/hadoop jar " +  HADOOP_EXAMPLES_JAR + " wordcount  -D mapred.reduce.tasks=" + str(maps_per_host) + " " + \
    WORDCOUNT_INPUT + " " + WORDCOUNT_OUTPUT;

hdfs_size = int(files) * int(size) ;
wordcount_size = int(wordCount_bytes_per_map) * int(number_of_map) * int(maps_per_host) / 1024 /1024 ;
print "hdfs size is " , hdfs_size  
print "wordcount_size is ", wordcount_size;

case = [["hadoop_write",hadoop_write], ["hadoop_read",hadoop_read],  ["WordCount",wordCount]];
file_size = [hdfs_size, hdfs_size,wordcount_size];
case_path =  confFile.get("OVERALL_CONF", "case_path"); 

if os.path.exists("../report"):
    print "path exisit"
else:
    print "path not exist"
    os.mkdir("../report")
    
# if os.path.exists(report_file_name):
print "file of %s  exisit" % report_file_name
# else:
report_file=open(report_file_name,'a');
print >> report_file,"[",str(time.strftime('%Y-%m-%d',time.localtime(time.time()))),"]"
print >> report_file, "%-20s %-15s %15s %20s %20s " % ("case_name", "start_time", "duration(s)",  "file_size", "performance")
report_file.close();  

print "start_time is %s " % str(time.strftime( ISOTIMEFORMAT, time.localtime( time.time() ) ));

def WordCount():
    print("prepare to do wordCount Prepare")
    os.system("hdfs dfs -rm -R " +  WORDCOUNT_INPUT);
    os.system("hdfs dfs -rm -R " +  WORDCOUNT_OUTPUT);
    WordCount_prepare = HADOOP_HOME + "/bin/hadoop jar " +  HADOOP_EXAMPLES_JAR + \
    " randomtextwriter -D test.randomtextwrite.bytes_per_map=" + str(wordCount_bytes_per_map) + \
    " -D test.randomtextwrite.maps_per_host=" + str(maps_per_host) + " " +WORDCOUNT_INPUT
    os.system(WordCount_prepare);
    print "WordCount_prepare = " + WordCount_prepare;
    print "WordCount prepare down"

def comp_perform(case_name,start_time, end_time, file_size,file_name,begin_date):
    perf = file_size /(end_time - start_time) ;
    report_file=open(file_name,'a');
    print >> report_file, "%-20s %-15s %15.3f %17sMB %18.3fMB/s" % (case_name, begin_date,end_time-start_time, int(file_size), perf)
    report_file.close();
    return perf;

WordCount();

def hadoop_perform_test(case_name, file_size):
    start_time = time.time();
    begin_date = str(time.strftime( ISOTIMEFORMAT, time.localtime( time.time() ) ));
    print "the case is %s " % case_name[1]
    os.system(case_name[1])
    end_time = time.time();
    comp_perform(case_name[0],start_time,end_time, file_size, report_file_name, begin_date);
    print "%s is done  "  % (case_name[0]);

print "begin performance test"

for i in range(0,len(case)):
    item_path="cd " +case_path + case[i][0];
    print "item_path = %s " %  item_path
#    os.system(item_path + ";./recover_conf.sh" )
    hadoop_perform_test(case[i],file_size[i]);
    
print "liuxiang"