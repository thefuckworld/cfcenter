package com.dw.cfcenter.client.core.transfer.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.dw.cfcenter.bean.vo.ClientDataSource;
import com.dw.cfcenter.bean.vo.DefaultHttpObject;
import com.dw.cfcenter.client.core.storage.DefaultDataStorageAdapter;
import com.dw.cfcenter.client.core.transfer.PingServer;
import com.dw.cfcenter.client.manager.DefaultClientConfigManager;
import com.dw.cfcenter.encryption.AESEncrypt;
import com.dw.cfcenter.util.ConstantUtils;
import com.dw.cfcenter.util.DefaultHttpClient;
import com.dw.cfcenter.util.LocalUtils;

/**
 * Description: ping server http实现类
 * @author caohui
 */
public final class HttpPingServer implements PingServer{
	private final static Logger LOGGER = LoggerFactory.getLogger(HttpPingServer.class);
	
	private static HttpPingServer httpPingServer;
	private HttpPingServer() {}
	private static volatile boolean instanced = false;
	
	/**
	 * Description: 得到HttpPingServer的单例
	 * All Rights Reserved.
	 *
	 * @version 1.0 2016年11月10日 上午1:03:02 created by caohui(1343965426@qq.com)
	 */
	public static HttpPingServer getInstance() {
		if(!instanced) {
			synchronized(HttpPingServer.class) {
				if(!instanced) {
					synchronized(HttpPingServer.class) {
						httpPingServer = new HttpPingServer();
						instanced = true;
					}
				}
			}
		}
		return httpPingServer;
	}
	
	/**
	 * Description: 上报服务端，并且判断是否需要回填数据
	 * All Rights Reserved.
	 *
	 * @param isFirst
	 * @return
	 * @throws Exception
	 * @return boolean
	 * @version 1.0 2016年11月10日 上午1:04:12 created by caohui(1343965426@qq.com)
	 */
	@Override
	public boolean pingAndVertify(boolean isFirst) throws Exception {
		Map<String, String> params = new HashMap<String, String> ();
		params.put(ConstantUtils.PING_PARAM_IP, LocalUtils.getLocalIp());
		params.put(ConstantUtils.PING_PARAM_PORT, String.valueOf(DefaultClientConfigManager.getInstance().getClientConfig().getLocalServerPort()));
		params.put(ConstantUtils.PING_PARAM_PROJECT, String.valueOf(DefaultClientConfigManager.getInstance().getClientConfig().getProjectName()));
		params.put(ConstantUtils.DATASOURCE_KEY, String.valueOf(DefaultClientConfigManager.getInstance().getClientConfig().getKey()));
		params.put(ConstantUtils.DATA_DIGEST, DefaultClientConfigManager.getInstance().getClientConfig().getDataDigest() == null ? "" : DefaultClientConfigManager.getInstance().getClientConfig().getDataDigest());
		
		String returnValue = null;
		try {
			returnValue = DefaultHttpClient.doGet(DefaultClientConfigManager.getInstance().getClientConfig().getServerDomain() + ConstantUtils.PING_URL_SUFFIX, params, 0, 0);
		}catch(Exception e) {
			throw new Exception(e.getMessage());
		}
		
		if(StringUtils.isNotBlank(returnValue)) {
			DefaultHttpObject defaultHttpObject = JSONObject.parseObject(returnValue, DefaultHttpObject.class);
			if(defaultHttpObject != null && defaultHttpObject.getMsg().equals(ConstantUtils.PING_SUCCESS)) {
				// 验证数据摘要
				String dataDigest = JSONObject.parseObject(returnValue).getString(ConstantUtils.DATA_DIGEST);
				boolean isNeedUpdate = false;
				// 如果是第一次进行ping操作，或者数据摘要满足都不为空且不相同，那么更新本地数据源
				if(isFirst || StringUtils.isNotBlank(dataDigest) && !dataDigest.equals(DefaultClientConfigManager.getInstance().getDataDigest())) {
					DefaultClientConfigManager.getInstance().setDataDigest(defaultHttpObject.getDataDigest());
					isNeedUpdate = true;
				}
				
				if(isNeedUpdate) {
					Map<String, ClientDataSource> dataSourceMap = null;
					String result = JSONObject.parseObject(returnValue).getString(ConstantUtils.GET_URL_RESULT);
					if(StringUtils.isNotBlank(result)) {
						List<ClientDataSource> dataSourceList = JSONObject.parseArray(returnValue, ClientDataSource.class);
						if(dataSourceList != null && dataSourceList.size() > 0) {
							dataSourceMap = new HashMap<String, ClientDataSource>(dataSourceList.size());
							for(ClientDataSource dataSource : dataSourceList) {
								dataSourceMap.put(dataSource.getSourceName(), dataSource);
								
								// value解密
								try {
									dataSource.setSourceValue(new String(AESEncrypt.decrypt(dataSource.getSourceValue()), "utf-8"));
								}catch(Exception e) {
									LOGGER.error(e.getMessage(), e);
								}
							}
							
						}
					}
					DefaultDataStorageAdapter.getInstance().loadDataByProjectId(true, true, dataSourceMap);
					return true;
				}
			}
		}
		
		throw new Exception("confcenter system error, so we read config from local.");
	}

}
