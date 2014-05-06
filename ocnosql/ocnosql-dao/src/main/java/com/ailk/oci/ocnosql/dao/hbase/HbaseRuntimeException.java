package com.ailk.oci.ocnosql.dao.hbase;

import com.ailk.oci.ocnosql.common.OCNosqlNestedRuntimeException;

public class HbaseRuntimeException extends OCNosqlNestedRuntimeException {
	private static final long serialVersionUID = 884179978194796074L;

	public HbaseRuntimeException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public HbaseRuntimeException(String msg) {
		super(msg);
	}

}
