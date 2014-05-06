package com.ailk.oci.ocnosql.dao.spi;

import com.ailk.oci.ocnosql.common.OCNosqlNestedRuntimeException;

public class RegionRuntimeException extends OCNosqlNestedRuntimeException {
	private static final long serialVersionUID = 115991363343522798L;

	public RegionRuntimeException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public RegionRuntimeException(String msg) {
		super(msg);
	}

}
