package com.ailk.oci.ocnosql.client.put;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.ailk.oci.ocnosql.client.config.spi.CommonConstants;

public class HBasePutTest {

	@Test
	public void hbasePut(){
		Map<String, String> param = new HashMap<String, String>();
		param.put(  CommonConstants.SINGLE_FAMILY, "F");
		param.put( CommonConstants.SEPARATOR, ";");
		param.put( CommonConstants.SKIPBADLINE, "");
		param.put( CommonConstants.BATCH_PUT, "");
		param.put( CommonConstants.HBASE_MAXPUTNUM, "");
		param.put( CommonConstants.STORAGE_STRATEGY, "");
		param.put( CommonConstants.COLUMNS, "name,age,tel");
		param.put( CommonConstants.ROWKEYCOLUMN, "");
		param.put( CommonConstants.ROWKEYGENERATOR, "");
		param.put( CommonConstants.ALGOCOLUMN, "");
		param.put( CommonConstants.ROWKEYCALLBACK, "");
		HBasePut hbasePut = new HBasePut("HbaseInitTest", param);
		hbasePut.put("11,haha,33,44_");
	}

}
