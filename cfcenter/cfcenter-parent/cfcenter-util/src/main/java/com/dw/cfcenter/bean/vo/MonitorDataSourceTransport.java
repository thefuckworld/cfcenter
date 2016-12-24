package com.dw.cfcenter.bean.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * Description: 客户端向服务端监控对象
 * @author caohui
 */
public class MonitorDataSourceTransport implements Serializable{
	private static final long serialVersionUID = -2458044880240402821L;
	
	private Long projectId;
	// 服务端摘要
	private String serverDigestData;
	// 客户端摘要
	private String clientDigestData;
	// 指定某个key，如果为空获取全部
	private String dataSourceKey;
	/**
	 * 数据源类型
	 * 0代表普通数据源
	 * 1代表公共数据源
	 * 2代表全部数据源
	 */
	private Byte sourceType = 0;

	// 客户端info ip:端口号
	private String clientInfo;
	
	// 客户端值
	private Map<String, String> clientValue;
	
	// 监控时间
	private Date monitorTime;
	
	/**
	 * 客户端是否上报value值，默认为true
	 * 考虑到客户端发送http请求大小会受到负载均衡、proxy、应用服务器限制
	 */
	private boolean isSendClientValue = true;

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public String getServerDigestData() {
		return serverDigestData;
	}

	public void setServerDigestData(String serverDigestData) {
		this.serverDigestData = serverDigestData;
	}

	public String getClientDigestData() {
		return clientDigestData;
	}

	public void setClientDigestData(String clientDigestData) {
		this.clientDigestData = clientDigestData;
	}

	public String getDataSourceKey() {
		return dataSourceKey;
	}

	public void setDataSourceKey(String dataSourceKey) {
		this.dataSourceKey = dataSourceKey;
	}

	public Byte getSourceType() {
		return sourceType;
	}

	public void setSourceType(Byte sourceType) {
		this.sourceType = sourceType;
	}

	public String getClientInfo() {
		return clientInfo;
	}

	public void setClientInfo(String clientInfo) {
		this.clientInfo = clientInfo;
	}

	public Map<String, String> getClientValue() {
		return clientValue;
	}

	public void setClientValue(Map<String, String> clientValue) {
		this.clientValue = clientValue;
	}

	public Date getMonitorTime() {
		return monitorTime;
	}

	public void setMonitorTime(Date monitorTime) {
		this.monitorTime = monitorTime;
	}

	public boolean isSendClientValue() {
		return isSendClientValue;
	}

	public void setSendClientValue(boolean isSendClientValue) {
		this.isSendClientValue = isSendClientValue;
	}
}
