package com.ailk.oci.ocnosql.client.query;

import java.util.List;

import com.ailk.oci.ocnosql.common.exception.ClientRuntimeException;
import com.ailk.oci.ocnosql.client.query.criterion.Criterion;
import com.ailk.oci.ocnosql.common.config.ColumnFamily;
import com.ailk.oci.ocnosql.common.config.Connection;

public interface Query {

	/**
	 * 条件过滤
	 * @param conn 连接
	 * @param rowkey 
	 * @param tableNames 
	 * @param criterion
	 * @return 结果列表
	 */
	//public List<String[]> query(Connection conn, String rowkey[], List<String> tableNames, Criterion criterion) throws ClientRuntimeException;
	//public List<String[]> query(Connection conn, String rowkey, List<String> tableNames, Criterion criterion) throws ClientRuntimeException;
	
	
	//public List<String[]> query(Connection conn, String rowkey, List<String> tableNames, Criterion criterion, List<ColumnFamily> columnFamilies) throws ClientRuntimeException;
	public List<String[]> query(Connection conn, String rowkey[], List<String> tableNames, Criterion criterion, List<ColumnFamily> columnFamilies) throws ClientRuntimeException;
	
	
	//public List<String[]> query(Connection conn, String startKey, String stopKey, List<String> tableNames, Criterion criterion) throws ClientRuntimeException;
	public List<String[]> query(Connection conn, String startKey, String stopKey, List<String> tableNames, Criterion criterion, List<ColumnFamily> columnFamilies) throws ClientRuntimeException;

	
	public List<String[]> executeSql(Connection conn, String sql) throws ClientRuntimeException;
}
