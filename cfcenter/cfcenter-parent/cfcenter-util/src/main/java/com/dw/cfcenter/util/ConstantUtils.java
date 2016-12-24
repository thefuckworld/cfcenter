package com.dw.cfcenter.util;

/**
 * Description: 常亮工具类
 * @author caohui
 */
public final class ConstantUtils {

	private ConstantUtils() {}
	
	// 配置项默认值
	public static final String COMMON_PROJECT = "common";
	public static final String COMMON_PROJECT_KEY = "common_project_key";
	
	// 数据是否加密， 0代表不加密 1代表加密
	public static final byte DS_NOT_ENCRYPT = 0;
	public static final byte DS_ENCRYPT = 1;
	
	// 上报返回成功消息
	public static final String PING_SUCCESS = "success";
	// 上报服务端参数名称
	public static final String PING_PARAM_ID = "id";
	public static final String PING_PARAM_IP = "ip";
	public static final String PING_PARAM_PORT = "port";
	public static final String PING_PARAM_PROJECT = "projectName";
	public static final String DATASOURCE_KEY = "key";
	public static final String DS_NAME = "dsName";
	public static final String NEED_SAVE = "isNeedSave";
	public static final String DATA_DIGEST = "dataDigest";
	
	// 上报URL后缀
	public static final String PING_URL_SUFFIX = "/cfcenter-admin/client/ping.json?";
	// 得到所有结果数据源请求的URL后缀
	public static final String GETALL_URL_SUFFIX = "/cfcenter-admin/client/loadDataByServerId.json?";
	// 获取单个数据源并且关联数据源
	public static final String GET_DS_RELATION_SUFFIX = "/cfcenter-admin/client/getDsAndRelation.json?";
	// 获得服务器信息请求URL后缀
	public static final String SERVER_INFO_URL_SUFFIX = "/cfcenter-admin/client/server.json?";
	// 监控URL
	public static final String MONITOR_URL = "/cfcenter-admin/monitor/clientReceive.json";
	// 修改服务端数据源URL
	public static final String UPDATE_DATASOURCE_URL = "/cfcenter-admin/client/client/updateDataSource.json";
	// 返回结果
	public static final String GET_URL_RESULT = "data";
	public static final String DATASOURCE_RESULT = "dataSource";
	public static final String SERVER_URL_RESULT = "serverList";
	
}
