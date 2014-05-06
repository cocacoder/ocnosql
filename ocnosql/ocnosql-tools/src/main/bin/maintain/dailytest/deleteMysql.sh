#!/bin/sh


if [ $# -eq 1 ]
then
   start=`date -d "-$1 day" "+%Y-%m-%d"`
else
   start=`date -d "-30 day" "+%Y-%m-%d"`
fi

echo $start >/tmp/deleteOldJob.log
echo "deleting old job...... " >>/tmp/deleteOldJob.log
mysql -uhive -h10.181.170.146 -p134%abc <<END  >>/tmp/deleteOldJob.log
#use ctetl2;
use ctetl;
delete from df_act_varible where act_inst_ in (select dbid_ from df_his_act_inst where flow_inst_ in (select dbid_ from df_his_flow_inst where start_ <= '$start'));
delete from df_his_act_inst where flow_inst_ in (select dbid_ from df_his_flow_inst where start_ <= '$start');
delete from df_his_flow_varible where flow_inst_ in (select dbid_ from df_his_flow_inst where start_ <= '$start');
delete from df_his_flow_inst where start_ <= '$start';
delete from df_flow_varible where flow_inst_ in (select dbid_ from df_flow_inst where start_ <= '$start');
delete from df_flow_inst where start_ <= '$start';
delete from df_execution_rel where parent_id_ in (select dbid_ from df_execution where start_ <= '$start');
delete from df_execution where start_ <= '$start';
delete from sch_var_log where task_id_ in (select dbid_ from sch_job_log where start_time_ <= '$start');
delete from sch_job_log where start_time_ <= '$start';
END

echo "Done! " >>/tmp/deleteOldJob.log
