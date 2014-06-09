'''
Created on 2013-11-2

@author: liuxiang2
'''

from email.mime.text import MIMEText
import ConfigParser
import re
import smtplib
import time


mailto_list=["liuxiang2@asiainfo-linkage.com","d_liuxiang@126.com"]
mail_host="smtp.163.com"
mail_user="ocnosqlautotest"
mail_pass="ocnosql"
mail_postfix="163.com"
#mail_host="mail.asiainfo-linkage.com"
#mail_user="liuxiang2"
#mail_pass="%lx08020729"
#mail_postfix="asiainfo-linkage.com"
ISOTIMEFORMAT='%Y-%m-%d %X'

confFile = ConfigParser.ConfigParser();
confFile.read("../conf/ocnosql-test.conf");

HBASE_HOME = confFile.get("OVERALL_CONF", "HBASE_HOME");

def read_file(filename,lenth):
    file_objec=open(filename);
    mail_content = [];
    date_time = str(time.strftime( '%Y-%m-%d', time.localtime(time.time())))
#     print date_time
    i = 0;
    try:
        list_file=file_objec.readlines();
        for lines in list_file:
            if  date_time in lines or i >= 1 :
#                 print  lines;
#                 print i
                if (i >= 1):
                    mail_content.append(lines);
#                 print "mail_contest is ", mail_content;
                i = i + 1;
                if i >= lenth:
                    break; 
    finally:
        file_objec.close();
    return  mail_content;

def make_content(fileName, lenth):
    content = read_file(fileName,lenth)
    messageTmp = "";
    for i in content:
        print i;
        '  '.join(i.split());
        i = "<td>" + i;
        i = re.sub(r'\s+', '</td> <td>', i);
        messageTmp = messageTmp +  " <tr>"  + i + " </tr>" ;
    messageTmp = messageTmp + "</table>"
    return messageTmp

def send_mail(to_list,sub,content):
    me=mail_user+"<"+mail_user+"@"+mail_postfix+">"
    msg = MIMEText(content,'html')
    msg['Subject'] = sub
    msg['From'] = me
    msg['To'] = ";".join(to_list)
    try:
        s = smtplib.SMTP()
        s.connect(mail_host)
        s.login(mail_user,mail_pass)
        s.sendmail(me, to_list, msg.as_string())
        s.close()
        return True
    except Exception, e:
        print str(e)
        return False

if __name__ == '__main__':
    begin_date = str(time.strftime( ISOTIMEFORMAT, time.localtime( time.time() ) ));
    subject = "OCNoSQL DAILLY AUTO TEST " + begin_date;
    print "subject is ", subject;
    filename = [["Hbase Performance Test", "../report/hbasePerformanceTest.report", 6] ,
                ["Hadoop Performance Test","../report/hadoopPerformanceTest.report", 5],
                ["OCNoSQL LOAD&PUT Performance Test","../report/OCNoSQLPerformanceTest.report", 6],
                ["OCNoSQL QUERY Performance Test","../report/OCNoSQLQueryTest.report", 10]]
    
    message = """        
    This is an e-mail message sent by OCNoSQL AUTO-TEST Program 
    <h1>OCNoSQL DAILLY AUTO TEST</h1>
    """
    for i in range(0, len(filename)):
        print filename[i][0];
        msg = " <h2><a> "   +  filename[i][0] +  "  </a></h2>  <table border=\"1\">  "
        message = message + msg + make_content(filename[i][1], filename[i][2]);
    
    print message;

    if send_mail(mailto_list, subject, message):
        print "success"
    else:
        print "fail"
