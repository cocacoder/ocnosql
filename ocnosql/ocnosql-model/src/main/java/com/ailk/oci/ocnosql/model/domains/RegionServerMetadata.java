package com.ailk.oci.ocnosql.model.domains;

/**
 * @author Administrator
 * RegionServer元数据
 */
public class RegionServerMetadata {

	private String host; //所在主机
	private String port;	//regionServer端口
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
}
