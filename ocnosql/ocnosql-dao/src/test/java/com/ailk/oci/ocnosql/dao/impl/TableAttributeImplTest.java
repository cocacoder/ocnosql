package com.ailk.oci.ocnosql.dao.impl;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ailk.oci.ocnosql.dao.criteria.Criteria;
import com.ailk.oci.ocnosql.dao.spi.TableAttributeDao;
import com.ailk.oci.ocnosql.model.domains.TableAttributeMetadata;
import com.ailk.oci.ocnosql.model.domains.TableMetadata;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:applicationContext.xml")
public class TableAttributeImplTest {
	@Autowired
	private TableAttributeDao tableAttributeDao;
	@Ignore
	public void testSaveTableAttributeMetadata() {
		TableAttributeMetadata tableAttributeInstance = new TableAttributeMetadata();
		tableAttributeInstance.setAttributeKey("compress");
		tableAttributeInstance.setAttributeValue("LZO");
		tableAttributeInstance.setShowType("select");
		tableAttributeInstance.setAttributeType("string");
		TableMetadata tableMetadata = new TableMetadata();
		tableMetadata.setId("34B993697F001343D511E440F5E5A");
		tableAttributeDao.saveTableAttributeMetadata(tableAttributeInstance);
	}

	@Test
	public void testGetTableAttributeMetadataInstance() {
		TableAttributeMetadata tableAttributeMetadata = tableAttributeDao.getTableAttributeMetadataInstance("");
	}

	@Ignore
	public void testDeleteTableAttributeMetadata() {
		TableAttributeMetadata tableAttributeInstance = new TableAttributeMetadata();
		tableAttributeDao.deleteTableAttributeMetadata(tableAttributeInstance);
	}

	@Ignore
	public void testUpdateTableAttributeMetadata() {
		TableAttributeMetadata tableAttributeInstance = new TableAttributeMetadata();
		tableAttributeDao.updateTableAttributeMetadata(tableAttributeInstance);
	}

	@Ignore
	public void testQueryTableAttributeMetadataByEQ() {
		Criteria criteria = new Criteria();
		criteria.addEqCriterion("attributeValue", "dd");
		List<TableAttributeMetadata> list = 
				tableAttributeDao.queryTableAttributeMetadataByCriteria(criteria);
	}

}
