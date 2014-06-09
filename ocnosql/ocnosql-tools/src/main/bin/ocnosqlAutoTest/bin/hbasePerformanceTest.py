#!/usr/bin/env python

# To change this template, choose Tools | Templates
# and open the template in the editor.

__author__="liuxiang2"
__date__ ="$2013-8-15 14:30:08$"

import time
import os
import ConfigParser

confFile = ConfigParser.ConfigParser();
confFile.read("../conf/ocnosql-test.conf");

ISOTIMEFORMAT='%X'
HBASE_HOME = confFile.get("OVERALL_CONF", "HBASE_HOME");
report_file_name="../report/hbasePerformanceTest.report";
rows = int(confFile.get("HBASE-CASE", "rows"));
clients = int(confFile.get("HBASE-CASE", "clients"));

hbase_random_read = HBASE_HOME +  "/bin/hbase org.apache.hadoop.hbase.PerformanceEvaluation --rows=" + str(rows) + " randomRead " + str(clients)
hbase_random_write = HBASE_HOME +  "/bin/hbase org.apache.hadoop.hbase.PerformanceEvaluation --rows=" + str(rows) + " --presplit=30  randomWrite " + str(clients)
hbase_scan = HBASE_HOME +  "/bin/hbase org.apache.hadoop.hbase.PerformanceEvaluation --rows=" + str(rows) + " --writeToWAL=false scan "  + str(clients)
hbase_sequential_write =  HBASE_HOME +  "/bin/hbase org.apache.hadoop.hbase.PerformanceEvaluation  --rows=" + str(rows) + " --presplit=30 sequentialWrite " + str(clients)

case=[  ["hbase_sequential_write",hbase_sequential_write] , [ "hbase_random_write",hbase_random_write], ["hbase_random_read",hbase_random_read], ["hbase_scan",hbase_scan] ];
case_path="/home/ocnosql/usr/lx/tools/case_conf/";

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
print >> report_file, "%-25s %10s %15s %15s %15s %15s " % ("case_name", "start_time", "duration(s)",  "rows" ,  "clients", "performance")
report_file.close();  

def comp_perform(case_name,start_time, end_time,count,clients,file_name,begin_date):
    perf = count * clients /(end_time - start_time);
    report_file=open(file_name,'a');
    print >> report_file, "%-25s %10s %15.3f %15s %15s %12.3frows/s " % (case_name, begin_date, end_time-start_time,  str(count), str(clients), perf)
    report_file.close();    
    return perf;

def hbase_perform_test(case_name):
    start_time = time.time();
    begin_date = str(time.strftime( ISOTIMEFORMAT, time.localtime( time.time() ) ));
#     print "the case is %s " % case_name[1]
    os.system(case_name[1])
    end_time = time.time();
    comp_perform(case_name[0],start_time,end_time,rows,clients, report_file_name, begin_date);
    print "%s is done  "  % (case_name[0]);

print "begin performance test"

disableTable="disable \"TestTable\" "
dropTable="drop \"TestTable\" "
cmdShell1 = "echo \" " + disableTable + " \" | hbase shell"
cmdShell2 = "echo \" " + dropTable + " \" | hbase shell"  
os.system(cmdShell1);
os.system(cmdShell2);
for i in range(0,len(case)):
    item_path="cd " +case_path + case[i][0];
    print "item_path = %s " %  item_path
#    os.system(item_path + ";./recover_conf.sh" )
    hbase_perform_test(case[i]);
