package com.dw.cfcenter.client.core.storage;

import java.util.Map;

import com.dw.cfcenter.bean.vo.ClientDataSource;
import com.dw.cfcenter.bean.vo.DataSourceChange;

/**
 * Description:
 * @author caohui
 */
public interface DataStorageAdapter {

	/**
	 * Description: 通过项目ID获取数据源
	 * All Rights Reserved.
	 *
	 * @param isFromServer
	 * @param isUseParam
	 * @param dataSourceMap
	 * @return
	 * @return Map<String,ClientDataSource>
	 * @version 1.0 2016年11月10日 上午4:09:28 created by caohui(1343965426@qq.com)
	 */
	Map<String, ClientDataSource> loadDataByProjectId(boolean isFromServer, boolean isUseParam, Map<String, ClientDataSource> dataSourceMap);
	
	/**
	 * Description: 先从本地取数据源，没有则从服务器拉取，取到后关联数据源和项目
	 * All Rights Reserved.
	 *
	 * @param isFromServer
	 * @param key
	 * @return
	 * @return ClientDataSource
	 * @version 1.0 2016年11月10日 下午6:06:56 created by caohui(1343965426@qq.com)
	 */
	ClientDataSource getDataSourceAndRelationIfNotExists(boolean isFromServer, String key);
	
	
	/**
	 * Description:
	 * All Rights Reserved.
	 *
	 * @param ip
	 * @param dataMessage
	 * @return void
	 * @version 1.0 2016年12月22日 下午5:07:19 created by caohui(1343965426@qq.com)
	 */
	void changeDataFromSocket(String ip, String dataMessage);
	
	
	/**
	 * Description: 修改服务端数据源
	 * All Rights Reserved.
	 *
	 * @param dataSourceChange
	 * @throws Exception
	 * @return void
	 * @version 1.0 2016年12月23日 下午3:00:48 created by caohui(1343965426@qq.com)
	 */
	void updateServerDataSource(DataSourceChange dataSourceChange) throws Exception;
	
}
