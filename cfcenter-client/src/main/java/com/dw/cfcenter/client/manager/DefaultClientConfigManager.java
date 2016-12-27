package com.dw.cfcenter.client.manager;

import java.net.BindException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dw.cfcenter.bean.cf.ClientConfig;
import com.dw.cfcenter.bean.vo.ClientDataSource;
import com.dw.cfcenter.client.api.ConfCenterApi;
import com.dw.cfcenter.client.api.impl.DefaultConfCenterApi;
import com.dw.cfcenter.client.core.callback.BatchCallBackListener;
import com.dw.cfcenter.client.core.callback.DataChangeListener;
import com.dw.cfcenter.client.core.scheduling.SchedulingTaskManager;
import com.dw.cfcenter.client.core.storage.DefaultDataStorageAdapter;
import com.dw.cfcenter.client.core.storage.file.CustomDataStorageAdapter;
import com.dw.cfcenter.client.core.transfer.ReceiveServer;
import com.dw.cfcenter.client.core.transfer.impl.TcpReceiveServer;
import com.dw.cfcenter.util.DigitalDigest;
/**
 * Description: 客户端配置文件管理器
 * @author caohui
 */
public final class DefaultClientConfigManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultClientConfigManager.class);
	
	private ClientConfig clientConfig;
	// 自定义监听器
	private List<DataChangeListener> dataChangeListenerList;
	// 自定义批量回调函数监听器
	private List<BatchCallBackListener> batchCallBackListeners;
	// 该项目对应数据源的数据签名
	private String dataDigest;
	
	// 接收回调消息server
	private ReceiveServer receiveServer;
	private static DefaultClientConfigManager defaultClientConfigManager;
	
	// 用户访问的API对象
	private ConfCenterApi confCenterApi = DefaultConfCenterApi.newInstance();
	
	private static final int DEFAULT_INCREASE_PORT = 100;
	
	// 配置文件名称
	public static String propertiesFile = "conf_center";
	
	// 初始化开关
	private static volatile boolean isInit = false;
	public static volatile boolean isInitSuccess = true;
	
	private DefaultClientConfigManager() {}
	
	// 通过properties配置文件获取数据
	public static DefaultClientConfigManager getInstance(String propertiesPath) {
		propertiesFile = propertiesPath;
		return getInstance();
	}
	
	/**
	 * Description: 得到客户端配置文件管理器单例
	 * All Rights Reserved.
	 *
	 * @return DefaultClientConfigManager
	 * @version 1.0 2016年11月10日 上午12:20:20 created by caohui(1343965426@qq.com)
	 */
	public static DefaultClientConfigManager getInstance() {
		if(!isInit) {
			synchronized(DefaultClientConfigManager.class) {
				if(!isInit) {
					synchronized(DefaultClientConfigManager.class) {
						defaultClientConfigManager = new DefaultClientConfigManager();
						try {
							// 初始化配置文件
							defaultClientConfigManager.setClientConfig(new ClientConfig(propertiesFile));
							// 初始化数据
							init(defaultClientConfigManager);
							isInit = true;
						}catch(Exception e) {
							isInitSuccess = false;
							LOGGER.error("初始化配置中心失败", e);
						}
					}
				}
			}
		}
		
		return defaultClientConfigManager;
	}

	/**
	 * Description: 根据客户端配置文件开关初始化数据
	 * All Rights Reserved.
	 *
	 * @param DefaultClientConfigManager
	 * @return void
	 * @version 1.0 2016年11月10日 上午12:30:31 created by caohui(1343965426@qq.com)
	 * @throws Exception 
	 */
	public static void init(DefaultClientConfigManager defaultClientConfigManager) throws Exception {
		// 如果不是降级读取本地数据源
		if(!defaultClientConfigManager.getClientConfig().isDegrade) {
			// 优先创建定时任务，加载数据和ping server
			new SchedulingTaskManager().startJob(defaultClientConfigManager.getClientConfig().getPingFrequency(), defaultClientConfigManager.getClientConfig().getLoadDataFrequency());
			// 设置回调端口号
			setTcpPort(null, defaultClientConfigManager);
			return;
		}
		
		// 降级读取，初始化客户端自定义数据源文件
		CustomDataStorageAdapter.init();
		LOGGER.warn("conf-center is not use, currently read your project classpath local file");
	}
	
	
	// 设置TCP回调端口号
	public static void setTcpPort(TcpReceiveServer tcpReceiveServer, DefaultClientConfigManager defalutClientConfigManager) throws Exception {
		try {
			if(tcpReceiveServer == null) {
				tcpReceiveServer = new TcpReceiveServer();
			}
			defaultClientConfigManager.setReceiveServer(tcpReceiveServer);
			defaultClientConfigManager.getReceiveServer().start(defaultClientConfigManager.getClientConfig().getLocalServerPort());
		}catch(BindException e1) {
			defaultClientConfigManager.getClientConfig().setLocalServerPort(defaultClientConfigManager.getClientConfig().getLocalServerPort() + DEFAULT_INCREASE_PORT);
			setTcpPort(tcpReceiveServer, defaultClientConfigManager);
		}catch(Exception e) {
			throw new Exception(e);
		}
	}
	public ClientConfig getClientConfig() {
		return clientConfig;
	}

	public void setClientConfig(ClientConfig clientConfig) {
		this.clientConfig = clientConfig;
	}

	public List<DataChangeListener> getDataChangeListenerList() {
		return dataChangeListenerList;
	}

	public void setDataChangeListenerList(
			List<DataChangeListener> dataChangeListenerList) {
		this.dataChangeListenerList = dataChangeListenerList;
	}

	public List<BatchCallBackListener> getBatchCallBackListeners() {
		return batchCallBackListeners;
	}

	public void setBatchCallBackListeners(
			List<BatchCallBackListener> batchCallBackListeners) {
		this.batchCallBackListeners = batchCallBackListeners;
	}

	public String getDataDigest() {
		if(getClientConfig().isRealCompareData()) {
			Map<String, ClientDataSource> map = DefaultDataStorageAdapter.getInstance().loadDataByProjectId(false, false, null);
			StringBuffer stringBuffer = new StringBuffer();
			if(map != null) {
				for(String key : map.keySet()) {
					stringBuffer.append(map.get(key).getSourceValue());
				}
			}
			
			try {
				return DigitalDigest.encodeByMd5(stringBuffer.toString());
			}catch(Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		return dataDigest;
	}

	public void setDataDigest(String dataDigest) {
		this.dataDigest = dataDigest;
	}

	public ReceiveServer getReceiveServer() {
		return receiveServer;
	}

	public void setReceiveServer(ReceiveServer receiveServer) {
		this.receiveServer = receiveServer;
	}

	public static String getPropertiesFile() {
		return propertiesFile;
	}

	public static void setPropertiesFile(String propertiesFile) {
		DefaultClientConfigManager.propertiesFile = propertiesFile;
	}

	public static boolean isInit() {
		return isInit;
	}

	public static void setInit(boolean isInit) {
		DefaultClientConfigManager.isInit = isInit;
	}


	public ConfCenterApi getConfCenterApi() {
		return confCenterApi;
	}

	public void setConfCenterApi(ConfCenterApi confCenterApi) {
		this.confCenterApi = confCenterApi;
	}
}
