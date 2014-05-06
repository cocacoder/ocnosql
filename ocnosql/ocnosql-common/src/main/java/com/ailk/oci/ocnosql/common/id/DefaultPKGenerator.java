package com.ailk.oci.ocnosql.common.id;

import com.ailk.oci.ocnosql.common.util.SequenceCreator;

public class DefaultPKGenerator implements PKGenerator<String> {

	public String generateKey() {
		return new SequenceCreator().getUID();
	}

	@Override
	public String emptyPK() {
		return "";
	}

}
