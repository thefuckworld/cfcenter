package com.dw.cfcenter.client.api.impl;

import java.util.Map;

import com.dw.cfcenter.bean.cf.ClientConfig;
import com.dw.cfcenter.bean.vo.ClientDataSource;
import com.dw.cfcenter.bean.vo.DataSourceChange;
import com.dw.cfcenter.client.api.ConfCenterApi;
import com.dw.cfcenter.client.core.exception.CfcenterException;
import com.dw.cfcenter.client.core.storage.DefaultDataStorageAdapter;
import com.dw.cfcenter.client.core.storage.file.CustomDataStorageAdapter;
import com.dw.cfcenter.client.manager.DefaultClientConfigManager;
import com.dw.cfcenter.util.ConstantUtils;
import com.dw.cfcenter.util.DefaultHttpClient;
import com.dw.cfcenter.util.LocalUtils;

/**
 * Description: 给用户提供统一的接口
 * @author caohui
 */
public final class DefaultConfCenterApi implements ConfCenterApi{
	
	private static DefaultConfCenterApi instance = new DefaultConfCenterApi();
	private DefaultConfCenterApi() {}
	
	public static DefaultConfCenterApi newInstance() {
		return instance;
	}
	

	@Override
	public Map<String, ClientDataSource> getAllDataSource() {
		if(DefaultClientConfigManager.getInstance() == null || 
				!DefaultClientConfigManager.isInitSuccess) {
			return null;
		}
		
		ClientConfig clientConfig = DefaultClientConfigManager.getInstance().getClientConfig();
		if(clientConfig.isDegrade) {
			return CustomDataStorageAdapter.getInstance().loadDataByProjectId(false, false, null);
		}
		return DefaultDataStorageAdapter.getInstance().loadDataByProjectId(false, false, null);
	}

	@Override
	public ClientDataSource getDataSourceByKey(String key) {
		if(DefaultClientConfigManager.getInstance() == null || 
				!DefaultClientConfigManager.isInitSuccess) {
			return null;
		}
		ClientConfig clientConfig = DefaultClientConfigManager.getInstance().getClientConfig();
		if(clientConfig.isDegrade) {
			return CustomDataStorageAdapter.getInstance().loadDataByProjectId(false, false, null).get(key);
		}
		return DefaultDataStorageAdapter.getInstance().loadDataByProjectId(false, false, null).get(key);
	}

	@Override
	public ClientDataSource getDataSourceAndRelationIfNotExists(String key) {
		if(DefaultClientConfigManager.getInstance() == null || 
				!DefaultClientConfigManager.isInitSuccess) {
			return null;
		}
		
		ClientConfig clientConfig = DefaultClientConfigManager.getInstance().getClientConfig();
		if((DefaultHttpClient.failCount.intValue() >= clientConfig.getFailCountToDegrade() && 
				clientConfig.isFailAutoDegrade()) || 
				clientConfig.isDegrade) {
			return CustomDataStorageAdapter.getInstance().getDataSourceAndRelationIfNotExists(false, key);
		}
		return DefaultDataStorageAdapter.getInstance().getDataSourceAndRelationIfNotExists(false, key);
	}

	@Override
	public void updateServerDataSource(DataSourceChange dataSourceChange) throws CfcenterException{
		if(DefaultClientConfigManager.getInstance() == null || !DefaultClientConfigManager.getInstance().isInitSuccess) {
			 return;
		}
		
		if(dataSourceChange == null 
				|| dataSourceChange.getSendDataSourceTransport() == null
				|| dataSourceChange.getSendDataSourceTransport().size() == 0) {
			throw new CfcenterException("updateServerDataSource param is null");
		}
		
		if(DefaultClientConfigManager.getInstance().getClientConfig() != null
				&& DefaultClientConfigManager.getInstance().getClientConfig().getProjectName().equals(ConstantUtils.COMMON_PROJECT)) {
			throw new CfcenterException("common project cannot write to the data source");
		}
		
		// 设置项目名称
		dataSourceChange.setProjectName(DefaultClientConfigManager.getInstance().getClientConfig().getProjectName());
		// 设置IP
		dataSourceChange.setIp(LocalUtils.getLocalIp());
		// 设置端口号
		dataSourceChange.setPort(DefaultClientConfigManager.getInstance().getClientConfig().getLocalServerPort());
		// 设置秘钥
		dataSourceChange.setKey(DefaultClientConfigManager.getInstance().getClientConfig().getKey());
		// 数据源修改
		DefaultDataStorageAdapter.getInstance().updateServerDataSource(dataSourceChange);
	}

}
