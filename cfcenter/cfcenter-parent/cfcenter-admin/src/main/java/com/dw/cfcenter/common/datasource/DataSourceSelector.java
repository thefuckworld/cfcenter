package com.dw.cfcenter.common.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * Description: 数据源选择器
 * @author caohui
 */

public class DataSourceSelector extends AbstractRoutingDataSource{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceSelector.class);
	
	@Override
	protected Object determineCurrentLookupKey() {
		String dataSource = HandleDataSource.getDataSource();
		LOGGER.debug("使用{}数据库", dataSource);
		return dataSource;
	}
	
	public static class HandleDataSource {
		// 利用ThreadLocal解决线程安全问题
		public static final ThreadLocal<String> holder = new ThreadLocal<String> ();
		
		public static void putDataSource(DataSourceType dataSourceType) {
			if(null == dataSourceType) {
				return;
			}
			
			holder.set(dataSourceType.name());
		}
		
		public static String getDataSource() {
			return holder.get();
		}
	}
	
	public static enum DataSourceType {
		RW, RO;
	}
}
