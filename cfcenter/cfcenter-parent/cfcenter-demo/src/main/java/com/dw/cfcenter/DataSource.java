package com.dw.cfcenter;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dw.cfcenter.bean.vo.ClientDataSource;
import com.dw.cfcenter.client.api.impl.DefaultConfCenterApi;
import com.dw.cfcenter.util.DefaultHttpClient;
import com.dw.cfcenter.util.JsonUtils;

public class DataSource {

	public static void addDataSource() {
		String url = "http://127.0.0.1:8080/cfcenter-admin/client/addDataSource";
		Map<String, String> params = new HashMap<String, String> ();
		params.put("projectName", "common");
		params.put("sourceName", "what");
		params.put("sourceValue", "the fuck world");
		try {
			String result = DefaultHttpClient.doGet(url, params);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void getDataSourceByKey() {
		 ClientDataSource dataSource = DefaultConfCenterApi.newInstance().getDataSourceByKey("common");
		 System.out.println(JsonUtils.toJson(dataSource));
	}
	
	public static void main(String[] args) {
		//addDataSource();
		getDataSourceByKey();
	}
}

