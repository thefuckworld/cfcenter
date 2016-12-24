package com.dw.cfcenter.client.core.scheduling;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dw.cfcenter.client.core.security.server.ServerConfigList;
import com.dw.cfcenter.client.core.storage.DataStorageAdapter;
import com.dw.cfcenter.client.core.storage.DefaultDataStorageAdapter;
import com.dw.cfcenter.client.core.transfer.impl.HttpPingServer;
import com.dw.cfcenter.client.manager.DefaultClientConfigManager;

/**
 * Description: 任务调度管理器
 * @author caohui
 */
public final class SchedulingTaskManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(SchedulingTaskManager.class);
	
	private static final int LOAD_ALL_SERVER_TIME = 300;
	private volatile boolean isFirstInit = true;
	private static volatile long lastPingTime = System.currentTimeMillis();
	private ScheduledExecutorService schedulingService = Executors.newSingleThreadScheduledExecutor();
	
	/**
	 * Description: 初始化调度器
	 * All Rights Reserved.
	 *
	 * @param pingFrequency        ping服务器的频率
	 * @param loadDataFrequency    加载数据的频率
	 *
	 * @version 1.0 2016年11月10日 上午12:49:28 created by caohui(1343965426@qq.com)
	 */
	public SchedulingTaskManager() {
		try {
			// 校验是否需要更新数据，如果需要更新则进行更新
			HttpPingServer.getInstance().pingAndVertify(isFirstInit);
			isFirstInit = false;
		}catch(Exception e) {
			// 异常时读取本地副本
			DefaultDataStorageAdapter.getInstance().loadDataByProjectId(true, false, null);
		}
		
		if(DefaultClientConfigManager.getInstance().getClientConfig().isCheckServerRequest) {
			ServerConfigList.refreshServerConfig();
		}
		
	}
	
	public void startJob(long pingFrequency, long loadDataFrequency) {
		// 初始化ping定时任务：检验是否需要更新，如果需要更新则进行更新
		schedulingService.scheduleAtFixedRate(new PingScheduling(), pingFrequency, pingFrequency, TimeUnit.SECONDS);
		
		// 初始化加载数据定时任务：从服务器端加载数据到本地
		if(DefaultClientConfigManager.getInstance().getClientConfig().isNeedTimingLoadData) {
			schedulingService.scheduleAtFixedRate(new LoadDataScheduling(), loadDataFrequency, loadDataFrequency, TimeUnit.SECONDS);
		}
		
		// 初始化获取服务端有效IP列表
		if(DefaultClientConfigManager.getInstance().getClientConfig().isCheckServerRequest) {
			schedulingService.scheduleAtFixedRate(new LoadAllServerScheduling(), LOAD_ALL_SERVER_TIME, LOAD_ALL_SERVER_TIME, TimeUnit.SECONDS);
		}
	}
	
	/**
	 * Description: ping定时任务
	 * @author caohui
	 */
	class PingScheduling implements Runnable {
		@Override
		public void run() {
			
			boolean pingSuccess = false;
			
			try {
				pingSuccess = HttpPingServer.getInstance().pingAndVertify(isFirstInit);
				isFirstInit = false;
			}catch(Exception e) {
				LOGGER.warn(e.getMessage(), e);
			}
			
			if(!pingSuccess) {
				long current = System.currentTimeMillis();
				if(current - lastPingTime > 1000 * 60 * 5L) {
					// TODO 如果5分钟还未发送心跳成功，则发送报警
					LOGGER.error("ping {} is failed, please check your config and notice confcenter manager", DefaultClientConfigManager.getInstance().getClientConfig().getServerDomain());
					lastPingTime = current;
				}
				return;
			}
			lastPingTime = System.currentTimeMillis();
		}
	}
	
	/**
	 * Description: 加载全部数据定时任务
	 * @author caohui
	 */
	class LoadDataScheduling implements Runnable {
		@Override
		public void run() {
			
			DataStorageAdapter adapter = DefaultDataStorageAdapter.getInstance();
			adapter.loadDataByProjectId(true, false, null);
		}
	}
	
	/**
	 * Description: 获取服务端有效IP列表定时任务
	 * @author caohui
	 */
	class LoadAllServerScheduling implements Runnable {
		@Override
		public void run() {
			ServerConfigList.refreshServerConfig();
		}
	}
}
