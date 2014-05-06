package com.ailk.oci.ocnosql.dao.impl;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ailk.oci.ocnosql.dao.criteria.Criteria;
import com.ailk.oci.ocnosql.dao.spi.TableTypeAttributeMetadataDao;
import com.ailk.oci.ocnosql.dao.spi.TableTypeDao;
import com.ailk.oci.ocnosql.model.domains.TableTypeAttributeMetadata;
import com.ailk.oci.ocnosql.model.domains.TableTypeMetadata;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:applicationContext.xml")
public class TableTypeImplTest {
	@Autowired
	private TableTypeDao tableTypeDao;
	@Autowired
	private TableTypeAttributeMetadataDao tableTypeAttrDao;

	@Ignore
	public void testDeleteTableType() {
		TableTypeMetadata tableTypeInstance = new TableTypeMetadata();
		tableTypeInstance.setId("576A2F6BA36862974E56567F20");
		tableTypeDao.deleteTableType(tableTypeInstance);
	}

	@Ignore
	public void testGetTableType() {
		TableTypeMetadata tableTypeMeta = tableTypeDao.getTableType("576A2F6BA36862974E56567F20");
		System.out.println(tableTypeMeta.getName());
	}

	@Ignore
	public void testList() {
		List<TableTypeMetadata> tableTypeList = tableTypeDao.list();
		for(TableTypeMetadata tableTypeMeta : tableTypeList){
			System.out.println(tableTypeMeta.getName());
		}
	}

	@Ignore
	public void testQueryTableByEQ() {
		Criteria mycriteria = new Criteria();
		mycriteria.addEqCriterion("name", "详单1");
		tableTypeDao.queryTableTypeByCriteria(mycriteria);
	}

	@Test
	public void testSaveTableType() {
		TableTypeMetadata tableTypeInstance = new TableTypeMetadata();
		tableTypeInstance.setName("详单");
		String id = tableTypeDao.saveTableType(tableTypeInstance);
		System.out.println(id);
		
		TableTypeAttributeMetadata typeMetadata1 = new TableTypeAttributeMetadata();
		typeMetadata1.setAttributeKey("compress");
		typeMetadata1.setAttributeValue("{\"GZ\",\"LZO\",\"SNAPPY\"}");
		typeMetadata1.setAttributeType("string");
		typeMetadata1.setShowType("redio");
		tableTypeAttrDao.saveTableTypeAttr(typeMetadata1);
		
		TableTypeAttributeMetadata typeMetadata2 = new TableTypeAttributeMetadata();
		typeMetadata2.setAttributeKey("regionNum");
		typeMetadata2.setAttributeValue("120");
		typeMetadata2.setAttributeType("string");
		typeMetadata2.setShowType("text");
		tableTypeAttrDao.saveTableTypeAttr(typeMetadata2);
		
	}

	@Ignore
	public void testUpdateTableType() {
		TableTypeMetadata tableTypeInstance = tableTypeDao.getTableType("576A2F6BA36862974E56567F20");
		tableTypeInstance.setName("详单1");
		tableTypeDao.updateTableType(tableTypeInstance);
	}
}
