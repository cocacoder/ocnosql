package com.ailk.oci.ocnosql.dao.hbase;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ailk.oci.ocnosql.dao.spi.RegionDao;
import com.ailk.oci.ocnosql.model.domains.RegionMetadata;
import com.ailk.oci.ocnosql.model.domains.RegionServerMetadata;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:applicationContext.xml")
public class RegionDaoImplTest {
	@Autowired
	private RegionDao regionDao;
	
	@Ignore
	public void testListRegionServers(){
		List<RegionServerMetadata> rsList = regionDao.listRegionServers();
		for(RegionServerMetadata rsMeta : rsList){
			System.out.println(rsMeta.getHost());
		}
	}
	
	@Ignore
	public void testMajorCompact(){
		List<String> regionNames = new ArrayList<String>();
		regionNames.add("dr_query20120108,,1353406198148.207721b77f3536d534f7202297bcb350.");
		regionDao.majorCompact(regionNames);
	}

	@Ignore
	public void testMinorCompact(){
		List<String> regionNames = new ArrayList<String>();
		regionNames.add("dr_query20130101,,1356492657135.d1b10f2d81ee4f5f1ffa53797147c896.");
		regionDao.minroCompact(regionNames);
	}

	@Ignore
	public void testMoveRegion(){
		List<String> regionNames = new ArrayList<String>();
		regionNames.add("dr_query20130101,6f218721455521,1358863121441.8c1ecb3d25e28f9ae88a92a7c1578d84.");
		regionDao.moveRegion(regionNames, "10.10.140.166");
	}

	@Ignore
	public void testScanRegionByTableName(){
		String tableName = "dr_query20130101";
		List<RegionMetadata> regionList = regionDao.scanRegionByTableName(tableName);
		for(RegionMetadata region : regionList){
			System.out.println(region.getName());
		}
	}
	
	@Test
	public void testScanRegionServerByName(){
		String serverName = "10.10.140.166";
		List<RegionMetadata> regionList = regionDao.scanRegionByServerName(serverName);
		for(RegionMetadata region : regionList){
			System.out.println(region.getName());
		}
	}
	
	@Ignore
	public void testSplitRegion(){
		List<String> regionNames = new ArrayList<String>();
		regionNames.add("dr_query20130101,,1356492657135.d1b10f2d81ee4f5f1ffa53797147c896.");
		regionDao.splitRegion(regionNames);
	}
}
