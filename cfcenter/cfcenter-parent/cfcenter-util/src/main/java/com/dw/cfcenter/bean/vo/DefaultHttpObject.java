package com.dw.cfcenter.bean.vo;

/**
 * Description:
 * @author caohui
 */
public class DefaultHttpObject {

	private int status;
	private String msg;
	private String dataDigest;
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getDataDigest() {
		return dataDigest;
	}
	public void setDataDigest(String dataDigest) {
		this.dataDigest = dataDigest;
	}
}
