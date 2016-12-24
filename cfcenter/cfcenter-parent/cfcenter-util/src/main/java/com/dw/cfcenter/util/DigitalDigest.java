package com.dw.cfcenter.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DigitalDigest {

	private static MessageDigest md5 = null;
	private static final Logger LOGGER = LoggerFactory.getLogger(DigitalDigest.class);
	
	static {
		try {
			md5 = MessageDigest.getInstance("MD5");
		}catch(NoSuchAlgorithmException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Description: md5加密 使用消息摘要MessageDigest处理
	 * All Rights Reserved.
	 *
	 * @param str
	 * @return
	 * @throws Exception
	 * @return String
	 * @version 1.0 2016年12月23日 上午11:34:26 created by caohui(1343965426@qq.com)
	 */
	public static String encodeByMd5(String str) throws Exception {
		if(StringUtils.isBlank(str)) {
			// 如果为空，那么使用空串代替
			str = "";
		}
		// 先更新摘要
		md5.update(str.getBytes());
		// 再通过执行注入填充之类的最终操作完成哈希计算。在调用此方法之后，摘要被重置。
		byte[] digest = md5.digest();
		return toHex(digest);
	}
	
	/**
	 * Description: md5摘要转16进制
	 * All Rights Reserved.
	 *
	 * @param digest
	 * @return
	 * @return String
	 * @version 1.0 2016年12月23日 上午11:37:19 created by caohui(1343965426@qq.com)
	 */
	private static String toHex(byte[] digest) {
		if(digest == null || digest.length <= 0) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		int len = digest.length;
		for(int i=0; i<len; i++) {
			String out = Integer.toHexString(0xFF & digest[i]);
			if(out.length() ==  1) {
				// 如果为1位，前面补个0
				sb.append("0");
			}
			sb.append(out);
		}
		
		return sb.toString();
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(encodeByMd5(""));
	}
}
