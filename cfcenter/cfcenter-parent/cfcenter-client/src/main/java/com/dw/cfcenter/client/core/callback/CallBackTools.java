package com.dw.cfcenter.client.core.callback;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dw.cfcenter.bean.en.TransferTypeEnum;
import com.dw.cfcenter.bean.vo.ClientDataSource;
import com.dw.cfcenter.bean.vo.DataSourceBatchTransport;
import com.dw.cfcenter.bean.vo.DataSourceTransport;
import com.dw.cfcenter.client.manager.DefaultClientConfigManager;

public class CallBackTools {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CallBackTools.class);
	static ExecutorService singleBackService = null;
	static ExecutorService callBackService = null;

	static {
		int threadCount = 1;
		if(DefaultClientConfigManager.getInstance() != null &&
				DefaultClientConfigManager.getInstance().getClientConfig() != null) {
			threadCount = DefaultClientConfigManager.getInstance().getClientConfig().getCallBackThreadCount();
		}
		singleBackService = Executors.newFixedThreadPool(threadCount);
		callBackService = Executors.newFixedThreadPool(threadCount);
	}
	
	/**
	 * Description: 执行client定义的callback方法 回调函数不会影响主进程更新
	 * All Rights Reserved.
	 *
	 * @param dataSourceTransport
	 * @return void
	 * @version 1.0 2016年11月10日 下午5:22:05 created by caohui(1343965426@qq.com)
	 */
	public static void executeCallBack(final DataSourceTransport dataSourceTransport) {
		if(dataSourceTransport == null) {
			return;
		}
		
		// 如果用户有自定义回调函数，此处回调
		final List<DataChangeListener> dataChangeListenerList = DefaultClientConfigManager.getInstance().getDataChangeListenerList();
		if(dataChangeListenerList != null) {
			singleBackService.execute(new Runnable() {
				@Override
				public void run() {
					for(DataChangeListener dataChangeListener : dataChangeListenerList) {
						dataChangeListener.call(dataSourceTransport);
					}
				}
			});
		}
	}
	
	/**
	 * Description: 批量执行回调函数
	 * All Rights Reserved.
	 *
	 * @param dataSourceBatchTransport
	 * @return void
	 * @version 1.0 2016年11月10日 下午5:26:04 created by caohui(1343965426@qq.com)
	 */
	public static void executeBatchCallBack(final List<DataSourceBatchTransport> dataSourceBatchTransportList) {
		if(dataSourceBatchTransportList == null || dataSourceBatchTransportList.size() < 0) {
			return;
		}
		
		if(DefaultClientConfigManager.getInstance() == null || 
				DefaultClientConfigManager.getInstance().getConfCenterApi() == null && 
				DefaultClientConfigManager.getInstance().getConfCenterApi().getAllDataSource() == null) {
			return;
		}
		
		for(DataSourceBatchTransport dbt : dataSourceBatchTransportList) {
			if(!DefaultClientConfigManager.getInstance().getConfCenterApi().getAllDataSource().containsKey(dbt.getClientDataSource().getSourceName())) {
				LOGGER.error("current data map not inlude batchData, and the key is "+ dbt.getClientDataSource().getSourceName());
				return;
			}
		}
		
		// 如果用户有自定义的回调函数，此处回调
		final List<BatchCallBackListener> batchCallBackListeners = DefaultClientConfigManager.getInstance().getBatchCallBackListeners();
		if(batchCallBackListeners != null) {
			callBackService.execute(new Runnable() {
				@Override
				public void run() {
					for(BatchCallBackListener batchCallBackListener : batchCallBackListeners) {
						batchCallBackListener.batchCall(dataSourceBatchTransportList);
					}
				}
			});
		}
	}
	
	/**
	 * Description: 定时从server load数据时比较数据变化
	 * All Rights Reserved.
	 *
	 * @param oldData
	 * @param newData
	 * @return void
	 * @version 1.0 2016年11月10日 下午5:36:11 created by caohui(1343965426@qq.com)
	 */
	public static void compareData(Map<String, ClientDataSource> oldData, Map<String, ClientDataSource> newData) {
		// 1. 旧数据为空，新数据不为空
		if(oldData == null || oldData.size() == 0) {
			if(newData != null && newData.size() > 0) {
				for(String key : newData.keySet()) {
					convertObjectAndExecute(newData, key, TransferTypeEnum.TRANSFER_ADD);
				}
			}
			return;
		}
		
		// 2. 新数据为空，旧数据不为空
		if(newData == null || newData.size() == 0) {
			for(String key : oldData.keySet()) {
				convertObjectAndExecute(oldData, key, TransferTypeEnum.TRANSFER_REDUCE);
			}
			return;
		}
		
		// 3. 新旧数据都不为空
		for(String key : newData.keySet()) {
			// 如果旧的key和新的key相同，但是内容不同，则将数据更新
			if(oldData.containsKey(key)) {
				if(!oldData.get(key).getSourceValue().equals(newData.get(key).getSourceValue())) {
					convertObjectAndExecute(newData, key, TransferTypeEnum.TRANSFER_UPDATE);
				}
			}else {
				// 如果旧的数据里面不包含，说明是新增
				convertObjectAndExecute(newData, key, TransferTypeEnum.TRANSFER_ADD);
			}
		}
		
		for(String key : oldData.keySet()) {
			if(!newData.containsKey(key)) {
				convertObjectAndExecute(newData, key, TransferTypeEnum.TRANSFER_REDUCE);
			}
		}
	}
	
	/**
	 * Description: 转换对象并且执行回调函数
	 * All Rights Reserved.
	 *
	 * @param newData
	 * @param key
	 * @param transferTypeEnum
	 * @return void
	 * @version 1.0 2016年11月10日 下午5:59:24 created by caohui(1343965426@qq.com)
	 */
	private static void convertObjectAndExecute(Map<String, ClientDataSource> newData, String key, TransferTypeEnum transferTypeEnum) {
		DataSourceTransport dataSourceTransport = new DataSourceTransport();
		dataSourceTransport.setTransferTypeEnum(transferTypeEnum);
		dataSourceTransport.setClientDataSource(newData.get(key));
		executeCallBack(dataSourceTransport);
	}
}
