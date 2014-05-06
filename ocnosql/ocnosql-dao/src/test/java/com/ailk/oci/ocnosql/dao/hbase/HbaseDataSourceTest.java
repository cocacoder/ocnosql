package com.ailk.oci.ocnosql.dao.hbase;

import java.io.IOException;

import org.apache.hadoop.hbase.HTableDescriptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:applicationContext.xml")
public class HbaseDataSourceTest {
	@Autowired
	private HbaseDataSource hbaseDataSource;

	@Test
	public void test(){
		try {
			HTableDescriptor[] tables = hbaseDataSource.getAdmin().listTables();
			System.out.println(tables[0].getNameAsString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
