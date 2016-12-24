package com.dw.cfcenter.bean.vo;

import java.io.Serializable;

import com.dw.cfcenter.bean.en.TransferTypeEnum;

/**
 * Description: 数据变化时传输对象
 * @author caohui
 */
public class DataSourceTransport implements Serializable{

	private static final long serialVersionUID = -7467686818292509522L;

	// 项目所有数据源的摘要
	private String dataDigest;
	
	// key value键值对
	private ClientDataSource clientDataSource;
	
	// 类型
	private TransferTypeEnum transferTypeEnum;

	public String getDataDigest() {
		return dataDigest;
	}

	public void setDataDigest(String dataDigest) {
		this.dataDigest = dataDigest;
	}

	public ClientDataSource getClientDataSource() {
		return clientDataSource;
	}

	public void setClientDataSource(ClientDataSource clientDataSource) {
		this.clientDataSource = clientDataSource;
	}

	public TransferTypeEnum getTransferTypeEnum() {
		return transferTypeEnum;
	}

	public void setTransferTypeEnum(TransferTypeEnum transferTypeEnum) {
		this.transferTypeEnum = transferTypeEnum;
	}
}
