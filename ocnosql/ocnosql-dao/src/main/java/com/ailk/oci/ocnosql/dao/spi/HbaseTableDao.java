package com.ailk.oci.ocnosql.dao.spi;

import com.ailk.oci.ocnosql.model.domains.TableMetadata;

/**
 * @author Administrator
 * HBase表操作DAO
 */
public interface HbaseTableDao {
	//创建表
	void createTable(TableMetadata tableMetadata) throws HbaseTableDaoException;
	
	//删除表
	void deleteTable(TableMetadata tableMetadata) throws HbaseTableDaoException;
	
	//disable表
	void disableTable(TableMetadata tableMetadata) throws HbaseTableDaoException;
	
	//enable表
	void enableTable(TableMetadata tableMetadata) throws HbaseTableDaoException;
}
