package com.dw.cfcenter.client.core.storage;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.dw.cfcenter.bean.en.TransferTypeEnum;
import com.dw.cfcenter.bean.vo.ClientDataSource;
import com.dw.cfcenter.bean.vo.DataSourceBatchTransport;
import com.dw.cfcenter.bean.vo.DataSourceChange;
import com.dw.cfcenter.bean.vo.DataSourceTransport;
import com.dw.cfcenter.bean.vo.MonitorDataSourceTransport;
import com.dw.cfcenter.bean.vo.SendDataSourceTransport;
import com.dw.cfcenter.client.core.callback.CallBackTools;
import com.dw.cfcenter.client.core.exception.CfcenterException;
import com.dw.cfcenter.client.core.security.server.ServerConfigList;
import com.dw.cfcenter.client.core.storage.file.CustomDataStorageAdapter;
import com.dw.cfcenter.client.core.storage.file.FileDataStorage;
import com.dw.cfcenter.client.core.storage.process.ProcessDataStorage;
import com.dw.cfcenter.client.core.transfer.impl.HttpDataCollector;
import com.dw.cfcenter.client.manager.DefaultClientConfigManager;
import com.dw.cfcenter.encryption.AESEncrypt;
import com.dw.cfcenter.util.ConstantUtils;
import com.dw.cfcenter.util.HttpClientForJson;

/**
 * Description: 存储适配器
 * @author caohui
 */
