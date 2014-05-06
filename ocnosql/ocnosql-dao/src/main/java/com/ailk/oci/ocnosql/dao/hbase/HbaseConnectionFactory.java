package com.ailk.oci.ocnosql.dao.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HBaseAdmin;

public class HbaseConnectionFactory{
	private String zkHost;
	private String zkClientPort;
	
	private HBaseAdmin admin;
	
	public HbaseConnectionFactory(String zkHost, String zkClientPort){
		this.zkHost = zkHost;
		this.zkClientPort = zkClientPort;
	}

	public HBaseAdmin createConnection() throws HbaseRuntimeException {
		//获取HBase配置文件中的配置
		Configuration config = HBaseConfiguration.create();
		//设置zookeeper host
		config.set("hbase.zookeeper.quorum", zkHost);
		//设置zookeeper端口
		config.set("hbase.zookeeper.property.clientPort", zkClientPort);
		try {
			//创建HBaseAdmin对象，以操作HBase元数据
			admin = new HBaseAdmin(config);
		} catch (MasterNotRunningException e) {
			throw new HbaseRuntimeException("master progress is not running", e);
		} catch (ZooKeeperConnectionException e) {
			throw new HbaseRuntimeException("could not connet zookeeper", e);
		}
		return admin;
	}
}
