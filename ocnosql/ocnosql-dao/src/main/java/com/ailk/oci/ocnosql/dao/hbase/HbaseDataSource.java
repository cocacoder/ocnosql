package com.ailk.oci.ocnosql.dao.hbase;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTablePool;

import java.io.IOException;

public class HbaseDataSource{
	private final static Log log = LogFactory.getLog(HbaseDataSource.class
			.getSimpleName());

	private String zkHost;
	private String zkClientPort;
	private int tablePoolMaxSize; //每个表可以获得的最大的reference数量
	private HTablePool tablePool; 
	private HBaseAdmin admin;
	
	public String getZkHost() {
		return zkHost;
	}
	public void setZkHost(String zkHost) {
		this.zkHost = zkHost;
	}
	public String getZkClientPort() {
		return zkClientPort;
	}
	public void setZkClientPort(String zkClientPort) {
		this.zkClientPort = zkClientPort;
	}
	public int getTablePoolMaxSize() {
		return tablePoolMaxSize;
	}
	public void setTablePoolMaxSize(int tablePoolMaxSize) {
		this.tablePoolMaxSize = tablePoolMaxSize;
	}
	public HTablePool getTablePool() {
		return tablePool;
	}
	public void setTablePool(HTablePool tablePool) {
		this.tablePool = tablePool;
	}
	public HBaseAdmin getAdmin() {
		return admin;
	}
	public void setAdmin(HBaseAdmin admin) {
		this.admin = admin;
	}

	private void init(){
		if(StringUtils.isEmpty(zkHost)||StringUtils.isEmpty(zkClientPort)){
			String msg = "hosts or port must not be null";
			log.error(msg);
			throw new HbaseRuntimeException(msg);
		}

        try {
            if (!System.getProperty("profile", "product").equals("test")) {
                HbaseConnectionFactory hbaseconnectionFactory = new HbaseConnectionFactory(zkHost, zkClientPort);
                admin = hbaseconnectionFactory.createConnection();
                tablePool = new HTablePool(admin.getConfiguration(), tablePoolMaxSize);
            }else{
                new Thread(){
                    @Override
                    public void run() {
                        HbaseConnectionFactory hbaseconnectionFactory = new HbaseConnectionFactory(zkHost, zkClientPort);
                        admin = hbaseconnectionFactory.createConnection();
                        tablePool = new HTablePool(admin.getConfiguration(), tablePoolMaxSize);
                    }
                }.start();
            }
        } catch (HbaseRuntimeException e) {
            log.warn("cannot connect to hbase,perhaps it was in test environment");
        }
    }
	
	public void init1(){
		if(StringUtils.isEmpty(zkHost)||StringUtils.isEmpty(zkClientPort)){
			String msg = "hosts or port must not be null";
			log.error(msg);
			throw new HbaseRuntimeException(msg);
		}

        try {
            if (!System.getProperty("profile", "product").equals("test")) {
                HbaseConnectionFactory hbaseconnectionFactory = new HbaseConnectionFactory(zkHost, zkClientPort);
                admin = hbaseconnectionFactory.createConnection();
                tablePool = new HTablePool(admin.getConfiguration(), tablePoolMaxSize);
            }else{
                new Thread(){
                    @Override
                    public void run() {
                        HbaseConnectionFactory hbaseconnectionFactory = new HbaseConnectionFactory(zkHost, zkClientPort);
                        admin = hbaseconnectionFactory.createConnection();
                        tablePool = new HTablePool(admin.getConfiguration(), tablePoolMaxSize);
                    }
                }.start();
            }
        } catch (HbaseRuntimeException e) {
            log.warn("cannot connect to hbase,perhaps it was in test environment");
        }
    }
    private void destroy(){
        if(admin!=null){
            try {
                admin.close();
            } catch (Exception e) {
                log.warn("excepation when close hbase connection",e);
            }
        }
        if(tablePool!=null){
            try {
                tablePool.close();
            } catch (Exception e) {
                log.warn("excepation when close hbase table poll",e);
            }
        }
    }
}
