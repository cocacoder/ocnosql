package com.ailk.oci.ocnosql.common.id;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

public class DefaultPKGeneratorTest {
	static Log LOG = LogFactory.getLog(DefaultPKGeneratorTest.class);

	@Test
	public void testGenerateKey() {
		DefaultPKGenerator pkGenerator = new DefaultPKGenerator();
		String pk = pkGenerator.generateKey();
		LOG.info(pk);
	}
}
