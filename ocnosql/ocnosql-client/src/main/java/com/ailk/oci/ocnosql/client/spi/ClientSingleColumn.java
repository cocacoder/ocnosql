package com.ailk.oci.ocnosql.client.spi;

import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;

import com.ailk.oci.ocnosql.common.exception.ClientConnectionException;
import com.ailk.oci.ocnosql.common.exception.ClientRuntimeException;
import com.ailk.oci.ocnosql.client.query.Query;
import com.ailk.oci.ocnosql.client.query.QuerySingleColumn;
import com.ailk.oci.ocnosql.client.query.criterion.Criterion;
import com.ailk.oci.ocnosql.common.config.ColumnFamily;
import com.ailk.oci.ocnosql.common.config.Connection;

/**
 * @author Administrator
 * 单列查询的客户端实现类
 *
 */
public class ClientSingleColumn implements Client {
	private final static Log log = LogFactory.getLog(ClientSingleColumn.class.getSimpleName());
	private Connection connection;
	private String msg;
	private Map<String, Object> resultMap;
	
	public ClientSingleColumn(){
	}
	
	public ClientSingleColumn(Connection connection){
		this.connection = connection;
	}
	
	@Override
	public List<String[]> queryBySql(String sql, Map<String, String> param)
			throws ClientRuntimeException {
		checkConnection();
		Configuration conf = connection.getConf();
		Query query = new QuerySingleColumn(); 
		if(param != null && param.size() > 0){
			for(String key : param.keySet()){
				conf.set(key, param.get(key));
			}
		}
		return query.executeSql(connection, sql);
	}
	
	private void checkConnection() throws ClientConnectionException {
		if(connection == null){
			msg = "Not connect ocnosql, cause by not set conection.Please invok setConnection()" ;
			log.error(msg);
			throw new ClientConnectionException(msg);
		}
	}

	private void checkConnection(Connection connection) throws ClientConnectionException {
		if(connection == null){
			msg = "Not connect ocnosql, cause by not set conection.Please invok setConnection()" ;
			log.error(msg);
			throw new ClientConnectionException(msg);
		}
	}
	
	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	public Map<String, Object> getResultMap() {
		return resultMap;
	}

	@Override
	public List<String[]> queryByRowkey(Connection conn, String rowkey,
			List<String> tableNames, Criterion criterion,
			Map<String, String> param) throws ClientRuntimeException {
		return queryByRowkey(conn, new String[]{rowkey}, tableNames, criterion, param, null);
	}

	@Override
	public List<String[]> queryByRowkey(Connection conn, String rowkey,
			List<String> tableNames, Criterion criterion,
			Map<String, String> param, List<ColumnFamily> columnFamilies)
			throws ClientRuntimeException {
		return queryByRowkey(conn, new String[]{rowkey}, tableNames, criterion, param, columnFamilies);
	}
	

	@Override
	public List<String[]> queryByRowkey(Connection conn, String[] rowkey,
			List<String> tableNames, Criterion criterion,
			Map<String, String> param, List<ColumnFamily> columnFamilies)
			throws ClientRuntimeException {
		checkConnection(conn);
		Configuration conf = conn.getConf();
		Query query = new QuerySingleColumn(); 
		if(param != null && param.size() > 0){
			for(String key : param.keySet()){
				conf.set(key, param.get(key));
			}
		}
		return query.query(conn, rowkey, tableNames, criterion, null);
	}

	
	@Override
	public List<String[]> queryByRowkey(Connection conn, String startKey,
			String stopKey, List<String> tableNames, Criterion criterion,
			Map<String, String> param) throws ClientRuntimeException {
		return queryByRowkey(conn, startKey, stopKey, tableNames, criterion, param, null);
	}

	
	/**
	 * 对于单列存储，columnFamilies应置为空
	 */
	@Override
	public List<String[]> queryByRowkey(Connection conn, String startKey,
			String stopKey, List<String> tableNames, Criterion criterion,
			Map<String, String> param, List<ColumnFamily> columnFamilies)
			throws ClientRuntimeException {
		checkConnection(conn);
		Configuration conf = conn.getConf();
		Query query = new QuerySingleColumn(); 
		if(param != null && param.size() > 0){
			for(String key : param.keySet()){
				conf.set(key, param.get(key));
			}
		}
		
		return query.query(conn, startKey, stopKey, tableNames, criterion, null);
	}

	
	@Override
	public List<String[]> queryByRowkey(Connection conn, String[] rowkey,
			List<String> tableNames, Criterion criterion,
			Map<String, String> param) throws ClientRuntimeException {
		return queryByRowkey(conn, rowkey, tableNames, criterion, param, null);
	}

}
