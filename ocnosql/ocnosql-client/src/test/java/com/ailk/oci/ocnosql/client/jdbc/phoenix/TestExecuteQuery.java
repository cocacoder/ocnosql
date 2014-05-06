package com.ailk.oci.ocnosql.client.jdbc.phoenix;

import java.sql.ResultSet;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.ailk.oci.ocnosql.client.jdbc.HbaseJdbcHelper;

public class TestExecuteQuery {
	 @Test
	// 使用statement
	public void executeQuery() {
//		String sql = "select * from student where age>=25 order by age desc";
		String sql = "select count(1) a from  DW_COC_LABEL_USER_006 where L1_02_05='1'";
		HbaseJdbcHelper help = null;
		try {
			help = new PhoenixJdbcHelper();
			System.out.println("start1------------------------"+new Date().toString());
			ResultSet rs = help.executeQueryRaw(sql);
			while (rs.next()) {
				System.out.print("count=" + rs.getString("a"));
//				System.out.print(",stuid=" + rs.getString("name"));
//				System.out.print(",age=" + rs.getString("age"));
//				System.out.print(",score=" + rs.getString("score"));
//				System.out.print(",classid=" + rs.getString("classid"));
			}
			System.out.println("end1------------------------"+new Date().toString());
			System.out.println("start2------------------------"+new Date().toString());
			ResultSet rs2 = help.executeQueryRaw(sql);
			while (rs2.next()) {
				System.out.print("count=" + rs2.getString("a"));
//				System.out.print(",stuid=" + rs.getString("name"));
//				System.out.print(",age=" + rs.getString("age"));
//				System.out.print(",score=" + rs.getString("score"));
//				System.out.print(",classid=" + rs.getString("classid"));
			}
			System.out.println("end2------------------------"+new Date().toString());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		} finally {
			try {
				help.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

//	@Test
//	// 使用preparedStatement
//	public void TestExecuteQuery1() {
//		String sql = "select * from student where age>=? order by age desc";
//		HbaseJdbcHelper help = null;
//		try {
//			help = new PhoenixJdbcHelper();
//			Object[] args = new Object[] { 25 };
//			ResultSet rs = help.executeQueryRaw(sql, args);
//			while (rs.next()) {
//				System.out.print("stuid=" + rs.getString("stuid"));
//				System.out.print(",stuid=" + rs.getString("name"));
//				System.out.print(",age=" + rs.getString("age"));
//				System.out.print(",score=" + rs.getString("score"));
//				System.out.print(",classid=" + rs.getString("classid"));
//				System.out.println("------------------------");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			Assert.fail();
//		} finally {
//			try {
//				help.close();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	// 以下方法测试返回包装后结果的查询方法
//
//	@Test
//	// 使用statement
//	public void TestExecuteQuery3() {
//		String sql = "select * from student where age>=25 order by age desc";
//		HbaseJdbcHelper help = null;
//		try {
//			help = new PhoenixJdbcHelper();
//			List<Map<String, Object>> list = help.executeQuery(sql);
//			for(Map<String, Object> recMap:list){
//				Set<Map.Entry<String, Object>> entrySet=recMap.entrySet();
//				int i=0;
//				for(Entry<String, Object> entry:entrySet){
//					if(i!=0){
//						System.out.print(","+entry.getKey() +"="+ entry.getValue());
//					}else {
//						System.out.print(entry.getKey() +"="+ entry.getValue());
//					}
//					i++;
//				}
//				System.out.println("------------------------");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			Assert.fail();
//		} finally {
//			try {
//				help.close();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	@Test
//	// 使用preparedStatement
//	public void TestExecuteQuery4() {
//		String sql = "select * from student where age>=? order by age desc";
//		HbaseJdbcHelper help = null;
//		try {
//			help = new PhoenixJdbcHelper();
//			Object[] args = new Object[] { 25 };
//			List<Map<String, Object>> list = help.executeQuery(sql, args);
//			for(Map<String, Object> recMap:list){
//				Set<Map.Entry<String, Object>> entrySet=recMap.entrySet();
//				int i=0;
//				for(Entry<String, Object> entry:entrySet){
//					if(i!=0){
//						System.out.print(","+entry.getKey() +"="+ entry.getValue());
//					}else {
//						System.out.print(entry.getKey() +"="+ entry.getValue());
//					}
//					i++;
//				}
//				System.out.println("------------------------");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			Assert.fail();
//		} finally {
//			try {
//				help.close();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
}
