package com.dw.cfcenter.client.core.callback;

import java.util.List;

import com.dw.cfcenter.bean.vo.DataSourceBatchTransport;

public interface BatchCallBackListener {

	// 触发规则为用户通过后台手动触发，不会改变服务端、客户端副本内容
	void batchCall(List<DataSourceBatchTransport> dataSourceBatchTransport);
}
