package com.dw.cfcenter.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HttpClientForJson {

	public static final Logger LOGGER = LoggerFactory.getLogger(HttpClientForJson.class);
	
	public static HttpClient httpClient;
	
	static {
		httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager());
	}
	
	public static void getHttpResponseForJson(String url, String value) {
		HttpPost post = null;
		HttpResponse response = null;
		
		try {
			post = new HttpPost(url);
			StringEntity se = new StringEntity(value, "UTF-8");
			se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			se.setContentType("application/json;charset=utf-8");
			post.setHeader("Accept", "application/json");
			post.setHeader("Content-Type", "application/json");
			post.setEntity(se);
			response = httpClient.execute(post);
		}catch(Exception e) {
			LOGGER.error(e.getMessage(), e);
		}finally {
			if(response != null) {
				try {
					EntityUtils.consume(response.getEntity());
				}catch(Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
			
			if(post != null) {
				post.releaseConnection();
			}
		}
	}
	
	public static String getHttpResponse(String url, String value) throws Exception {
		HttpPost post = null;
		HttpResponse response = null;
		InputStream instream = null;
		BufferedReader reader = null;
		StringWriter writer = null;
		try {
			post = new HttpPost(url);
			StringEntity se = new StringEntity(value, "UTF-8");
			se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			se.setContentType("application/json;charset=utf-8");
			post.setHeader("Accept", "application/json");
			post.setHeader("Content-Type", "application/json");
			post.setEntity(se);
			response = httpClient.execute(post);
			HttpEntity entity = response.getEntity();
			instream = entity.getContent();
			reader = new BufferedReader(new InputStreamReader(instream, "UTF-8"));
			writer = new StringWriter();
			char[] chars = new char[1024];
			int count = 0;
			while((count = reader.read(chars)) > 0) {
				writer.write(chars, 0, count);
			}
			return writer.toString();
		}catch(Exception e) {
			throw new Exception(e.getMessage());
		}finally {
			if(response != null) {
				try {
					EntityUtils.consume(response.getEntity());
				}catch(Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
			
			if(instream != null) {
				instream.close();
			}
			
			if(reader != null) {
				reader.close();
			}
			
			if(writer != null) {
				writer.close();
			}
			
			if(post != null) {
				post.releaseConnection();
			}
		}
	}
}
