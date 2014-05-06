package com.ailk.oci.ocnosql.dao.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ailk.oci.ocnosql.dao.criteria.Criteria;
import com.ailk.oci.ocnosql.dao.spi.TableDao;
import com.ailk.oci.ocnosql.dao.spi.ViewDao;
import com.ailk.oci.ocnosql.model.domains.TableMetadata;
import com.ailk.oci.ocnosql.model.domains.ViewMetadata;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:applicationContext.xml")
public class ViewDaoImplTest {
	@Autowired
	private ViewDao viewDao;
	@Autowired
	private TableDao tableDao;
	
//	@Test
	@Ignore
	public void testSaveView(){
		Criteria criteria = new Criteria();
		criteria.addEqCriterion("name", "dr_query20120117");
		List<TableMetadata> tableList = tableDao.queryTableByCriteria(criteria);
		
		Set<TableMetadata> tables = new HashSet<TableMetadata>();
		for(TableMetadata table : tableList){
			tables.add(table);
		}
		
		ViewMetadata viewInstance = new ViewMetadata();
		viewInstance.setName("zhuangyang_view");
		viewInstance.setTables(tables);
		viewDao.saveView(viewInstance);
	}
	
//	@Test
	@Ignore
	public void testList(){
		List<ViewMetadata> viewList = viewDao.list();
		for(ViewMetadata view : viewList){
			System.out.println(view.getName());
		}
	}
	
//	@Test
	@Ignore
	public void testGetView(){
		ViewMetadata view = viewDao.getView("716CF815A36862AD0DC037D54C");
		System.out.println(view.getName());
	}
	
//	@Test
	@Ignore
	public void testUpdateView(){
		ViewMetadata view = new ViewMetadata(); 
		view.setId("716CF815A36862AD0DC037D54C");
		view.setName("view_test");
		viewDao.updateView(view);
		ViewMetadata viewMetadata = viewDao.getView("716CF815A36862AD0DC037D54C");
		System.out.println(viewMetadata.getName());
	}
	
//	@Test
	@Ignore
	public void testQueryViewByCriteria(){
		Criteria criteria = new Criteria();
		criteria.addEqCriterion("name", "view_test");
		List<ViewMetadata> viewList = viewDao.queryViewByCriteria(criteria);
		for(ViewMetadata view : viewList){
			System.out.println(view.getName());
		}
	}
	
	@Test
//	@Ignore
	public void testDeleteView(){
		ViewMetadata view = new ViewMetadata(); 
		view.setId("716CF815A36862AD0DC037D54C");
		view.setName("view_test");
		viewDao.deleteView(view);
	}
}
