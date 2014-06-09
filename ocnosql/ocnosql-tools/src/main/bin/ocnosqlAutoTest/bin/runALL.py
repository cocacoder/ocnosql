'''
Created on 2013-11-9

@author: liuxiang2
'''
import os

#autoTest = ["prepareTest.py", "hadoopPerformanceTest.py", "hbasePerformanceTest.py", "ocnosqlLoadApi.py", 
#            "OcnosqlPutTest.py", "OcnosqlQueryAutoTest.py", "sendEmail.py"];

autoTest = ["prepareTest.py", "hadoopPerformanceTest.py", "hbasePerformanceTest.py", "ocnosqlLoadApi.py", 
             "OcnosqlQueryAutoTest.py", ];
def pythonRun():
    for i in autoTest:
        i = "python " +  i;
        print "**************************begin run " , i ,"**************************";
        os.system(i);
        print i , "************************** have completed!!!! **************************" ;

if __name__ == '__main__':
    pythonRun();
