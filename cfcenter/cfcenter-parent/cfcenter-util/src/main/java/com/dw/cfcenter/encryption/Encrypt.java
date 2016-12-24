package com.dw.cfcenter.encryption;

import org.apache.commons.codec.binary.Base64;

/**
 * Description:
 * @author caohui
 */
public final class Encrypt {

	// 秘钥明文
	public static final String originalText = "0123456789012345";
	
	/**
	 * Description: BASE64加密
	 * All Rights Reserved.
	 *
	 * @param data
	 * @return
	 * @return String
	 * @version 1.0 2016年11月10日 上午3:24:15 created by caohui(1343965426@qq.com)
	 */
	public static String encryptBASE64(byte[] data) {
		return Base64.encodeBase64String(data);
	}
	
	/**
	 * Description: Base64解密
	 * All Rights Reserved.
	 *
	 * @param data
	 * @return
	 * @return byte[]
	 * @version 1.0 2016年11月10日 上午3:26:46 created by caohui(1343965426@qq.com)
	 */
	public static byte[] decryptBASE64(String data) {
		try {
			return Base64.decodeBase64(data.getBytes("utf-8"));
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
