package com.ailk.oci.ocnosql.dao.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ailk.oci.ocnosql.dao.spi.TableDao;
import com.ailk.oci.ocnosql.dao.spi.TableTypeDao;
import com.ailk.oci.ocnosql.model.domains.TableAttributeMetadata;
import com.ailk.oci.ocnosql.model.domains.TableMetadata;
import com.ailk.oci.ocnosql.model.domains.TableTypeMetadata;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:applicationContext.xml")
public class TableDaoImplTest {
	@Autowired
	private TableDao tableDao;
	@Autowired
	private TableTypeDao tableTypeDao;
	@Test
//	@Ignore
	public void testCreateTable() {
		TableMetadata tableMetadata = new TableMetadata();
		tableMetadata.setColumns("{\"ab\",\"cd\",\"ef\"}");
		tableMetadata.setName("dr_query20130117");
		tableMetadata.setStatus("Active");
		
		TableTypeMetadata tableTypeMetadata = tableTypeDao.list().get(0);
		tableMetadata.setType(tableTypeMetadata);
		
		Set<TableAttributeMetadata> tableAttrs = new HashSet<TableAttributeMetadata>();
		TableAttributeMetadata tableAttrMeta = new TableAttributeMetadata();
		tableAttrMeta.setAttributeKey("regionNum1");
		tableAttrMeta.setAttributeValue("1000");
		tableAttrMeta.setAttributeType("String");
		tableAttrMeta.setShowType("checkbox");
		tableAttrMeta.setId(tableDao.getKeyGenerator().generateKey());
		tableAttrMeta.setTableMetadata(tableMetadata);
		tableAttrs.add(tableAttrMeta);
		tableMetadata.setTableAttrs(tableAttrs);
		
		tableDao.saveTable(tableMetadata);
	}

	@Ignore
	public void testDeleteTable() {
		TableMetadata tableMetadata = new TableMetadata();
		tableMetadata.setId("701F5DDBA3681DEA3824603007E");
		tableDao.deleteTable(tableMetadata);
		TableMetadata tableMetadataIndatasource = tableDao.getTable("701F5DDBA3681DEA3824603007E");
		Assert.assertNull(tableMetadataIndatasource);
	}
//	@Test
	@Ignore
	public void testUpdateTable() {
		TableMetadata tableMetadata = new TableMetadata();
		tableMetadata.setId("61E279DFA3147CEC59BCD529AC3");
		tableMetadata.setColumns("{\"ab-copy1\",\"cd-copy1\",\"ef-copy1\"}");
		tableMetadata.setName("dr_query20120113-1");
		tableMetadata.setStatus("Active");
		
		TableTypeMetadata tableTypeMetadata = tableTypeDao.list().get(0);
		tableMetadata.setType(tableTypeMetadata);
		
		tableDao.updateTable(tableMetadata);
		TableMetadata tableMetadataIndatasource = tableDao.getTable("61E279DFA3147CEC59BCD529AC3");
		Assert.assertEquals("dr_query20120113-1-copy",tableMetadataIndatasource.getName());
	}
//	@Test
	@Ignore
	public void testGetTableById() {
		TableMetadata tableMetadata = tableDao.getTable("705854AEA36816B277F2BCBEFC8");
		Set<TableAttributeMetadata> attributes = tableMetadata.getTableAttrs();
		for(TableAttributeMetadata tableAttributeMetadata:attributes){
			System.out.println(tableAttributeMetadata.getAttributeKey());
		}
		Assert.assertNotNull(tableMetadata);
	}
	@Ignore
//	@Test
	public void testList(){
		List<TableMetadata> list = tableDao.list();
		for(TableMetadata metadata:list){
			System.out.println(metadata.getName());
		}
	}
}
