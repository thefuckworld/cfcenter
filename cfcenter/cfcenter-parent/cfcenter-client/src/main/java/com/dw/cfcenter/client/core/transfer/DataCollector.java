package com.dw.cfcenter.client.core.transfer;

import java.util.Map;

import com.dw.cfcenter.bean.vo.ClientDataSource;

/**
 * Description: 数据收集器
 * @author caohui
 */
public interface DataCollector {

	/**
	 * Description: 获取全部数据
	 * All Rights Reserved.
	 *
	 * @return
	 * @throws Exception
	 * @return Map<String,ClientDataSource>
	 * @version 1.0 2016年11月10日 下午3:10:08 created by caohui(1343965426@qq.com)
	 */
	Map<String, ClientDataSource> getAllData() throws Exception;
	
	/**
	 * Description: 从远程服务器拉取数据源并且关联数据源和项目
	 * All Rights Reserved.
	 *
	 * @param key
	 * @return
	 * @return ClientDataSource
	 * @version 1.0 2016年11月10日 下午3:11:04 created by caohui(1343965426@qq.com)
	 */
	ClientDataSource getDataSourceFromServerAndRelation(String key);
}
