package com.dw.cfcenter.client.core.transfer.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.dw.cfcenter.client.manager.DefaultClientConfigManager;
import com.dw.cfcenter.util.ConstantUtils;
import com.dw.cfcenter.util.DefaultHttpClient;
import com.dw.cfcenter.util.LocalUtils;

/**
 * Description:
 * @author caohui
 */
public final class HttpServerTools {

	private static HttpServerTools httpServerTools;
	public static final String SEPARATOR = ",";
	
	private HttpServerTools() {}
	
	public static HttpServerTools getInstance() {
		if(httpServerTools == null) {
			synchronized(HttpServerTools.class) {
				httpServerTools = new HttpServerTools();
			}
		}
		return httpServerTools;
	}
	
	/**
	 * Description: 获取有效服务器
	 * All Rights Reserved.
	 *
	 * @return
	 * @return List<String>
	 * @version 1.0 2016年11月10日 下午7:17:20 created by caohui(1343965426@qq.com)
	 */
	public List<String> getServerInfo() {
		Map<String, String> params = new HashMap<String, String> ();
		params.put(ConstantUtils.PING_PARAM_IP, LocalUtils.getLocalIp());
		params.put(ConstantUtils.PING_PARAM_PORT, String.valueOf(DefaultClientConfigManager.getInstance().getClientConfig().getLocalServerPort()));
		params.put(ConstantUtils.PING_PARAM_PROJECT, String.valueOf(DefaultClientConfigManager.getInstance().getClientConfig().getProjectName()));
		params.put(ConstantUtils.DATASOURCE_KEY, String.valueOf(DefaultClientConfigManager.getInstance().getClientConfig().getKey()));
		
		String returnValue = null;
		try {
			returnValue = DefaultHttpClient.doGet(DefaultClientConfigManager.getInstance().getClientConfig().getServerDomain() + ConstantUtils.SERVER_INFO_URL_SUFFIX, params, 0, 0);
		}catch(Exception e) {
			return null;
		}
		
		if(StringUtils.isBlank(returnValue)) {
			return null;
		}
		
		String result = JSONObject.parseObject(returnValue).getString(ConstantUtils.SERVER_URL_RESULT);
		if(StringUtils.isNotBlank(result)) {
			String[] args = result.split(SEPARATOR);
			List<String> returnList = new ArrayList<String> ();
			if(args != null && args.length > 0) {
				for(int i=0; i<args.length; i++) {
					returnList.add(args[i]);
				}
				return returnList;
			}
		}
		return null;
	}
}
