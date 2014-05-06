package com.ailk.oci.ocnosql.dao.spi;

import com.ailk.oci.ocnosql.common.OCNosqlNestedRuntimeException;

/**
 * @author Administrator
 * HBase表DAo异常
 */
public class HbaseTableDaoException extends OCNosqlNestedRuntimeException {
	private static final long serialVersionUID = -2638135527979038811L;

	public HbaseTableDaoException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public HbaseTableDaoException(String msg) {
		super(msg);
	}

}
