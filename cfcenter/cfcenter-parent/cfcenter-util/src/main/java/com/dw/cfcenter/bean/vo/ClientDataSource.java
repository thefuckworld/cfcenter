package com.dw.cfcenter.bean.vo;

import java.io.Serializable;

/**
 * 
 * Description: 客户端数据源
 * @author caohui
 */
public class ClientDataSource implements Serializable{
	private static final long serialVersionUID = 3339988566582577110L;

	// 值类型  0为普通数据源  1为公共数据源
	private byte sourceType = 0;
	
	// 数据源名称
	private String sourceName;
	
	// 数据源的值
	private String sourceValue;
	
	// 组名称
	private String groupName = "string";

	public byte getSourceType() {
		return sourceType;
	}

	public void setSourceType(byte sourceType) {
		this.sourceType = sourceType;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getSourceValue() {
		return sourceValue;
	}

	public void setSourceValue(String sourceValue) {
		this.sourceValue = sourceValue;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
}
