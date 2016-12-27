package com.dw.cfcenter.client.core.security.server;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.dw.cfcenter.client.core.transfer.impl.HttpServerTools;

/**
 * Description:
 * @author caohui
 */
public class ServerConfigList {

	public static Set<String> serverIpList = new CopyOnWriteArraySet<String>();
	
	/**
	 * Description: 刷新服务端列表
	 * All Rights Reserved.
	 *
	 * @return void
	 * @version 1.0 2016年11月10日 下午7:06:13 created by caohui(1343965426@qq.com)
	 */
	public static void refreshServerConfig() {
		List<String> valueList = HttpServerTools.getInstance().getServerInfo();
		if(valueList != null && valueList.size() > 0) {
			synchronized(serverIpList) {
				serverIpList.clear();
				serverIpList.addAll(valueList);
			}
		}
	}
}
