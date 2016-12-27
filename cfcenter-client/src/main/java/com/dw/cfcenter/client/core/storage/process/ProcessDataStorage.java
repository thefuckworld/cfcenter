package com.dw.cfcenter.client.core.storage.process;

import java.util.LinkedHashMap;
import java.util.Map;

import com.dw.cfcenter.bean.vo.ClientDataSource;

/**
 * Description:进程存储
 * @author caohui
 */
public class ProcessDataStorage {
	
	public Map<String, ClientDataSource> processData = new LinkedHashMap<String, ClientDataSource> ();

	/**
	 * Description:
	 * All Rights Reserved.
	 *
	 * @param dataSourceMap
	 * @return void
	 * @version 1.0 2016年11月10日 上午4:04:46 created by caohui(1343965426@qq.com)
	 */
	public void refreshData(Map<String, ClientDataSource> dataSourceMap) {
		if(dataSourceMap != null && dataSourceMap.size() > 0) {
			synchronized(this) {
				processData.clear();
				for(String key : dataSourceMap.keySet()) {
					processData.put(key, dataSourceMap.get(key));
				}
			}
			return;
		}
		
		processData.clear();
	}
	
	public Map<String, ClientDataSource> cloneData() {
		if(processData != null && processData.size() > 0) {
			Map<String, ClientDataSource> map = new LinkedHashMap<String, ClientDataSource> ();
			for(String key : processData.keySet()) {
				map.put(key, processData.get(key));
			}
			return map;
		}
		return null;
	}
}
