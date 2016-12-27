package com.dw.cfcenter.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description: http工具类
 * @author caohui
 */
public final class DefaultHttpClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultHttpClient.class);
	public static final String DEFAULT_CHARSET = "UTF-8";
	private static final String METHOD_POST = "POST";
	private static final String METHOD_GET = "GET";
	private static final int MAX_SIZE = 1024;
	private static final int DEFAULT_TIMEOUT = 5000;
	private static final int DEFAULT_CONNECT_TIMEOUT = 5000;
	public static AtomicInteger failCount = new AtomicInteger(0);
	
	private DefaultHttpClient() {}
	
	public static String doPost(String url, Map<String, String> params) throws Exception {
		return doPost(url, params, null, DEFAULT_CHARSET, 0, 0);
	}
	
	/**
	 * Description: 执行HTTP POST请求
	 * All Rights Reserved.
	 *
	 * @param url                  请求地址
	 * @param params               请求参数
	 * @param type                 编码类型
	 * @param charset              字符集，如UTF-8、GBK、GB2312
	 * @param connectTimeout       客户端连接超时时间
	 * @param readTimeout          服务端响应超时时间
	 * @return
	 * @return String
	 * @version 1.0 2016年11月10日 上午2:01:02 created by caohui(1343965426@qq.com)
	 */
	public static String doPost(String url, Map<String, String> params, String type, String charset, int connectTimeout, int readTimeout) throws Exception{
		String ctype = null;
		if(type == null) {
			ctype = "application/x-www-form-ulrencoded;charset" + charset;
		}else {
			ctype = type + ";charset=" + charset;
		}
		byte[] content = {};
		try {
			String query = buildQuery(params, charset);
			if(query != null) {
				content = query.getBytes(charset);
			}
		}catch(IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return doPost(url, ctype, content, connectTimeout, readTimeout);
	}
	
	public static String doPost(String url, String ctype, byte[] content, int connectTimeout, int readTimeout) throws Exception {
		HttpURLConnection conn = null;
		OutputStream out = null;
		String rsp = null;
		try {
			conn = getConnection(new URL(url), METHOD_POST, ctype, readTimeout, connectTimeout);
			conn.setConnectTimeout(connectTimeout);
			conn.setReadTimeout(readTimeout);
			out = conn.getOutputStream();
			out.write(content);
			rsp = getResponseAsString(conn);
		}catch(IOException e) {
			Map<String, String> map = getParamsFromUrl(url);
			LOGGER.error("{}访问出现错误", map.get("serviceId"));
		}finally {
			if(out != null) {
				try {
					out.close();
				}catch(IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
			
			if(conn != null) {
				conn.disconnect();
			}
		}
		return rsp;
	}
	
	public static String doGet(String url, Map<String, String> params) throws Exception {
		return doGet(url, params, 0, 0);
	}
	/**
	 * Description: 执行HTTP GET请求
	 * All Rights Reserved.
	 *
	 * @param url                请求地址
	 * @param params             请求参数
	 * @param readTimeout        服务端响应超时时间
	 * @param connectTimeout     客户端连接超时时间
	 * @return
	 * @return String
	 * @version 1.0 2016年11月16日 上午2:03:49 created by caohui(1343965426@qq.com)
	 */
	public static String doGet(String url, Map<String, String> params, int readTimeout, int connectTimeout) throws Exception{
		if(readTimeout <= 0) {
			readTimeout = DEFAULT_TIMEOUT;
		}
		if(connectTimeout <= 0) {
			connectTimeout = DEFAULT_CONNECT_TIMEOUT;
		}
		return doGet(url, params, DEFAULT_CHARSET, readTimeout, connectTimeout);
	}

	public static String doGet(String url, Map<String, String> params, String charset, int readTimeout, int connectTimeout) throws Exception{
		HttpURLConnection conn = null;
		String rsp = null;
		try {
			String ctype = "application/x-www-form-urlencoded;charset=" + charset;
			String query = buildQuery(params, charset);
			conn = getConnection(buildGetUrl(url, query), METHOD_GET, ctype, readTimeout, connectTimeout);
			rsp = getResponseAsString(conn);
			if(failCount.get() != 0) {
				failCount.set(0);
			}
		}catch(IOException e) {
			failCount.addAndGet(1);
			LOGGER.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}finally {
			if(conn != null) {
				conn.disconnect();
			}
		}
		return rsp;
	}
	
	/**
	 * Description: 获取HTTP连接
	 * All Rights Reserved.
	 *
	 * @param url                 请求地址
	 * @param method              请求方法
	 * @param ctype               请求类型
	 * @param readTimeout
	 * @param connectTimeout
	 * @return
	 * @throws Exception
	 * @return HttpURLConnection
	 * @version 1.0 2016年11月16日 上午2:05:42 created by caohui(1343965426@qq.com)
	 */
	public static HttpURLConnection getConnection(URL url, String method, String ctype, int readTimeout, int connectTimeout) throws Exception {
		
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod(method);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestProperty("Accept", "text/xml,text/javascript,text/html,text/plain");
		conn.setRequestProperty("User-Agent", "remote-dw-java");
		conn.setRequestProperty("Content-Type", ctype);
		conn.setRequestProperty("Accept-Language", "zh-CN");
		conn.setReadTimeout(readTimeout);
		conn.setConnectTimeout(connectTimeout);
		return conn;
	}
	
	/**
	 * Description: 构建HTTP请求地址
	 * All Rights Reserved.
	 *
	 * @param strUrl             请求地址
	 * @param query              请求参数串
	 * @return
	 * @throws Exception
	 * @return URL
	 * @version 1.0 2016年11月10日 上午2:12:44 created by caohui(1343965426@qq.com)
	 */
	private static URL buildGetUrl(String strUrl, String query) throws Exception {
		URL url = new URL(strUrl);
		if(StringUtils.isEmpty(query)) {
			return url;
		}
		
		StringBuilder builder = new StringBuilder(strUrl);
		if(StringUtils.isEmpty(url.getQuery())) {
			if(strUrl.endsWith("?")) {
				builder.append(query);
			}else {
				builder.append("?").append(query);
			}
		}else {
			if(strUrl.endsWith("&")) {
				builder.append(query);
			}else {
				builder.append("&").append(query);
			}
		}
		return new URL(builder.toString());
	}
	
	/**
	 * Description:
	 * All Rights Reserved.
	 *
	 * @param params
	 * @param charset
	 * @return
	 * @throws Exception
	 * @return String
	 * @version 1.0 2016年11月10日 上午2:18:42 created by caohui(1343965426@qq.com)
	 */
	public static String buildQuery(Map<String, String> params, String charset) throws Exception {
		if(params == null || params.isEmpty()) {
			return null;
		}
		
		StringBuilder query = new StringBuilder();
		Set<Entry<String, String>> entries = params.entrySet();
		boolean hasParam = false;
		for(Entry<String, String> entry : entries) {
			String name = entry.getKey();
			String value = entry.getValue();
			// 忽略参数名或参数值为空的参数
			if(StringUtils.isNotBlank(name)) {
				if(hasParam) {
					query.append("&");
				}else {
					hasParam = true;
				}
				
				query.append(name).append("=").append(URLEncoder.encode(value, charset));
			}
		}
		return query.toString();
	}
	
	/**
	 * Description:
	 * All Rights Reserved.
	 *
	 * @param conn
	 * @return
	 * @return String
	 * @version 1.0 2016年11月10日 上午2:20:51 created by caohui(1343965426@qq.com)
	 */
	protected static String getResponseAsString(HttpURLConnection conn) throws Exception{
		String charset = getResponseCharset();
		InputStream is = conn.getErrorStream();
		String msg = null;
		if(is == null) {
			try {
				return getStreamAsString(conn.getInputStream(), charset);
			}catch(Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		
		msg = getStreamAsString(is, charset);
		throw new Exception("服务端响应异常:"+msg+", url is" + conn.getURL());
	}
	
	private static String getStreamAsString(InputStream stream, String charset) throws Exception{
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream, charset));
			StringWriter writer = new StringWriter();
			char[] chars = new char[MAX_SIZE];
			int count = 0;
			while((count = reader.read(chars)) > 0) {
				writer.write(chars, 0, count);
			}
			return writer.toString();
		}catch(Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}finally {
			if(stream != null) {
				try {
					stream.close();
				}catch(IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
	}
	
	private static String getResponseCharset() {
		return DEFAULT_CHARSET;
	}
	
	public static String decode(String value) {
		return decode(value, DEFAULT_CHARSET);
	}
	
	public static String encode(String value) {
		return encode(value, DEFAULT_CHARSET);
	}
	
	public static String decode(String value, String charset) {
		String result = null;
		if(!StringUtils.isEmpty(value)) {
			try {
				result = URLDecoder.decode(value, charset);
			}catch(IOException e) {
				throw new RuntimeException(e);
			}
		}
		return result;
	}
	
	public static String encode(String value, String charset) {
		String result = null;
		if(!StringUtils.isEmpty(value)) {
			try {
				result = URLEncoder.encode(value, charset);
			}catch(IOException e) {
				throw new RuntimeException(e);
			}
		}
		return result;
	}
	
	private static Map<String, String> getParamsFromUrl(String url) {
		Map<String, String> map = null;
		if(url != null && url.indexOf('?') != -1) {
			map = splitUrlQuery(url.substring(url.indexOf('?') + 1));
		}
		
		if(map == null) {
			map = new HashMap<String, String> ();
		}
		return map;
	}
	
	public static Map<String, String> splitUrlQuery(String query) {
		Map<String, String> result = new HashMap<String, String> ();
		String[] pairs = query.split("&");
		if(pairs != null && pairs.length > 0) {
			for(String pair : pairs) {
				String[] param = pair.split("=", 2);
				if(param != null && param.length == 2) {
					result.put(param[0].trim(), param[1].trim());
				}
			}
		}
		return result;
	}
}
