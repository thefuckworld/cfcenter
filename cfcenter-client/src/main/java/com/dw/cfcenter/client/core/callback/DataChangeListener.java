package com.dw.cfcenter.client.core.callback;

import com.dw.cfcenter.bean.vo.DataSourceTransport;

/**
 * Description: 自定义回调函数接口
 * @author caohui
 */
public interface DataChangeListener {

	void call(DataSourceTransport dataSourceTransport);
}
