package com.dw.cfcenter.client.core.storage.file;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dw.cfcenter.bean.vo.ClientDataSource;
import com.dw.cfcenter.bean.vo.DataSourceChange;
import com.dw.cfcenter.client.core.storage.DataStorageAdapter;
import com.dw.cfcenter.client.manager.DefaultClientConfigManager;
import com.dw.cfcenter.util.PropertyUtils;

/**
 * Description: 为了实现降级策略，业务自定义的数据出口
 * @author caohui
 */
public class CustomDataStorageAdapter implements DataStorageAdapter{

	private final static Logger LOGGER = LoggerFactory.getLogger(CustomDataStorageAdapter.class);
	private static CustomDataStorageAdapter customDataStorageAdapter;
	
	// 本机配置文件的进程缓存副本
	public static Map<String, ClientDataSource> dataMap = new HashMap<String, ClientDataSource>();
	// 本机配置文件，防止key重复问题，所以多了一层关系
	public static Map<String, Map<String, String>> propertiesDataMap = new HashMap<String, Map<String, String>>();
	
	private static Set<String> excludeFileName = new HashSet<String> ();
	
	private static final String SEPARATOR = ",";
	private static final String SUFFIX = ".properties";
	private CustomDataStorageAdapter() {}
	
	public static void init() {
		if(customDataStorageAdapter == null) {
			synchronized(CustomDataStorageAdapter.class) {
				customDataStorageAdapter = new CustomDataStorageAdapter();
				// 先更新排除的文件
				String excludeFiles = DefaultClientConfigManager.getInstance().getClientConfig().getExcludeFiles();
				if(StringUtils.isNotBlank(excludeFiles)) {
					String[] excludeFileArray = excludeFiles.split(SEPARATOR);
					for(String fileName : excludeFileArray) {
						excludeFileName.add(fileName + SUFFIX);
					}
				}
				
				// 遍历classpath下面的properties文件，加载到本机内存
				File file = new File(String.valueOf(CustomDataStorageAdapter.class.getClassLoader().getResource("").getFile()));
				if(file != null && file.isDirectory()) {
					File[] tempList = file.listFiles();
					if(tempList != null && tempList.length > 0) {
						for(int i=0; i<tempList.length; i++) {
							if(tempList[i].isFile()) {
								// 排除特殊文件
								if(!tempList[i].getName().endsWith(SUFFIX) ||
										(DefaultClientConfigManager.propertiesFile + SUFFIX).equals(tempList[i].getName()) ||
										(excludeFileName.contains(tempList[i].getName()))) {
									continue;
								}
								
								Properties properties = PropertyUtils.getPropertiesWithoutSuffix(tempList[i].getAbsoluteFile().getName());
								if(properties != null) {
									Enumeration enumeration = properties.keys();
									propertiesDataMap.put(tempList[i].getAbsoluteFile().getName(), new HashMap<String, String> ());
									while(enumeration.hasMoreElements()) {
										String key = (String) enumeration.nextElement();
										// 填充DataMap
										ClientDataSource clientDataSource = new ClientDataSource();
										clientDataSource.setSourceName(key);
										clientDataSource.setSourceValue(properties.getProperty(key));
										dataMap.put(key, clientDataSource);
										
										// 填充propertiesDataMap
										propertiesDataMap.get(tempList[i].getAbsoluteFile().getName()).put(key, properties.getProperty(key));
									}
								}
							}
						}
					}else {
						LOGGER.error("配置中心未发现可加载的数据源文件");
					}
				}else {
					LOGGER.error("配置中心未发现可加载的数据源文件");
				}
			}
		}
	}
	
	public static CustomDataStorageAdapter getInstance() {
		return customDataStorageAdapter;
	}
	
	/**
	 * Description: 刷新本地缓存副本
	 * All Rights Reserved.
	 *
	 * @return void
	 * @version 1.0 2016年11月10日 下午4:55:25 created by caohui(1343965426@qq.com)
	 */
	public void refreshDataMap() {
		synchronized(dataMap) {
			// 先清空dataMap
			dataMap.clear();
			// 遍历propertiesDataMap
			for(String fileKey : propertiesDataMap.keySet()) {
				for(String sourceKey : propertiesDataMap.get(fileKey).keySet()) {
					ClientDataSource clientDataSource = new ClientDataSource();
					clientDataSource.setSourceName(sourceKey);
					clientDataSource.setSourceValue(propertiesDataMap.get(fileKey).get(sourceKey));
					dataMap.put(sourceKey, clientDataSource);
				}
			}
		}
	}
	
	/**
	 * Description: 转换对propertiesFile中的key，convertMap中的key为本地数据源，value为配置中心的key
	 * All Rights Reserved.
	 *
	 * @param propertiesFile
	 * @param convertMap          key为本地数据源，value为配置中心的key
	 * @return void
	 * @version 1.0 2016年11月10日 下午4:58:58 created by caohui(1343965426@qq.com)
	 */
	public void convertKeyMap(String propertiesFile, Map<String, String> convertMap) {
		if(StringUtils.isBlank(propertiesFile) || convertMap == null) {
			return;
		}
		
		synchronized(propertiesDataMap) {
			if(propertiesDataMap.containsKey(propertiesFile)) {
				boolean isNeedFresh = false;
				for(String key : convertMap.keySet()) {
					if(propertiesDataMap.get(propertiesFile).containsKey(key)) {
						// 先取出原有的配置的值
						String value = propertiesDataMap.get(propertiesFile).get(key);
						// 清除之前的key
						propertiesDataMap.get(propertiesFile).remove(key);
						// 增加新key和值
						propertiesDataMap.get(propertiesFile).put(convertMap.get(key), value);
						isNeedFresh = true;
					}
				}
				
				if(isNeedFresh) {
					refreshDataMap();
				}
			}
		}
	}
	@Override
	public Map<String, ClientDataSource> loadDataByProjectId(
			boolean isFromServer, boolean isUseParam,
			Map<String, ClientDataSource> dataSourceMap) {
		return dataMap;
	}
	
	/**
	 * Description: 更新用户自定义副本
	 * All Rights Reserved.
	 *
	 * @param dataSourceMap
	 * @return void
	 * @version 1.0 2016年11月10日 下午5:16:59 created by caohui(1343965426@qq.com)
	 */
	public void refreshCustomData(Map<String, ClientDataSource> dataSourceMap) {
		
	}

	@Override
	public ClientDataSource getDataSourceAndRelationIfNotExists(
			boolean isFromServer, String key) {
		return dataMap.get(key);
	}

	@Override
	public void changeDataFromSocket(String ip, String dataMessage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateServerDataSource(DataSourceChange dataSourceChange)
			throws Exception {
		// TODO Auto-generated method stub
		
	}
}
