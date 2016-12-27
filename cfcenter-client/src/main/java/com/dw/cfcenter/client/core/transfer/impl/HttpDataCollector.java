package com.dw.cfcenter.client.core.transfer.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.dw.cfcenter.bean.vo.ClientDataSource;
import com.dw.cfcenter.client.core.transfer.DataCollector;
import com.dw.cfcenter.client.manager.DefaultClientConfigManager;
import com.dw.cfcenter.encryption.AESEncrypt;
import com.dw.cfcenter.util.ConstantUtils;
import com.dw.cfcenter.util.DefaultHttpClient;
import com.dw.cfcenter.util.LocalUtils;

/**
 * Description: http数据收集器
 * @author caohui
 */
public class HttpDataCollector implements DataCollector{

	private final static Logger LOGGER = LoggerFactory.getLogger(HttpDataCollector.class);
	private static HttpDataCollector httpDataCollector;
	private static volatile boolean instanced = false;
	
	public static HttpDataCollector getInstance() {
		if(!instanced) {
			synchronized(HttpDataCollector.class) {
				if(!instanced) {
					synchronized(HttpDataCollector.class) {
						httpDataCollector = new HttpDataCollector();
						instanced = true;
					}
				}
			}
		}
		return httpDataCollector;
	}
	
	@Override
	public Map<String, ClientDataSource> getAllData() throws Exception {
		Map<String, String> params = new HashMap<String, String> ();
		params.put(ConstantUtils.PING_PARAM_IP, LocalUtils.getLocalIp());
		params.put(ConstantUtils.PING_PARAM_PORT, String.valueOf(DefaultClientConfigManager.getInstance().getClientConfig().getLocalServerPort()));
		params.put(ConstantUtils.PING_PARAM_PROJECT, String.valueOf(DefaultClientConfigManager.getInstance().getClientConfig().getProjectName()));
		params.put(ConstantUtils.DATASOURCE_KEY, String.valueOf(DefaultClientConfigManager.getInstance().getClientConfig().getKey()));
		
		String returnValue = null;
		try {
			returnValue = DefaultHttpClient.doGet(DefaultClientConfigManager.getInstance().getClientConfig().getServerDomain() + ConstantUtils.GETALL_URL_SUFFIX, params, 0, 0);
		}catch(Exception e) {
			throw new Exception(e.getMessage());
		}
		if(StringUtils.isNotBlank(returnValue)) {
			String result = JSONObject.parseObject(returnValue).getString(ConstantUtils.GET_URL_RESULT);
			if(StringUtils.isNotBlank(result)) {
				List<ClientDataSource> dataSourceList = JSONObject.parseArray(result, ClientDataSource.class);
				if(dataSourceList != null && dataSourceList.size() > 0) {
					Map<String, ClientDataSource> dataSourceMap = new HashMap<String, ClientDataSource> ();
					for(ClientDataSource dataSource : dataSourceList) {
						dataSourceMap.put(dataSource.getSourceName(), dataSource);
						// value解密
						try {
							dataSource.setSourceValue(new String(AESEncrypt.decrypt(dataSource.getSourceValue()), "utf-8"));
						}catch(Exception e) {
							LOGGER.error(e.getMessage(), e);
						}
					}
					return dataSourceMap;
				}
			}else { // 如果数据源的值是空的
				Integer status = JSONObject.parseObject(returnValue).getInteger("status");
				if(status == null || status.intValue() != 1) {
					String msg = JSONObject.parseObject(returnValue).getString("msg");
					throw new Exception(msg);
				}
				
			}
		}
		
		throw new Exception("result is null");
	}

	@Override
	public ClientDataSource getDataSourceFromServerAndRelation(String key) {
		Map<String, String> params = new HashMap<String, String> ();
		params.put(ConstantUtils.PING_PARAM_IP, LocalUtils.getLocalIp());
		params.put(ConstantUtils.PING_PARAM_PORT, String.valueOf(DefaultClientConfigManager.getInstance().getClientConfig().getLocalServerPort()));
		params.put(ConstantUtils.PING_PARAM_PROJECT, String.valueOf(DefaultClientConfigManager.getInstance().getClientConfig().getProjectName()));
		params.put(ConstantUtils.DATASOURCE_KEY, String.valueOf(DefaultClientConfigManager.getInstance().getClientConfig().getKey()));
		
		String returnValue = null;
		try {
			returnValue = DefaultHttpClient.doGet(DefaultClientConfigManager.getInstance().getClientConfig().getServerDomain() + ConstantUtils.GET_DS_RELATION_SUFFIX, params, 0, 0);
		}catch(Exception e) {
			return null;
		}
		
		if(StringUtils.isNotBlank(returnValue)) {
			String result = JSONObject.parseObject(returnValue).getString(ConstantUtils.DATASOURCE_RESULT);
			if(StringUtils.isNotBlank(result)) {
				ClientDataSource dataSource = JSONObject.parseObject(result, ClientDataSource.class);
				if(dataSource != null) {
					try {
						dataSource.setSourceValue(new String(AESEncrypt.decrypt(dataSource.getSourceValue()), "utf-8"));
					}catch(Exception e) {
						LOGGER.error(e.getMessage(), e);
					}
					return dataSource;
				}
			}
		}
		return null;
	}

}
