package com.ailk.oci.ocnosql.model.domains;

/**
 * @author Administrator
 * region元数据
 *
 */
public class RegionMetadata {

	
	private String tableName; //region所属表
	private String name;	//region名称
	private String serverName;	//region所在regionServer
	private String fileNum;	//文件数
	private String fileSize;	//文件大小
	private String startKey;	//startKey
	private String endKey;	//endKey
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public String getFileNum() {
		return fileNum;
	}
	public void setFileNum(String fileNum) {
		this.fileNum = fileNum;
	}
	public String getFileSize() {
		return fileSize;
	}
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
	public String getStartKey() {
		return startKey;
	}
	public void setStartKey(String startKey) {
		this.startKey = startKey;
	}
	public String getEndKey() {
		return endKey;
	}
	public void setEndKey(String endKey) {
		this.endKey = endKey;
	}
	
}
