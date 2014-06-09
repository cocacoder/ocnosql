package com.ailk.oci.ocnosql.client.spi;

import java.util.List;
import java.util.Map;

import com.ailk.oci.ocnosql.client.query.criterion.Criterion;
import com.ailk.oci.ocnosql.common.config.ColumnFamily;
import com.ailk.oci.ocnosql.common.config.Connection;
import com.ailk.oci.ocnosql.common.exception.ClientRuntimeException;

/**
 * OCNOSQL客户端接口，目前有两个实现类：
 * ClientSingleColumn 单列查询
 * ClientMultiColumn 多列查询
 */
public interface Client {
	
	public List<String[]> queryByRowkey(
			Connection conn, 
			String rowkey, 
			List<String> tableNames, 
			Criterion criterion, 
			Map<String, String> param
			) throws ClientRuntimeException;
	
	
	public List<String[]> queryByRowkey(
			Connection conn, 
			String rowkey, 
			List<String> tableNames, 
			Criterion criterion, 
			Map<String, String> param,
			List<ColumnFamily> columnFamilies) throws ClientRuntimeException;
	
	/**
	 * 通过Rowkey查询
	 * @param rowkey rowkey值
	 * @param tableNames 需要查询的表的名称列表
	 * @param criterion 过滤条件列表
	 * @param param 输入参数
	 * @return
	 * @throws ClientRuntimeException
	 */
	public List<String[]> queryByRowkey(
			Connection conn, 
			String rowkey[], 
			List<String> tableNames, 
			Criterion criterion, 
			Map<String, String> param
			) throws ClientRuntimeException;
	
	
	public List<String[]> queryByRowkey(
			Connection conn, 
			String rowkey[], 
			List<String> tableNames, 
			Criterion criterion, 
			Map<String, String> param,
			List<ColumnFamily> columnFamilies) throws ClientRuntimeException;
	
	
	public List<String[]> queryByRowkey(
			Connection conn, 
			String startKey, 
			String stopKey,
			List<String> tableNames, 
			Criterion criterion, 
			Map<String, String> param
			) throws ClientRuntimeException;
	
	
	public List<String[]> queryByRowkey(
			Connection conn, 
			String startKey, 
			String stopKey,
			List<String> tableNames, 
			Criterion criterion, 
			Map<String, String> param,
			List<ColumnFamily> columnFamilies) throws ClientRuntimeException;
	
	
	/**
	 * 通过SQL查询
	 * @param sql sql 需要执行的sql
	 * @param param 输入参数
	 * @return
	 * @throws ClientRuntimeException
	 */
	public List<String[]> queryBySql(
			String sql, 
			Map<String, String> param
			) throws ClientRuntimeException;
	
	/**
	 * * 结果
	 * @return
	 */
	public Map<String, Object> getResultMap();
}
