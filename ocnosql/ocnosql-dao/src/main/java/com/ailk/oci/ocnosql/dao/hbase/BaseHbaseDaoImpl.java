package com.ailk.oci.ocnosql.dao.hbase;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.zookeeper.ZKUtil;
import org.apache.hadoop.hbase.zookeeper.ZooKeeperWatcher;
import org.apache.hadoop.hbase.zookeeper.ZKUtil.NodeAndData;
import org.apache.zookeeper.KeeperException;

public class BaseHbaseDaoImpl {
	
	/**
	 * @param admin HBaseAdmin实例
	 * @param regionServerName RegionServer名称
	 * @return 
	 * @throws IOException
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	protected String findRegionServerFullName(HBaseAdmin admin, String regionServerName) throws IOException, KeeperException, InterruptedException{
		String rsFullName = null;
		ZooKeeperWatcher watcher = admin.getConnection().getZooKeeperWatcher();
		if(isIPAddress(regionServerName)){
			String serverName = convertIP2ServerName(regionServerName);
			List<String> nodeList = ZKUtil.listChildrenAndWatchForNewChildren(watcher, "/hbase/rs");//getChildDataAndWatchForNewChildren(watcher, "/hbase/rs");
			for(String node : nodeList){
				if(node.contains(serverName)){
					rsFullName = node;
					break;
				}
			}
		}
		return rsFullName;
	}
	
	protected boolean isIPAddress(String serverName){
		Pattern pattern = Pattern.compile("^[1-2]{0,1}[0-9]{1,2}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}");
		return pattern.matcher(serverName).matches();
	}
	
	protected String convertServerName2IP(String serverName) throws UnknownHostException{
		InetAddress address = InetAddress.getByName(serverName);
		return address.getHostAddress();
	}
	
	protected String convertIP2ServerName(String ip) throws UnknownHostException{
		InetAddress address = InetAddress.getByName(ip);
		return address.getHostName();
	}
}
