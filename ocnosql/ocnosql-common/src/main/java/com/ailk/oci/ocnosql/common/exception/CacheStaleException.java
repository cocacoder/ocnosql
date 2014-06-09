package com.ailk.oci.ocnosql.common.exception;


public class CacheStaleException extends ClientRuntimeException {

	private static final long serialVersionUID = -658856508296490394L;

	public CacheStaleException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public CacheStaleException(String msg) {
		super(msg);
	}

}
