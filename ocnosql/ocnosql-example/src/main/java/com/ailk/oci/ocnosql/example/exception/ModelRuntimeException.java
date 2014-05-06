package com.ailk.oci.ocnosql.example.exception;


import org.springframework.core.*;

public class ModelRuntimeException extends NestedRuntimeException{

	private static final long serialVersionUID = -1321446247887453347L;
	
	public ModelRuntimeException(String msg) {
		super(msg);
	}
	public ModelRuntimeException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
