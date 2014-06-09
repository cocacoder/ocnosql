package com.ailk.oci.ocnosql.client.util;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTablePool;

import com.ailk.oci.ocnosql.common.config.Connection;
/**
 * wangyp5
 */
public class HTableUtils {
	
	static private Boolean inited = false;

    public static HTableInterface getTable(Configuration conf, final byte[] tableName) throws IOException {
        return new HTable(conf, tableName,Connection.getInstance().getThreadPool());
    }
    
    public static HTablePool pool;
    
    public static HTableInterface getTable(Configuration conf, final String tableName) {
    	getPool(conf);
    	return pool.getTable(tableName);
    }
    
    
    
    public static HTablePool getPool(Configuration conf){
    	if(!inited){
    		synchronized(inited){
    			if(!inited) {
    				pool = new HTablePool(conf, conf.getInt("tablepool.max.size", 300));
    				inited = true;
    			}
    		}
    	}
    	return pool;
    }
}
