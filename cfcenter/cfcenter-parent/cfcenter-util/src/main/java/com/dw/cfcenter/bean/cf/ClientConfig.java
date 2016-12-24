package com.dw.cfcenter.bean.cf;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dw.cfcenter.util.ConstantUtils;
import com.dw.cfcenter.util.PropertyUtils;

/**
 * Description:客户端配置文件
 * @author caohui
 */
public class ClientConfig {
    
	private final static Logger LOGGER = LoggerFactory.getLogger(ClientConfig.class);
	
	// 系统配置
	private final static String DEFAULT_OPTS_DOMAIN_NAME = "cfcenter.domain";
	
	/*********必配项************/
	// 轮询上报服务器的域名
	public String serverDomain = "http://cfcenter.test.com/";
	// 项目名称
	public String projectName;
	// 项目秘钥
	public String key;
	
	/**********可选项************/
	// 本机默认端口号
	public int localServerPort = 5566;
	// ping服务器的频率，单位为秒
	public int pingFrequency = 60;
	// 加载数据频率，默认3小时
	public int loadDataFrequency = 3 * 60 * 60;
	// 是否需要本机存储文件，也就是存储配置中心中该项目的一个副本到本地
	public boolean needStorage = true;
	// 是否校验服务端请求
	public boolean isCheckServerRequest = false;
	// 是否降级
	public boolean isDegrade = true;
	// 向服务器发送多少次失败后自动降级，默认10次
	public int failCountToDegrade = 10;
	// 发生故障时，是否自动降级
	public boolean failAutoDegrade = false;
	// 是否需要定时
	public boolean isNeedTimingLoadData = false;
	// 自定义文件名
	public String customFileName = "custom_conf_file";
	// 校验数据 是否实时比较 默认为非实时比较
	private boolean isRealCompareData = false;
	
	// 作为数据对比的数据签名
	private String dataDigest;
	
	// 排除的文件
	private String excludeFiles;
	// 回调函数线程数量
	private int callBackThreadCount = 1;
	
	public ClientConfig(String properties) throws Exception {
		Properties propertiesFile = PropertyUtils.getProperties(properties);
		
		// 服务器域名、项目名称、key三个必须项，如果没有找到配置文件就使用公共名称
		if(propertiesFile == null) {
			// 从系统配置项中看是否存在
			String domain = System.getProperty(DEFAULT_OPTS_DOMAIN_NAME);
			if(StringUtils.isNotBlank(domain)) {
				this.serverDomain = domain;
			}else {
				LOGGER.error("没有从类路径找到配置文件：{}, 也没有从系统配置项中找到服务器域名", properties);
				throw new Exception("没有从类路径找到配置文件:" + properties + "并且也没有从系统配置项中找到服务器域名");
			}
			
			this.projectName = ConstantUtils.COMMON_PROJECT;
			this.key = ConstantUtils.COMMON_PROJECT_KEY;
		}else {
			
			/*********三个必须项如果没有配置则抛出异常************/
			// 设置项目名称
			String projectNameKey = propertiesFile.getProperty("projectName");
			if(StringUtils.isNotBlank(projectNameKey)) {
				this.projectName = projectNameKey;
			}else {
				throw new Exception("在配置文件" + properties + "中没有projectName配置项，请检查!!");
			}
			
			// 设置key
			String key = propertiesFile.getProperty("key");
			if(StringUtils.isNotBlank(key)) {
				this.key = key;
			}else {
				throw new Exception("在配置文件" + properties + "中没有key配置项，请检查!!");
			}
			
			// 设置domain
			String serverDomainKey = propertiesFile.getProperty("serverDomain");
			if(StringUtils.isNotBlank(serverDomainKey)) {
				this.serverDomain = serverDomainKey;
			}else {
				throw new Exception("在配置文件" + properties + "中没有serverDomain配置项，请检查!!");
			}
			
			/**********可选项*************/
			// 设置port
			String portKey = propertiesFile.getProperty("port");
			if(StringUtils.isNotBlank(portKey)) {
				this.localServerPort = Integer.parseInt(portKey);
			}
			
			// 设置通知频率
			String pingFrequencyKey = propertiesFile.getProperty("pingFrequency");
			if(StringUtils.isNotBlank(pingFrequencyKey)) {
				pingFrequency = Integer.parseInt(pingFrequencyKey);
			}
			
			// 设置本机是否需要存储文件
			String needStorageKey = propertiesFile.getProperty("needStorage");
			if(StringUtils.isNotBlank(needStorageKey)) {
				this.needStorage = Boolean.parseBoolean(needStorageKey);
			}
			
			// 设置加载数据频率
			String loadDataFrequency = propertiesFile.getProperty("loadDataFrequency");
			if(StringUtils.isNotBlank(loadDataFrequency)) {
				this.loadDataFrequency = Integer.parseInt(loadDataFrequency);
			}
			
			// 是否校验服务端请求
			String isCheckServerRequestStr = propertiesFile.getProperty("isCheckServerRequest");
			if(StringUtils.isNotBlank(isCheckServerRequestStr)) {
				this.isCheckServerRequest = Boolean.parseBoolean(isCheckServerRequestStr);
			}
			
			// 是否降级
			String isDegradeStr = propertiesFile.getProperty("isDegrade");
			if(StringUtils.isNotBlank(isDegradeStr)) {
				this.isDegrade = Boolean.parseBoolean(isDegradeStr);
			}
			
			// 向服务端发送多少次失败请求后自动降级，默认10次
			String failCountToDegradeStr = propertiesFile.getProperty("failCountToDegrade");
			if(StringUtils.isNotBlank(failCountToDegradeStr)) {
				this.failCountToDegrade = Integer.parseInt(failCountToDegradeStr);
			}
			
			// 故障时自动降级
			String failAutoDegradeStr = propertiesFile.getProperty("failAutoDegrade");
			if(StringUtils.isNotBlank(failAutoDegradeStr)) {
				this.failAutoDegrade = Boolean.parseBoolean(failAutoDegradeStr);
			}
			
			// 自定义文件名称
			String customFileNameStr = propertiesFile.getProperty("customFileName");
			if(StringUtils.isNotBlank(customFileNameStr)) {
				this.customFileName = customFileNameStr;
			}
			
			// 是否需要定时轮询
			String isNeedTimingLoadDataStr = propertiesFile.getProperty("isNeedTimingLoadData");
			if(StringUtils.isNotBlank(isNeedTimingLoadDataStr)) {
				this.isNeedTimingLoadData = Boolean.parseBoolean(isNeedTimingLoadDataStr);
			}
			
			// 是否实时比较校验数据
			String isRealCompareDataStr = propertiesFile.getProperty("isRealCompareData");
			if(StringUtils.isNotBlank(isRealCompareDataStr)) {
				this.isRealCompareData = Boolean.parseBoolean(isRealCompareDataStr);
			}
			
			// 排除的文件
			String excludeFileStr = propertiesFile.getProperty("excludeFiles");
			if(StringUtils.isNotBlank(excludeFileStr)) {
				this.excludeFiles = excludeFileStr;
			}
			
			// 回调函数线程数量
			String callBackThreadCountStr = propertiesFile.getProperty("callBackThreadCount");
			if(StringUtils.isNotBlank(callBackThreadCountStr)) {
				this.callBackThreadCount = Integer.parseInt(callBackThreadCountStr);
			}
		}
	}

