package com.ailk.oci.ocnosql.dao.impl;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.NotServingRegionException;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.UnknownRegionException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.catalog.CatalogTracker;
import org.apache.hadoop.hbase.catalog.MetaReader;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.ipc.HMasterInterface;
import org.apache.hadoop.hbase.ipc.HRegionInterface;
import org.apache.hadoop.hbase.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ailk.oci.ocnosql.dao.hbase.BaseHbaseDaoImpl;
import com.ailk.oci.ocnosql.dao.hbase.HbaseDataSource;
import com.ailk.oci.ocnosql.dao.spi.RegionDao;
import com.ailk.oci.ocnosql.dao.spi.RegionRuntimeException;
import com.ailk.oci.ocnosql.model.domains.RegionMetadata;
import com.ailk.oci.ocnosql.model.domains.RegionServerMetadata;
@Service("regionDao")
public class RegionDaoImpl extends BaseHbaseDaoImpl implements RegionDao {
	@Autowired
	private HbaseDataSource hbaseDataSource;

	/* (non-Javadoc)
	 * @see com.ailk.oci.ocnosql.dao.spi.RegionDao#listRegionServers()
	 * 获得RegionServer列表
	 */
	@SuppressWarnings("deprecation")
	@Override
	public List<RegionServerMetadata> listRegionServers() throws RegionRuntimeException {
		List<RegionServerMetadata> regionServerList = new ArrayList<RegionServerMetadata>();
		HBaseAdmin admin = hbaseDataSource.getAdmin();
		try {
			HMasterInterface master = admin.getMaster();
			Collection<ServerName> regionServers = master.getClusterStatus().getServers();
			for(ServerName server : regionServers){
				RegionServerMetadata rsMeta = new RegionServerMetadata();
				String hostName = server.getHostname();
				if(!isIPAddress(hostName)){
					hostName = convertServerName2IP(hostName);
				}
				rsMeta.setHost(hostName);
				rsMeta.setPort(String.valueOf(server.getPort()));
				regionServerList.add(rsMeta);
			}
		} catch (MasterNotRunningException e) {
			String msg = "hbase master is not running,please check hbase-${username}-master-${hostname}.log,cause by " + e;
			throw new RegionRuntimeException(msg);
		} catch (ZooKeeperConnectionException e) {
			String msg = "zookeeper connect faild,please check hbase-${username}-zookeeper-${hostname}.log,cause by " + e;
			throw new RegionRuntimeException(msg);
		} catch (UnknownHostException e) {
			String msg = "servername convert to ip occur error ,cause by " + e;
			throw new RegionRuntimeException(msg);
		} 
		return regionServerList;
	}

