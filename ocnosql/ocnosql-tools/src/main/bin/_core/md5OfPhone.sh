 #!/bin/sh

#export JAVA_HOME=/home/ocnosql/cdh42/jdk7
#export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
#export PATH=$JAVA_HOME/bin

# param:  13319870890 19080090023 18810900999
# oouput: 13319870890=>aaa
#         19080090023=>bbb
#         18810900999=>ccc

java -cp $CLASSPATH com/ailk/oci/ocnosql/tools/MD5OfPhoneUtil $1 $2 $3
