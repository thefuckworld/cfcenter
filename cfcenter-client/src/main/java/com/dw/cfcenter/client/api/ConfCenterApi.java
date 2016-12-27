package com.dw.cfcenter.client.api;

import java.util.Map;

import com.dw.cfcenter.bean.vo.ClientDataSource;
import com.dw.cfcenter.bean.vo.DataSourceChange;
import com.dw.cfcenter.client.core.exception.CfcenterException;


public interface ConfCenterApi {

    // 获取全部的数据
	Map<String, ClientDataSource> getAllDataSource();
	
	// 通过key获取对应的数据源
	ClientDataSource getDataSourceByKey(String key);
	
	// 如果该数据源名称对应的数据源在本地进程中不存在，去配置中心服务端请求该数据源，并且写入本地进程中
	ClientDataSource getDataSourceAndRelationIfNotExists(String key);
	
	void updateServerDataSource(DataSourceChange dataSourceChange) throws CfcenterException;
	
	
}