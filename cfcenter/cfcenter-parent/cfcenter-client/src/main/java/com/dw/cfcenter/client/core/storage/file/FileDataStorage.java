package com.dw.cfcenter.client.core.storage.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dw.cfcenter.bean.vo.ClientDataSource;
import com.dw.cfcenter.client.manager.DefaultClientConfigManager;

/**
 * Description:
 * @author caohui
 */
public class FileDataStorage {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileDataStorage.class);
	public String fileName = null;
	
	public FileDataStorage() {
		fileName = System.getProperty("user.dir") + File.separator + "cfcenter_" + DefaultClientConfigManager.getInstance().getClientConfig().getProjectName();
	}
	
	/**
	 * Description: 写文件
	 * All Rights Reserved.
	 *
	 * @param dataSourceMap
	 * @return void
	 * @version 1.0 2016年11月10日 上午3:49:37 created by caohui(1343965426@qq.com)
	 */
	public void refreshData(Map<String, ClientDataSource> dataSourceMap) {
		if(dataSourceMap == null) {
			dataSourceMap = new ConcurrentHashMap<String, ClientDataSource> ();
		}
		
		synchronized(this) {
			File file = new File(fileName);
			FileOutputStream fos = null;
			ObjectOutputStream oos = null;
			try {
				if(!file.exists()) {
					file.createNewFile();
				}
				
				fos = new FileOutputStream(file);
				oos = new ObjectOutputStream(fos);
				oos.writeObject(dataSourceMap);
			}catch(IOException e) {
				LOGGER.error(e.getMessage(), e);
			}finally {
				try {
					if(oos != null) {
						oos.close();
					}
					
					if(fos != null) {
						fos.close();
					}
				}catch(Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
	}
	
	/**
	 * Description: 读文件
	 * All Rights Reserved.
	 *
	 * @return
	 * @return Map<String,ClientDataSource>
	 * @version 1.0 2016年11月10日 上午3:54:02 created by caohui(1343965426@qq.com)
	 */
	@SuppressWarnings("unchecked")
	public Map<String, ClientDataSource> readDataFromFile() {
		Map<String, ClientDataSource> dataSourceMap = null;
		
		File file = new File(fileName);
		ObjectInputStream ois = null;
		FileInputStream fis = null;
		
		try {
			fis = new FileInputStream(file);
			ois = new ObjectInputStream(fis);
			dataSourceMap = (Map<String, ClientDataSource>) ois.readObject();
		}catch(Exception e) {
			LOGGER.error(e.getMessage(), e);
		}finally {
			try {
				if(fis != null) {
					fis.close();
				}
				if(ois != null) {
					ois.close();
				}
			}catch(Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		return dataSourceMap;
	}
}
