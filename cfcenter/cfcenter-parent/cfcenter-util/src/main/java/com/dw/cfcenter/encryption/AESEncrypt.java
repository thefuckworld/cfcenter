package com.dw.cfcenter.encryption;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Description: 对称加密算法AES加密工具
 * @author caohui
 */
public final class AESEncrypt {

	public static final String ALGORITHM = "AES";
	// 秘钥，必须是已经使用BASE64的串
	private static Key key = null;
	private static Cipher cipher = null;
	private AESEncrypt() {}
	
	static {
		try {
			key = toKey(Encrypt.originalText.getBytes());
			cipher = Cipher.getInstance(ALGORITHM);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Description: 解密
	 * All Rights Reserved.
	 *
	 * @param data
	 * @return
	 * @return byte[]
	 * @version 1.0 2016年11月10日 上午3:31:20 created by caohui(1343965426@qq.com)
	 */
	public static byte[] decrypt(String data) {
		try {
			cipher.init(Cipher.DECRYPT_MODE, key);
			return cipher.doFinal(Encrypt.decryptBASE64(data));
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Description:
	 * All Rights Reserved.
	 *
	 * @param data
	 * @return
	 * @return String
	 * @version 1.0 2016年11月10日 上午3:33:54 created by caohui(1343965426@qq.com)
	 */
	public static String encrypt(byte[] data) {
		try {
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return Encrypt.encryptBASE64(cipher.doFinal(data));
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Description: 转换秘钥
	 * All Rights Reserved.
	 *
	 * @param key
	 * @return
	 * @return Key
	 * @version 1.0 2016年11月10日 上午3:35:37 created by caohui(1343965426@qq.com)
	 */
	private static Key toKey(byte[] key) {
		SecretKey secretKey = new SecretKeySpec(key, ALGORITHM);
		return secretKey;
	}
	
}
