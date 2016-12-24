package com.dw.cfcenter.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class JsonUtils {

	private JsonUtils() {}
	
	private static ObjectMapper objectMapper = new ObjectMapper();
	static {
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		objectMapper.setTimeZone(TimeZone.getDefault());
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
	}
	
	/**
	 * Description: 转换成json串
	 * All Rights Reserved.
	 *
	 * @param object
	 * @return
	 * @return String
	 * @version 1.0 2016年11月11日 下午6:25:47 created by caohui(1343965426@qq.com)
	 */
	public static String toJson(Object object) {
		try {
			StringWriter sw = new StringWriter();
			objectMapper.writeValue(sw, object);
			return sw.toString();
		}catch(IOException e) {}
		return null;
	}
	
	/**
	 * Description: 转换成对象
	 * All Rights Reserved.
	 *
	 * @param value
	 * @param clazz
	 * @return
	 * @return T
	 * @version 1.0 2016年11月11日 下午6:27:59 created by caohui(1343965426@qq.com)
	 */
	public static<T> T parseObject(String value, Class<T> clazz) {
		try {
			return objectMapper.readValue(value, clazz);
		}catch(IOException e) {}
		return null;
	}
	
	/**
	 * Description:
	 * All Rights Reserved.
	 *
	 * @param value
	 * @param type
	 * @return
	 * @return T
	 * @version 1.0 2016年11月11日 下午6:31:35 created by caohui(1343965426@qq.com)
	 */
	public static<T> T parseObject(String value, TypeReference<T> type) {
		try {
			return objectMapper.readValue(value, type);
		}catch(IOException e) {}
		return null;
	}
	
	/**
	 * Description:
	 * All Rights Reserved.
	 *
	 * @param value
	 * @param clazz
	 * @return
	 * @return T
	 * @version 1.0 2016年11月11日 下午6:31:20 created by caohui(1343965426@qq.com)
	 */
	public static<T> T parseObject(byte[] value, Class<T> clazz) {
		try {
			return objectMapper.readValue(value, clazz);
		}catch(IOException e) {}
		return null;
	}
}
