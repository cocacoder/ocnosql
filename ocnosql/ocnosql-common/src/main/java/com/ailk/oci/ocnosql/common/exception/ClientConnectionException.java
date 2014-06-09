package com.ailk.oci.ocnosql.common.exception;

import com.ailk.oci.ocnosql.common.exception.ClientRuntimeException;

public class ClientConnectionException extends ClientRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -658856508296490394L;

	public ClientConnectionException(String msg, Throwable cause) {
		super(msg, cause);
		// TODO Auto-generated constructor stub
	}

	public ClientConnectionException(String msg) {
		super(msg);
		// TODO Auto-generated constructor stub
	}

}
