package com.dw.cfcenter.bean.en;

public enum TransferTypeEnum {

	TRANSFER_ADD((byte)1, "addDataSource"),
	TRANSFER_REDUCE((byte)2, "reduceDataSource"),
	TRANSFER_UPDATE((byte)3, "updateDataSource");
	
	private byte type;
	private String desc;
	
	private TransferTypeEnum(byte type, String desc) {
		this.type = type;
		this.desc = desc;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
