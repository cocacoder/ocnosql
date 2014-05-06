#!/bin/bash
#set -x
#sh source_profile.sh
echo "!------begin to balance hbase,begin time is `date +'%Y-%m-%d %H:%M:%S'`---------!"
echo "balancer"|hbase shell > /dev/null 2>&1
echo "!------end balance hbase,end time is `date +'%s'` --------!"
