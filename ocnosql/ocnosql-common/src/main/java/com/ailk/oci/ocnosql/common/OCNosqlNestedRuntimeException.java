package com.ailk.oci.ocnosql.common;

import org.springframework.core.NestedRuntimeException;

/**
 * 详单查询内部异常处理，目前是基于spring的异常进行重构
 */
public class OCNosqlNestedRuntimeException extends NestedRuntimeException{

	private static final long serialVersionUID = -6265397652642018237L;

	public OCNosqlNestedRuntimeException(String msg, Throwable cause) {
		super(msg, cause);
	}
	public OCNosqlNestedRuntimeException(String msg) {
		super(msg);
	}
}
