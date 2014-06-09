#!/bin/bash
############################################################################
# 功能：对hbase中的符合条件的表进行整理，每次间隔指定时间在执行下一张表的整理
# 使用方法：使用是需要根据实际情况修改变量root_dir和column_family的值，
#           调用./compactTableTool.sh 时间间隔(秒)即可，如：./compactTableTool.sh 300
############################################################################

# hbase数据存储的根目录，需要根据情况修改
root_dir="hbase"
# 列族名称，需要根据情况修改
column_family="detail"

# 获取全部的表名称
echo "list" | hbase shell > ./table_list.tmp
line_num=`cat ./table_list.tmp | wc -l`
end_num=`expr $line_num-2`
awk 'FNR>=7&&FNR<$end_num' table_list.tmp >tableName.tmp

while read line
do
  hfile_num=`hadoop fs -ls /$root_dir/$line/*/$column_family | wc -l`
  region_num=`hadoop fs -ls /$root_dir/$line | wc -l`

  a=$[2*$region_num]

  # 判断条件：hfile>region*2，执行major compact
  if [ $hfile_num -gt $a ];then
    echo "do major compact table : "$line
    echo "major_compact '$line'" | hbase shell
    sleep $1
  fi

done < ./tableName.tmp

# 清理临时文件
rm -r ./table_list.tmp
rm -r ./tableName.tmp

