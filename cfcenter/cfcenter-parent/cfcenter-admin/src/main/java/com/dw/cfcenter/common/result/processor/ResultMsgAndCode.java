package com.dw.cfcenter.common.result.processor;

public enum ResultMsgAndCode {

	REQUEST_SUCCESS("1", "请求成功"),
	PARAM_ERROR("2", "请求参数有误"),
	ADD_SUCESS("3" ,"添加成功");
	
	private String code;
	private String msg;
	private ResultMsgAndCode(String code, String msg) {
		this.code = code;
		this.msg = msg;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
}
