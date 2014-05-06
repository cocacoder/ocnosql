package com.ailk.oci.ocnosql.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.ailk.oci.ocnosql.dao.hbase.HbaseDataSource;
import com.ailk.oci.ocnosql.model.domains.RegionMetadata;
import com.ailk.oci.ocnosql.model.domains.RegionServerMetadata;

public class RegionDaoImplTest {
	
	private RegionDaoImpl regionDao;
	
	@Before
	public void setUp(){
		HbaseDataSource dataSource = new HbaseDataSource();
		dataSource.setZkHost("ocdata03,ocdata04,ocdata05,ocdata00,ocdata01");
		dataSource.setZkClientPort("2383");
		dataSource.setTablePoolMaxSize(100);
		dataSource.init1();
		
		regionDao = new RegionDaoImpl();
		regionDao.setHbaseDataSource(dataSource);
	}
	
	@Ignore
	public void listRegionServersTest(){
		
		List<RegionServerMetadata> regionServers = regionDao.listRegionServers();
		for(RegionServerMetadata regionServer : regionServers){
			System.out.println(regionServer.getHost() + " : " + regionServer.getPort());
		}
	}
	
	@Ignore
	public void scanRegionByServerNameTest(){
		
		List<RegionMetadata> regionServers = regionDao.scanRegionByServerName("ocdata00:60021");
		for(RegionMetadata region : regionServers){
			System.out.println("servername : " + region.getServerName() 
					+ ", tablename : " + region.getTableName()
					+ ", filenum: " + region.getFileNum()
					+ ", filesize : " + region.getFileSize()
					+ ", startKey : " + region.getStartKey()
					+ ", endKey : " + region.getEndKey());
		}
		System.out.println("size : " + regionServers.size());
	}
	
	@Ignore
	public void scanRegionByTableNameTest(){
		
		List<RegionMetadata> regionServers = regionDao.scanRegionByTableName("zhuangyang");
		for(RegionMetadata region : regionServers){
			System.out.println("servername : " + region.getServerName() 
					+ ", tablename : " + region.getTableName()
					+ ", filenum: " + region.getFileNum()
					+ ", filesize : " + region.getFileSize()
					+ ", startKey : " + region.getStartKey()
					+ ", endKey : " + region.getEndKey());
		}
		System.out.println("size : " + regionServers.size());
	}
	
	@Ignore
	public void minroCompactTest(){
		
		List<RegionMetadata> regionServers = regionDao.scanRegionByTableName("zhuangyang");
		List<String> regionNames = new ArrayList<String>();
		for(RegionMetadata region : regionServers){
			System.out.println("servername : " + region.getServerName() 
					+ ", tablename : " + region.getTableName()
					+ ", filenum: " + region.getFileNum()
					+ ", filesize : " + region.getFileSize()
					+ ", startKey : " + region.getStartKey()
					+ ", endKey : " + region.getEndKey());
			regionNames.add(region.getName());
		}
		System.out.println("size : " + regionServers.size());
		
		regionDao.minroCompact(regionNames);
		System.out.println("----------------complete minro compact----------------");
		
		regionServers = regionDao.scanRegionByTableName("zhuangyang");
		for(RegionMetadata region : regionServers){
			System.out.println("servername : " + region.getServerName() 
					+ ", tablename : " + region.getTableName()
					+ ", filenum: " + region.getFileNum()
					+ ", filesize : " + region.getFileSize()
					+ ", startKey : " + region.getStartKey()
					+ ", endKey : " + region.getEndKey());
		}
		System.out.println("size : " + regionServers.size());
	}
	
	@Ignore
	public void majorCompactTest(){
		
		List<RegionMetadata> regionServers = regionDao.scanRegionByTableName("zhuangyang");
		List<String> regionNames = new ArrayList<String>();
		for(RegionMetadata region : regionServers){
			System.out.println("servername : " + region.getServerName() 
					+ ", tablename : " + region.getTableName()
					+ ", filenum: " + region.getFileNum()
					+ ", filesize : " + region.getFileSize()
					+ ", startKey : " + region.getStartKey()
					+ ", endKey : " + region.getEndKey());
			regionNames.add(region.getName());
		}
		System.out.println("size : " + regionServers.size());
		
		regionDao.majorCompact(regionNames);
		System.out.println("----------------complete major compact----------------");
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		regionServers = regionDao.scanRegionByTableName("zhuangyang");
		for(RegionMetadata region : regionServers){
			System.out.println("servername : " + region.getServerName() 
					+ ", tablename : " + region.getTableName()
					+ ", filenum: " + region.getFileNum()
					+ ", filesize : " + region.getFileSize()
					+ ", startKey : " + region.getStartKey()
					+ ", endKey : " + region.getEndKey());
		}
		System.out.println("size : " + regionServers.size());
	}
	
	@Ignore
	public void splitRegionTest(){
		
		List<RegionMetadata> regionServers = regionDao.scanRegionByTableName("zhuangyang");
		List<String> regionNames = new ArrayList<String>();
		for(RegionMetadata region : regionServers){
			System.out.println("servername : " + region.getServerName() 
					+ ", tablename : " + region.getTableName()
					+ ", filenum: " + region.getFileNum()
					+ ", filesize : " + region.getFileSize()
					+ ", startKey : " + region.getStartKey()
					+ ", endKey : " + region.getEndKey());
			regionNames.add(region.getName());
		}
		System.out.println("size : " + regionServers.size());
		
		regionDao.splitRegion(regionNames);
		System.out.println("----------------complete split region----------------");
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		regionServers = regionDao.scanRegionByTableName("zhuangyang");
		for(RegionMetadata region : regionServers){
			System.out.println("servername : " + region.getServerName() 
					+ ", tablename : " + region.getTableName()
					+ ", filenum: " + region.getFileNum()
					+ ", filesize : " + region.getFileSize()
					+ ", startKey : " + region.getStartKey()
					+ ", endKey : " + region.getEndKey());
		}
		System.out.println("size : " + regionServers.size());
	}
	
	@Test
	public void moveRegionTest(){
		
		List<RegionMetadata> regionServers = regionDao.scanRegionByTableName("zhuangyang");
		List<String> regionNames = new ArrayList<String>();
		for(RegionMetadata region : regionServers){
			System.out.println("servername : " + region.getServerName() 
					+ ", tablename : " + region.getTableName()
					+ ", filenum: " + region.getFileNum()
					+ ", filesize : " + region.getFileSize()
					+ ", startKey : " + region.getStartKey()
					+ ", endKey : " + region.getEndKey());
			regionNames.add(region.getName());
		}
		System.out.println("size : " + regionServers.size());
		
		regionDao.moveRegion(regionNames, "10.1.253.90");
		System.out.println("----------------complete move region----------------");
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		regionServers = regionDao.scanRegionByTableName("zhuangyang");
		for(RegionMetadata region : regionServers){
			System.out.println("servername : " + region.getServerName() 
					+ ", tablename : " + region.getTableName()
					+ ", filenum: " + region.getFileNum()
					+ ", filesize : " + region.getFileSize()
					+ ", startKey : " + region.getStartKey()
					+ ", endKey : " + region.getEndKey());
		}
		System.out.println("size : " + regionServers.size());
	}
}
