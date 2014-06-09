package com.ailk.oci.ocnosql.common.rowkeygenerator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.ailk.oci.ocnosql.common.rowkeygenerator.GetRowKeyUtil;

/**
 * Created by IntelliJ IDEA.
 * User: lifei5
 * Date: 13-12-12
 * Time: 上午11:37
 * To change this template use File | Settings | File Templates.
 */
public class TestGetRowKeyUtil {
	static Log LOG = LogFactory.getLog(TestGetRowKeyUtil.class);
    @Test
	public void testLoadFromDir(){
		try {
            String rowKey=GetRowKeyUtil.getRowKeyByStringArr("TEST01",new String[]{"1","jing","15652965156"});
            LOG.info("----------------rowKey="+rowKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
