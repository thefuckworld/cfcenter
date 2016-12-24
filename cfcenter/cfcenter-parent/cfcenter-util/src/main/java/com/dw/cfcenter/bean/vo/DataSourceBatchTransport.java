package com.dw.cfcenter.bean.vo;

import java.io.Serializable;

import com.dw.cfcenter.bean.en.TransferTypeEnum;

/**
 * Description:
 * @author caohui
 */
public class DataSourceBatchTransport implements Serializable{
	private static final long serialVersionUID = -6127026895126141845L;

	// key value键值对
	private ClientDataSource clientDataSource;
	
	// 类型
	private TransferTypeEnum transferTypeEnum;
	
	// 是否使用本地数据源，如果为服务端手动推送，默认为使用本地数据源副本
	private boolean isUseLocalDataSource = true;

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

	public boolean isUseLocalDataSource() {
		return isUseLocalDataSource;
	}

	public void setUseLocalDataSource(boolean isUseLocalDataSource) {
		this.isUseLocalDataSource = isUseLocalDataSource;
	}
}
