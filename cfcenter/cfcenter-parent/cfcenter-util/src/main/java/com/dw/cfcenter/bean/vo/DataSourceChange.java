package com.dw.cfcenter.bean.vo;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Description:
 * @author caohui
 */
public class DataSourceChange implements Serializable{
	private static final long serialVersionUID = -4620039473856614288L;

	private static final int maxRetryCount = 3;
	
	// 项目名称
	private String projectName;
	// 需要推送一个或者多个数据源
	private List<SendDataSourceTransport> sendDataSourceTransport;
	
	/**
	 * 是否触发批量回调(默认false)
	 * true为触发批量函数
	 * false为不触发
	 */
	private boolean isNeedBatchCallBack = false;
	
	// 通知服务端重试的次数，如果小于等于0则不重试，大于3则次数等于3
	private int httpRetryCount = 0;
	// 客户端本地IP
	private String ip = "";
	// 客户端项目端口号
	private Integer port = 0;
	// 客户端的key
	private String key = "";
	// 客户端验证的时间间隔，默认为30秒，如果在此时间内连续更改，认为无效
	private int serverVertifyTimeInterval = 30;
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		if(StringUtils.isNotBlank(this.projectName) || StringUtils.isBlank(projectName)) {
			return;
		}
		this.projectName = projectName;
	}
	public List<SendDataSourceTransport> getSendDataSourceTransport() {
		return sendDataSourceTransport;
	}
	public void setSendDataSourceTransport(
			List<SendDataSourceTransport> sendDataSourceTransport) {
		this.sendDataSourceTransport = sendDataSourceTransport;
	}
	public boolean isNeedBatchCallBack() {
		return isNeedBatchCallBack;
	}
	public void setNeedBatchCallBack(boolean isNeedBatchCallBack) {
		this.isNeedBatchCallBack = isNeedBatchCallBack;
	}
	public int getHttpRetryCount() {
		return httpRetryCount;
	}
	public void setHttpRetryCount(int httpRetryCount) {
		// 验证最大重试次数
		if(httpRetryCount > DataSourceChange.maxRetryCount) {
			this.httpRetryCount = DataSourceChange.maxRetryCount;
		}else {
			this.httpRetryCount = httpRetryCount;
		}
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		if(StringUtils.isNotBlank(this.ip) || StringUtils.isBlank(ip)) {
			return;
		}
		this.ip = ip;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		if(this.port > 0 || port <= 0) {
			return;
		}
		this.port = port;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		if(StringUtils.isNotBlank(this.key) || StringUtils.isBlank(key)) {
			return;
		}
		this.key = key;
	}
	public int getServerVertifyTimeInterval() {
		return serverVertifyTimeInterval;
	}
	public void setServerVertifyTimeInterval(int serverVertifyTimeInterval) {
		this.serverVertifyTimeInterval = serverVertifyTimeInterval;
	}
	public static int getMaxretrycount() {
		return maxRetryCount;
	}
}
