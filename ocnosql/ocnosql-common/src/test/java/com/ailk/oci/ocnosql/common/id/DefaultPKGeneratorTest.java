package com.ailk.oci.ocnosql.common.id;

import org.junit.Test;

public class DefaultPKGeneratorTest {

	@Test
	public void testGenerateKey() {
		DefaultPKGenerator pkGenerator = new DefaultPKGenerator();
		String pk = pkGenerator.generateKey();
		System.out.println(pk);
	}
}