	/* (non-Javadoc)
	 * @see com.ailk.oci.ocnosql.dao.spi.RegionDao#majorCompact(java.util.List)
	 * 对Region的列表进行majorCompact
	 */
	@Override
	public boolean majorCompact(List<String> regionNames) throws RegionRuntimeException {
		HBaseAdmin admin = hbaseDataSource.getAdmin();
		for(String regionName : regionNames){
			try {
				admin.majorCompact(regionName);
			} catch (IOException e) {
				String msg = "connect hbase occur error,cause by " + e;
				throw new RegionRuntimeException(msg);
			} catch (InterruptedException e) {
				String msg = "execute majorcompact occur error,cause by " + e;
				throw new RegionRuntimeException(msg);
			} 
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see com.ailk.oci.ocnosql.dao.spi.RegionDao#minroCompact(java.util.List)
	 * 对Region的列表进行microCompact
	 */
	@Override
	public boolean minroCompact(List<String> regionNames)
			throws RegionRuntimeException {
		HBaseAdmin admin = hbaseDataSource.getAdmin();
		for(String regionName : regionNames){
			try {
				admin.compact(regionName);
			} catch (IOException e) {
				String msg = "connect hbase occur error,cause by " + e;
				throw new RegionRuntimeException(msg);
			} catch (InterruptedException e) {
				String msg = "execute minrocompact occur error,cause by " + e;
				throw new RegionRuntimeException(msg);
			} 
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see com.ailk.oci.ocnosql.dao.spi.RegionDao#moveRegion(java.util.List, java.lang.String)
	 * 转移region到目标regionServer上
	 */
	@Override
	public boolean moveRegion(List<String> regionNames, String host)
			throws RegionRuntimeException {
		HBaseAdmin admin = hbaseDataSource.getAdmin();
		
		String rsFullName = null;
		try {
			rsFullName = findRegionServerFullName(admin, host);
		} catch (Exception e1) {
			String msg = "search region server occur error,cause by " + e1;
			throw new RegionRuntimeException(msg);
		} 
		if(rsFullName == null){
			String msg = "can not found region server,region server name : " + host;
			throw new RegionRuntimeException(msg);
		}
		for(String regionName : regionNames){
			try {
				admin.move(HRegionInfo.encodeRegionName(regionName.getBytes()).getBytes(), rsFullName.getBytes());
			} catch (MasterNotRunningException e) {
				String msg = "hbase master is not running,please check hbase-${username}-master-${hostname}.log,cause by " + e;
				throw new RegionRuntimeException(msg);
			} catch (UnknownRegionException e) {
				String msg = "can not found region : " + regionName + "," + e;
				throw new RegionRuntimeException(msg);
			} catch (ZooKeeperConnectionException e) {
				String msg = "zookeeper connect faild,please check hbase-${username}-zookeeper-${hostname}.log,cause by " + e;
				throw new RegionRuntimeException(msg);
			} 
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see com.ailk.oci.ocnosql.dao.spi.RegionDao#scanRegionByTableName(java.lang.String)
	 * 根据表名获取Region信息
	 */
	@Override
	public List<RegionMetadata> scanRegionByTableName(String tableName)
			throws RegionRuntimeException {
		List<RegionMetadata> regionMetaList = new ArrayList<RegionMetadata>();
		HBaseAdmin admin = hbaseDataSource.getAdmin();
		CatalogTracker ct = null;
	    try {
	      ct = new CatalogTracker(admin.getConfiguration());
	      ct.start();
	    } catch (InterruptedException e) {
	      Thread.currentThread().interrupt();
	      throw new RegionRuntimeException("Interrupted", e);
	    } catch (IOException e) {
			throw new RegionRuntimeException("connect hbase occur error", e);
		}
	    try {
			List<Pair<HRegionInfo, ServerName>> pairs = MetaReader.getTableRegionsAndLocations(ct,tableName);
			for(Pair<HRegionInfo, ServerName> pair : pairs){
				String hostName = pair.getSecond().getHostname();
				int port = pair.getSecond().getPort();
				HRegionInfo regionInfo = pair.getFirst();
				HRegionInterface regionInterface = admin.getConnection().getHRegionConnection(hostName, port);
				List<String> storeFiles = regionInterface.getStoreFileList(regionInfo.getRegionName());
				RegionMetadata regionMeta = new RegionMetadata();
				regionMeta.setTableName(tableName);
				regionMeta.setName(regionInfo.getRegionNameAsString());
				regionMeta.setServerName(hostName);
				regionMeta.setFileNum(String.valueOf((storeFiles == null)?0:storeFiles.size()));
				regionMeta.setFileSize("-");
				regionMeta.setStartKey(new String(regionInfo.getStartKey()));
				regionMeta.setEndKey(new String(regionInfo.getEndKey()));
				
				regionMetaList.add(regionMeta);
			}
		} catch (IOException e) {
			String msg = "move region occur error ,cause by " + e;
			throw new RegionRuntimeException(msg);
		} catch (InterruptedException e) {
			String msg = "execute scanRegionByTableName occur error,cause by " + e;
			throw new RegionRuntimeException(msg);
		}
		return regionMetaList;
	}

	/* (non-Javadoc)
	 * @see com.ailk.oci.ocnosql.dao.spi.RegionDao#scanRegionByServerName(java.lang.String)
	 * 根据host和端口获取region元数据
	 */
	@Override
	public List<RegionMetadata> scanRegionByServerName(String hostAndPort)
			throws RegionRuntimeException {
		List<RegionMetadata> regionMetaList = new ArrayList<RegionMetadata>();
		HBaseAdmin admin = hbaseDataSource.getAdmin();
		try {
			String[] serverInfo = hostAndPort.split(":");
			HRegionInterface regionInterface = admin.getConnection().getHRegionConnection(serverInfo[0], Integer.parseInt(serverInfo[1]));
			List<HRegionInfo> onlineRegions = regionInterface.getOnlineRegions();
			for(HRegionInfo region : onlineRegions){
				List<String> storeFiles = regionInterface.getStoreFileList(region.getRegionName());
				
				RegionMetadata regionMeta = new RegionMetadata();
				regionMeta.setName(region.getRegionNameAsString());
				regionMeta.setTableName(new String(region.getTableName()));
				regionMeta.setServerName(hostAndPort);
				regionMeta.setFileNum(String.valueOf((storeFiles == null)?0:storeFiles.size()));
				regionMeta.setFileSize("-");
				regionMeta.setStartKey(new String(region.getStartKey()));
				regionMeta.setEndKey(new String(region.getEndKey()));
				regionMetaList.add(regionMeta);
			}
		} catch (ConnectException e) {
			String msg = "zookeeper could not connect,cause by " + e;
			throw new RegionRuntimeException(msg);
		} catch (NotServingRegionException e) {
			String msg = "region is offline,cause by " + e;
			throw new RegionRuntimeException(msg);
		} catch (IOException e) {
			String msg = "hbase could not connect,cause by " + e;
			throw new RegionRuntimeException(msg);
		}
		return regionMetaList;
	}

	@Override
	public boolean splitRegion(List<String> regionNames)
			throws RegionRuntimeException {
		HBaseAdmin admin = hbaseDataSource.getAdmin();
		for(String regionName : regionNames){
			try {
				admin.split(regionName);
			} catch (MasterNotRunningException e) {
				String msg = "hbase master is not running,please check hbase-${username}-master-${hostname}.log,cause by " + e;
				throw new RegionRuntimeException(msg);
			} catch (ZooKeeperConnectionException e) {
				String msg = "zookeeper connect faild,please check hbase-${username}-zookeeper-${hostname}.log,cause by " + e;
				throw new RegionRuntimeException(msg);
			} catch (IOException e) {
				String msg = "split region occur error ,cause by " + e;
				throw new RegionRuntimeException(msg);
			} catch (InterruptedException e) {
				String msg = "execute split occur error,cause by " + e;
				throw new RegionRuntimeException(msg);
			}
		}
		return true;
	}

	
	public void setHbaseDataSource(HbaseDataSource hbaseDataSource) {
		this.hbaseDataSource = hbaseDataSource;
	}
}
