package com.ailk.oci.ocnosql.dao.impl;

import java.io.IOException;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ailk.oci.ocnosql.dao.hbase.HbaseDataSource;
import com.ailk.oci.ocnosql.dao.spi.HbaseTableDao;
import com.ailk.oci.ocnosql.dao.spi.HbaseTableDaoException;
import com.ailk.oci.ocnosql.model.domains.TableAttributeMetadata;
import com.ailk.oci.ocnosql.model.domains.TableMetadata;



/**
 * @author Administrator
 * HBASE表操作实现类
 */
/**
 * @author Administrator
 *
 */
/**
 * @author Administrator
 *
 */
@Service("hbaseTableDao")
public class HbaseTableDaoImpl implements HbaseTableDao{
	private final static Log log = LogFactory.getLog(HbaseTableDaoImpl.class.getSimpleName());
	@Autowired
	private HbaseDataSource hbaseDataSource;
	@Override
	public void createTable(TableMetadata tableMetadata) throws HbaseTableDaoException{
		HBaseAdmin hBaseAdmin = hbaseDataSource.getAdmin();
		try {
			String tablename = tableMetadata.getName();
			//如果表已经存在，则直接报错
			if (hBaseAdmin.tableExists(tablename)) {
				log.error("table is exists!");
			} 
			else {
				HTableDescriptor tableDesc = new HTableDescriptor(tablename);
				//从传入的元数据中获取列族名称
				String familyname = retieveFamliyname(tableMetadata);
				//加入列族
				tableDesc.addFamily(new HColumnDescriptor(familyname));
				//创建表
				hBaseAdmin.createTable(tableDesc);
				log.debug("table create sucess");
			}
		} 
		catch (IOException e) {
			log.error(e);
			throw new HbaseTableDaoException("create table in hbase occur error", e);
		}
	}
	
	
	/**
	 * @param tableMetadata
	 * @return
	 * 获取列族名称，
	 */
	private String retieveFamliyname(TableMetadata tableMetadata) {
		String familyname = null;
		Set<TableAttributeMetadata> attrs = tableMetadata.getTableAttrs();
		boolean isNullFamilyname=true;
		//如果有两个列族怎么办？
		for(TableAttributeMetadata attr:attrs){
			if(attr.getAttributeKey().equals("familyname")){
				familyname=attr.getAttributeValue();
				isNullFamilyname=false;
				break;
			}
		}
		if(isNullFamilyname){
			//默认的列族名称为cf0
			familyname="cf0";
		}
		return familyname;
	}
	@Override
	public void deleteTable(TableMetadata tableMetadata) throws HbaseTableDaoException{
		HBaseAdmin hBaseAdmin = hbaseDataSource.getAdmin();
		try {
			hBaseAdmin.deleteTable(tableMetadata.getName());
		} catch (IOException e) {
			log.error(e);
			throw new HbaseTableDaoException("delete table in hbase occur error", e);
		}
	}
	@Override
	public void disableTable(TableMetadata tableMetadata) throws HbaseTableDaoException {
		HBaseAdmin hBaseAdmin = hbaseDataSource.getAdmin();
		try {
			hBaseAdmin.disableTable(tableMetadata.getName());
		} catch (IOException e) {
			log.error(e);
			throw new HbaseTableDaoException("disable table in hbase occur error", e);
		}
	}
	@Override
	public void enableTable(TableMetadata tableMetadata) throws HbaseTableDaoException {
		HBaseAdmin hBaseAdmin = hbaseDataSource.getAdmin();
		try {
			hBaseAdmin.enableTable(tableMetadata.getName());
		} catch (IOException e) {
			log.error(e);
			throw new HbaseTableDaoException("enable table in hbase occur error", e);
		}
	}

}
