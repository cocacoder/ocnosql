package com.ailk.oci.ocnosql.common;

import org.springframework.core.NestedCheckedException;

/**
 * 详单查询内部异常处理，目前是基于spring的异常进行重构
 */
public class OCNosqlNestedCheckedException extends NestedCheckedException{

	private static final long serialVersionUID = -1506157965822780889L;

	public OCNosqlNestedCheckedException(String msg) {
		super(msg);
	}
	public OCNosqlNestedCheckedException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
