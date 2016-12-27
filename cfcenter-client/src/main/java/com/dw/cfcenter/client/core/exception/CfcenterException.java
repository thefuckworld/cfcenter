package com.dw.cfcenter.client.core.exception;

public class CfcenterException extends Exception{
	private static final long serialVersionUID = -7978987955704711386L;
	
	private static final String PREFIX = "Confcenter Exception: ";
	
	public CfcenterException(String msg) {
		super(PREFIX + msg);
	}
	
}
