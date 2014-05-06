package com.ailk.oci.ocnosql.dao.spi;

import java.util.List;

import com.ailk.oci.ocnosql.model.domains.RegionMetadata;
import com.ailk.oci.ocnosql.model.domains.RegionServerMetadata;

/**
 * @author Administrator
 * RegionDao操作
 */
public interface RegionDao {

	//列出表的Region列表
	public List<RegionMetadata> scanRegionByTableName(String tableName) throws RegionRuntimeException;
	
	//region迁移
	public boolean moveRegion(List<String> regionNames, String host) throws RegionRuntimeException;
	
	//microCompact
	public boolean minroCompact(List<String> regionNames) throws RegionRuntimeException;
	
	//majorCompact
	public boolean majorCompact(List<String> regionNames) throws RegionRuntimeException;
	
	//splitRegion
	public boolean splitRegion(List<String> regionNames) throws RegionRuntimeException;
	
	//列出RegionServer列表
	public List<RegionServerMetadata> listRegionServers() throws RegionRuntimeException;
	
	//列出RegionServer上的Region列表
	public List<RegionMetadata> scanRegionByServerName(String hostAndPort) throws RegionRuntimeException;
}
