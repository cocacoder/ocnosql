/**
 * 
 */
package com.ailk.oci.ocnosql.client.util;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.junit.Test;
/**
 * @author liujs3
 * 
 */
public class HbaseInit {
	static Log LOG = LogFactory.getLog(HbaseInit.class);

	@Test
	public void createTestTable() {
		try {
			String tableName="HbaseInitTest";
			String columnName="F";
			Configuration conf = HBaseConfiguration.create();
			HBaseAdmin admin = new HBaseAdmin(conf);
			if (admin.tableExists(tableName)) {
				LOG.error("table : " + tableName + " already exist.");
				return;
			}
			HTableDescriptor tableDesc = new HTableDescriptor(tableName);
			HColumnDescriptor columnDesc = new HColumnDescriptor(columnName);
			tableDesc.addFamily(columnDesc);
			admin.createTable(tableDesc);
			admin.getTableDescriptor(tableName.getBytes()).toString();
			LOG.info("table : " + tableName + " created sucessfully.");
			
			
		} catch (Exception e) {
			LOG.error("failed create region" + e);
		}
		
		/**
		 * put 'HbaseInitTest','9c0138666666666','F:name','OCEAN1'
		 * put 'HbaseInitTest','9c0138666666666','F:age','11'
		 * put 'HbaseInitTest','9c0138666666666','F:tel','15110272134'
		 *  
		 * put 'HbaseInitTest','0c7138555555556','F:name','floyd'
		 * put 'HbaseInitTest','0c7138555555556','F:age','28'
		 * put 'HbaseInitTest','0c7138555555556','F:tel','22222222228'
		 *    
		 * put 'HbaseInitTest','8e21','F:name','floyd2'
		 * put 'HbaseInitTest','8e21','F:age','22'
		 * put 'HbaseInitTest','8e21','F:tel','1234567890'
		 * 
		 */
	}
}
