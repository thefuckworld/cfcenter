package com.dw.cfcenter.common.result.processor;

import com.dw.cfcenter.common.util.json.JsonUtils;

public final class ResultProcessUtils {
	
	private static ResultProcessUtils instance = new ResultProcessUtils();
	private ResultProcessUtils() {}
	public static ResultProcessUtils newInstance() {
		return instance;
	}
	
	public  String serializeResult(Object o) {
		return JsonUtils.toJson(o);
	}
}
