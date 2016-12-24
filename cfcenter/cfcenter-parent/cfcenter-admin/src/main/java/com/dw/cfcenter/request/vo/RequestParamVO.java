package com.dw.cfcenter.request.vo;

public class RequestParamVO {

	private String ip; // 请求端IP 
	private Integer port; // 请求端port
	private String projectName; // 请求端项目名称
	private String key; // 数据源key
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
}
