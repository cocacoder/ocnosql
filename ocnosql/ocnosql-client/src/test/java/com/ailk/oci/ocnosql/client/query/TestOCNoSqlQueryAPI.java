/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ailk.oci.ocnosql.client.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ailk.oci.ocnosql.client.spi.ClientAdaptor;
import com.ailk.oci.ocnosql.common.config.ColumnFamily;
import com.ailk.oci.ocnosql.common.config.Connection;

/**
 * 
 * @author liuxiang2
 */
public class TestOCNoSqlQueryAPI {
	private static Connection conn;
	static String columFam = "columnFamilies";
	static String tablename = "HbaseInitTest";
	static List<ColumnFamily> columnfamilies = new ArrayList<ColumnFamily>();
	String rowkey="9c0138666666666"; // ="9c0138666666666";
	String startKey= "0c7138555555555"; // = "0c7138555555555";
	String endKey="8e22"; // = "8e22";

	@BeforeClass
	public static void before() {
		System.out.println("connect HBase");
		conn = Connection.getInstance();
		if (columFam.equals("NoColumn")) {
			columnfamilies = null;
		} else {
			ColumnFamily cf = new ColumnFamily();
			cf.setFamily("F");//F:name,F:age,F:tel,F:sex,F:addr
			cf.setColumns(new String[] { "name", "age", "tel"});
			columnfamilies.add(cf);
		}

		System.out.println("conn =" + conn.getThreadPool().isTerminated());

	}

	@Test
	public void queryGetSigleKey() {
		System.out.println("begin hbase get ");
		ClientAdaptor client = new ClientAdaptor();
		List<String[]> list = client.queryByRowkey(conn, rowkey,
				Arrays.asList(tablename), null, null, columnfamilies);
		System.out.println("lenth is " + list.size());
		for (String[] result : list) {
			System.out.println("result is " + StringUtils.join(result, ";"));
		}
	}

	@Test
	public void queryGetMultiKey() {
		System.out.println("begin hbase get ");
		ClientAdaptor client = new ClientAdaptor();
		List<String[]> list = client.queryByRowkey(conn, rowkey,
				Arrays.asList(tablename), null, null, columnfamilies);
		System.out.println("lenth is " + list.size());
		for (String[] result : list) {
			System.out.println("result is " + StringUtils.join(result, ";"));
		}
	}

	@Test
	public void queryRowKeyPreFix() {
		System.out.println("begin hbase get ");
		ClientAdaptor client = new ClientAdaptor();
		List<String[]> list = client.queryByRowkeyPrefix(conn, rowkey,
				Arrays.asList(tablename), null, null, columnfamilies);
		System.out.println("lenth is " + list.size());
		for (String[] result : list) {
			System.out.println("result is " + StringUtils.join(result, ";"));
		}
	}

	@Test
	public void queryRowKeyRange() {
		System.out.println("begin hbase get ");
		ClientAdaptor client = new ClientAdaptor();
		List<String[]> list = client.queryByRowkey(conn, startKey, endKey,
				Arrays.asList(tablename), null, null, columnfamilies);
		System.out.println("lenth is " + list.size());
		for (String[] result : list) {
			System.out.println("result is " + StringUtils.join(result, ";"));
		}
	}

	@AfterClass
	public static void after() {
		conn.getThreadPool().shutdown();
	}

}
