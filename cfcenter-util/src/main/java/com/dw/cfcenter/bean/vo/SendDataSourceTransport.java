package com.dw.cfcenter.bean.vo;

import java.io.Serializable;

import com.dw.cfcenter.bean.en.TransferTypeEnum;

public class SendDataSourceTransport implements Serializable{

	private static final long serialVersionUID = -7620858040705486745L;

	/**
	 * clientDataSource.sourceName: 数据源名称
	 * clientDataSource.sourceValue: 数据源值
	 */
	private ClientDataSource clientDataSource;
	
	// 数据源描述信息(添加数据源必填)
	private String dataSourceDesc;
	
	private TransferTypeEnum transferTypeEnum = TransferTypeEnum.TRANSFER_UPDATE;

	public ClientDataSource getClientDataSource() {
		return clientDataSource;
	}

	public void setClientDataSource(ClientDataSource clientDataSource) {
		this.clientDataSource = clientDataSource;
	}

	public String getDataSourceDesc() {
		return dataSourceDesc;
	}

	public void setDataSourceDesc(String dataSourceDesc) {
		this.dataSourceDesc = dataSourceDesc;
	}

	public TransferTypeEnum getTransferTypeEnum() {
		return transferTypeEnum;
	}

	public void setTransferTypeEnum(TransferTypeEnum transferTypeEnum) {
		this.transferTypeEnum = transferTypeEnum;
	}
}