	public String getServerDomain() {
		return serverDomain;
	}

	public void setServerDomain(String serverDomain) {
		this.serverDomain = serverDomain;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getLocalServerPort() {
		return localServerPort;
	}

	public void setLocalServerPort(int localServerPort) {
		this.localServerPort = localServerPort;
	}

	public int getPingFrequency() {
		return pingFrequency;
	}

	public void setPingFrequency(int pingFrequency) {
		this.pingFrequency = pingFrequency;
	}


	public int getLoadDataFrequency() {
		return loadDataFrequency;
	}

	public void setLoadDataFrequency(int loadDataFrequency) {
		this.loadDataFrequency = loadDataFrequency;
	}

	public boolean isNeedStorage() {
		return needStorage;
	}

	public void setNeedStorage(boolean needStorage) {
		this.needStorage = needStorage;
	}

	public boolean isCheckServerRequest() {
		return isCheckServerRequest;
	}

	public void setCheckServerRequest(boolean isCheckServerRequest) {
		this.isCheckServerRequest = isCheckServerRequest;
	}

	public boolean isDegrade() {
		return isDegrade;
	}

	public void setDegrade(boolean isDegrade) {
		this.isDegrade = isDegrade;
	}

	public int getFailCountToDegrade() {
		return failCountToDegrade;
	}

	public void setFailCountToDegrade(int failCountToDegrade) {
		this.failCountToDegrade = failCountToDegrade;
	}

	public boolean isFailAutoDegrade() {
		return failAutoDegrade;
	}

	public void setFailAutoDegrade(boolean failAutoDegrade) {
		this.failAutoDegrade = failAutoDegrade;
	}

	public boolean isNeedTimingLoadData() {
		return isNeedTimingLoadData;
	}

	public void setNeedTimingLoadData(boolean isNeedTimingLoadData) {
		this.isNeedTimingLoadData = isNeedTimingLoadData;
	}

	public String getCustomFileName() {
		return customFileName;
	}

	public void setCustomFileName(String customFileName) {
		this.customFileName = customFileName;
	}

	public boolean isRealCompareData() {
		return isRealCompareData;
	}

	public void setRealCompareData(boolean isRealCompareData) {
		this.isRealCompareData = isRealCompareData;
	}

	public String getExcludeFiles() {
		return excludeFiles;
	}

	public void setExcludeFiles(String excludeFiles) {
		this.excludeFiles = excludeFiles;
	}

	public int getCallBackThreadCount() {
		return callBackThreadCount;
	}

	public void setCallBackThreadCount(int callBackThreadCount) {
		this.callBackThreadCount = callBackThreadCount;
	}

	public String getDataDigest() {
		return dataDigest;
	}

	public void setDataDigest(String dataDigest) {
		this.dataDigest = dataDigest;
	}
	
}