public final class DefaultDataStorageAdapter implements DataStorageAdapter{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultDataStorageAdapter.class);
	private static DefaultDataStorageAdapter defaultDataStorageAdapter = new DefaultDataStorageAdapter();
	private FileDataStorage fileDataStorage = new FileDataStorage();
	private ProcessDataStorage processDataStorage = new ProcessDataStorage();
	
	// 是否为启动执行
	private volatile boolean isStartExecute = true;
	static ExecutorService executeService = Executors.newFixedThreadPool(3);
    private DefaultDataStorageAdapter() {}
    
    public static DefaultDataStorageAdapter getInstance() {
    	return defaultDataStorageAdapter;
    }
    
    
    /**
     * Description: 启动时或者定时任务触发时获取数据
     * All Rights Reserved.
     *
     * @version 1.0 2016年11月10日 下午3:04:54 created by caohui(1343965426@qq.com)
     */
	@Override
	public Map<String, ClientDataSource> loadDataByProjectId(
			boolean isFromServer, boolean isUseParam,
			Map<String, ClientDataSource> dataSourceMap) {
		
		// 从服务端获取数据源
		if(isFromServer) {
			
			boolean serverRequestSuccess = true;
			
			if(!isUseParam) {
				try {
					dataSourceMap = HttpDataCollector.getInstance().getAllData();
				}catch(Exception e) {
					serverRequestSuccess = false;
				}
			}
			
			// 如果服务端请求成功并且有数据则更新本地副本
			if(serverRequestSuccess) {
				Map<String, ClientDataSource> clientDataSourceMap = null;
				if(!isStartExecute) {
					clientDataSourceMap = processDataStorage.cloneData();
				}
				// 刷新进程数据
				processDataStorage.refreshData(dataSourceMap);
				
				// 刷新本地文件数据
				if(DefaultClientConfigManager.getInstance().getClientConfig().isNeedStorage()) {
					fileDataStorage.refreshData(dataSourceMap);
				}
				
				// 如果开启故障时选择降级策略则更新用户自定义副本
				// TODO:自定义文件至允许有数据时同步更新，配置中心数据源为空对自定义文件不变化
				if(DefaultClientConfigManager.getInstance().getClientConfig().isFailAutoDegrade()) {
					if(StringUtils.isNotBlank(DefaultClientConfigManager.getInstance().getClientConfig().getCustomFileName()) && dataSourceMap != null && !dataSourceMap.isEmpty()) {
						CustomDataStorageAdapter.getInstance().refreshCustomData(dataSourceMap);
					}
				}
				
				if(!isStartExecute) {
					CallBackTools.compareData(clientDataSourceMap, dataSourceMap);
				}
			}
			
			// 如果服务器请求失败则读取本地副本
			if(!serverRequestSuccess) {
				dataSourceMap = fileDataStorage.readDataFromFile();
				if(dataSourceMap != null && dataSourceMap.size() > 0) {
					// 刷新进程数据
					processDataStorage.refreshData(dataSourceMap);
				}
			}
		}
		
		
		if(isStartExecute) {
			isStartExecute = false;
		}
		
		return processDataStorage.processData;
	}

	@Override
	public ClientDataSource getDataSourceAndRelationIfNotExists(
			boolean isFromServer, String key) {
		if(StringUtils.isBlank(key)) {
			return null;
		}
		
		Map<String, ClientDataSource> clientDataSourceMap = loadDataByProjectId(isFromServer, false, null);
		if(clientDataSourceMap == null || clientDataSourceMap.size() == 0) {
			return null;
		}
		
		if(clientDataSourceMap.containsKey(key)) {
			return clientDataSourceMap.get(key);
		}
		
		// 本地进程当中没有该key对应的数据源，去配置中心服务端请求该key对应的数据源，并将该数据源关联到本地进程中
		ClientDataSource clientDataSource = HttpDataCollector.getInstance().getDataSourceFromServerAndRelation(key);
		if(clientDataSource != null) {
			// 追加数据源
			processDataStorage.processData.put(key, clientDataSource);
			if(DefaultClientConfigManager.getInstance().getClientConfig().isNeedStorage()) {
				fileDataStorage.refreshData(processDataStorage.processData);
			}
		}
		return clientDataSource;
	}

	@Override
	public void changeDataFromSocket(String ip, String dataMessage) {
		if(StringUtils.isBlank(dataMessage)) {
			return;
		}
		// 检测IP是否有效
		if(DefaultClientConfigManager.getInstance().getClientConfig().isCheckServerRequest) {
			if(!ServerConfigList.serverIpList.contains(ip)) {
				LOGGER.warn("server transfer data, but the client ip is not vertify, the ip is "+ip);
				return;
			}
		}
		
		DataSourceTransport dataSourceTransport = null;
		try {
			dataSourceTransport = JSONObject.parseObject(dataMessage, DataSourceTransport.class);
		}catch(Exception e) {
			LOGGER.debug("parse receive object is not normal message");
		}
		
		if(dataSourceTransport != null && dataSourceTransport.getClientDataSource() != null) {
			// 验证消息是否重复
			if(StringUtils.isNotBlank(dataSourceTransport.getDataDigest()) && 
					StringUtils.isNotBlank(DefaultClientConfigManager.getInstance().getDataDigest()) &&
					dataSourceTransport.getDataDigest().equals(DefaultClientConfigManager.getInstance().getDataDigest())) {
				LOGGER.error("tcp call back message is repeat, and new digest is {}, before digest is {}", dataSourceTransport.getDataDigest(), DefaultClientConfigManager.getInstance().getDataDigest());
				return;
			}
			
			if(StringUtils.isNotBlank(dataSourceTransport.getDataDigest()) && !dataSourceTransport.getDataDigest().equals(DefaultClientConfigManager.getInstance().getDataDigest())) {
				DefaultClientConfigManager.getInstance().setDataDigest(dataSourceTransport.getDataDigest());
			}
			
			// 解密数据源
			try {
				dataSourceTransport.getClientDataSource().setSourceValue(new String(AESEncrypt.decrypt(dataSourceTransport.getClientDataSource().getSourceValue()), "utf-8"));
			}catch(Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
			
			// 增加数据源
			if(dataSourceTransport.getTransferTypeEnum().getType() == TransferTypeEnum.TRANSFER_ADD.getType()) {
				processDataStorage.processData.put(dataSourceTransport.getClientDataSource().getSourceName(), dataSourceTransport.getClientDataSource());
			}else if(dataSourceTransport.getTransferTypeEnum().getType() == TransferTypeEnum.TRANSFER_REDUCE.getType()) {
				// 减少数据
				if(processDataStorage.processData.containsKey(dataSourceTransport.getClientDataSource().getSourceName())) {
					processDataStorage.processData.remove(dataSourceTransport.getClientDataSource().getSourceName());
				}
			}else if(dataSourceTransport.getTransferTypeEnum().getType() == TransferTypeEnum.TRANSFER_UPDATE.getType()) {
				// 修改数据源
				processDataStorage.processData.put(dataSourceTransport.getClientDataSource().getSourceName(), dataSourceTransport.getClientDataSource());
			}
			
			// 更新file缓存
			fileDataStorage.refreshData(processDataStorage.processData);
			
			// 执行回调函数
			CallBackTools.executeCallBack(dataSourceTransport);
		}else {
			MonitorDataSourceTransport monitorDataSourceTransport = null;
			try {
				monitorDataSourceTransport = JSONObject.parseObject(dataMessage, MonitorDataSourceTransport.class);
			}catch(Exception e) {
				LOGGER.debug("parse receive object is not monitor message");
			}
			
			if(monitorDataSourceTransport != null && monitorDataSourceTransport.getProjectId() != null) {
				sendClientValueToServer(monitorDataSourceTransport);
			}else {
				List<DataSourceBatchTransport> dataSourceBatchTransportList = null;
				try {
					dataSourceBatchTransportList = JSONObject.parseArray(dataMessage, DataSourceBatchTransport.class);
				}catch(Exception e) {
					LOGGER.error("parse receive object is not batch callback message", e);
				}
				
				if(dataSourceBatchTransportList != null && dataSourceBatchTransportList.size() > 0) {
					boolean isBatchCall = true;
					
					for(DataSourceBatchTransport dataSourceBatchTransport : dataSourceBatchTransportList) {
						if(dataSourceBatchTransport.isUseLocalDataSource()) {
							ClientDataSource clientDataSource = DefaultClientConfigManager.getInstance().
									getConfCenterApi().getDataSourceByKey(dataSourceBatchTransport.getClientDataSource().getSourceName());
							if(clientDataSource != null) {
								dataSourceBatchTransport.getClientDataSource().setSourceValue(clientDataSource.getSourceValue());;
							}
						}else {
							try {
								dataSourceBatchTransport.getClientDataSource().setSourceValue(new String(AESEncrypt.decrypt(dataSourceBatchTransport.getClientDataSource().getSourceValue()), "utf-8"));
							}catch(Exception e) {
								LOGGER.error(e.getMessage(), e);
								isBatchCall = false;
							}
						}
					}
					
					if(isBatchCall) {
						CallBackTools.executeBatchCallBack(dataSourceBatchTransportList);
					}
				}
			}
		}
		
		
	}

	// 本机的value发送至服务端
	private void sendClientValueToServer(MonitorDataSourceTransport monitorDataSourceTransport) {
		if(monitorDataSourceTransport != null) {
			monitorDataSourceTransport.setClientDigestData(DefaultClientConfigManager.getInstance().getDataDigest());
			if(monitorDataSourceTransport.isSendClientValue()) {
				if(processDataStorage.processData != null && processDataStorage.processData.size() > 0) {
					 Map<String, String> returnMap = new ConcurrentSkipListMap<String, String>();
					 for(String key : processDataStorage.processData.keySet()) {
						 if(monitorDataSourceTransport.getSourceType() == 2) {
							 // 全部数据源
							 returnMap.put(key, processDataStorage.processData.get(key).getSourceValue());
						 }else if(monitorDataSourceTransport.getSourceType() == 0) {
							 // 普通数据源
							 if(processDataStorage.processData.get(key).getSourceType() == 0) {
								 returnMap.put(key, processDataStorage.processData.get(key).getSourceValue());
							 }
						 }else if(monitorDataSourceTransport.getSourceType() == 1) {
							 // 公共数据源
							 if(processDataStorage.processData.get(key).getSourceType() == 1) {
								 returnMap.put(key, processDataStorage.processData.get(key).getSourceValue());
							 }
						 }
					 }
					    monitorDataSourceTransport.setClientValue(returnMap);
				}
			}
			httpClientSendValue(monitorDataSourceTransport);
		}
	}
	
	private void httpClientSendValue(final MonitorDataSourceTransport monitorDataSourceTransport) {
		executeService.execute(new Runnable() {
			@Override
			public void run() {
				String url = DefaultClientConfigManager.getInstance().getClientConfig().getServerDomain() + ConstantUtils.MONITOR_URL;
				HttpClientForJson.getHttpResponseForJson(url, JSONObject.toJSONString(monitorDataSourceTransport));
			}
		});
	}

	@Override
	public void updateServerDataSource(DataSourceChange dataSourceChange)
			throws CfcenterException {
		String url = DefaultClientConfigManager.getInstance().getClientConfig().getServerDomain() + ConstantUtils.UPDATE_DATASOURCE_URL;
		
		try {
			// 校验输入的内容是否包含在本地内容中
			for(SendDataSourceTransport sendDataSourceTransport : dataSourceChange.getSendDataSourceTransport()) {
				if(DefaultClientConfigManager.getInstance().getConfCenterApi().getDataSourceByKey(sendDataSourceTransport.getClientDataSource().getSourceName()) == null
						&& !sendDataSourceTransport.getTransferTypeEnum().equals(TransferTypeEnum.TRANSFER_ADD)) {
					throw new CfcenterException("updateServerDataSource key is not exists , and the key is "+ sendDataSourceTransport.getClientDataSource().getSourceName());
				}
				
				if(sendDataSourceTransport.getTransferTypeEnum().equals(TransferTypeEnum.TRANSFER_ADD)) {
					if(StringUtils.isBlank(sendDataSourceTransport.getDataSourceDesc())) {
						throw new CfcenterException("addServerDataSource desc is null , and the key is "+ sendDataSourceTransport.getClientDataSource().getSourceName());
					}
				}
			}
			
			// 返回结果
			String returnValue = HttpClientForJson.getHttpResponse(url, JSONObject.toJSONString(dataSourceChange));
			if(StringUtils.isNotBlank(returnValue) && !"success".equals(returnValue)) {
				throw new Exception(returnValue);
			}
		}catch(Exception e) {
			// 重试次数大于0的情况下，进行失败重试
			if(dataSourceChange.getHttpRetryCount() > 0) {
				dataSourceChange.setHttpRetryCount(dataSourceChange.getHttpRetryCount() - 1);
				updateServerDataSource(dataSourceChange);
			}
		}
	}
}
