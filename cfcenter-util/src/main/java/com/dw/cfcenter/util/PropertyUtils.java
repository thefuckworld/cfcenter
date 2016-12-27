package com.dw.cfcenter.util;

import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description: 加载properties文件工具
 * @author caohui
 */
public final class PropertyUtils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PropertyUtils.class);
	
	private PropertyUtils() {}
	
	public static Properties getProperties(String propertiesName) {
		InputStream fis = null;
		Properties properties = null;
		try {
			fis = PropertyUtils.class.getResourceAsStream("/" + propertiesName + ".properties");
			if(fis != null) {
				properties = new Properties();
				properties.load(fis);
			}
		}catch(Exception e) {
			LOGGER.error("客户端加载配置文件抛出异常", e);
		}finally {
			if(fis != null) {
				try {
					fis.close();
				}catch(Exception e) {}
			}
		}
		return properties;
	}

	public static Properties getPropertiesWithoutSuffix(String propertiesName) {
		InputStream fis = null;
		Properties properties = null;
		try {
			fis = PropertyUtils.class.getResourceAsStream("/" + propertiesName);
			if(fis != null) {
				properties = new Properties();
				properties.load(fis);
			}
		}catch(Exception e) {
			LOGGER.error("客户端加载配置文件抛出异常", e);
		}finally {
			if(fis != null) {
				try {
				  fis.close();
				}catch(Exception e) {}
			}
		}
		return properties;
	}
}
