/**
 * 
 */
package com.ido85.party.platform.crypt;

import org.apache.commons.codec.digest.Crypt;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * SHA512加密，适用于LDAP crypt类型
 * @author rongxj
 *
 */
public class SHA512Crypt {

	/**
	 * 适用于LDAP crypt加密,使用随机salt<br/>
	 * @param key
	 * @return 加密后的key
	 * <p>
	 * sha512Crypt("123456")生成
	 * {crypt}$6$4UKjn0F3$u./tplegsw6Gf6e7FBIxQTmX1vosahq/qwkIAaM8BshuCfL.rjTJpDReam2M2e8jQJzVsIwsid/EcDF5wfS3U0
	 * </p>
	 */
	public static String sha512Crypt(String key){
		return String.format("{crypt}%s",Crypt.crypt(key.getBytes(), String.format("$6$%s", RandomStringUtils.randomAlphanumeric(8))));
	}
	
	/**
	 * 适用于LDAP crypt加密,<br/>
	 * @param key
	 * @param salt
	 * @return 加密后的key
	 * <p>
	 * sha512Crypt("123456", 4UKjn0F3)生成
	 * {crypt}$6$4UKjn0F3$u./tplegsw6Gf6e7FBIxQTmX1vosahq/qwkIAaM8BshuCfL.rjTJpDReam2M2e8jQJzVsIwsid/EcDF5wfS3U0
	 * </p>
	 */
	public static String sha512Crypt(String key, String salt){
		return String.format("{crypt}%s",Crypt.crypt(key, String.format("$6$%s", salt)));
	}
	
	/**
	 * 明码和密码比对
	 * @param plainKey
	 * @param cryptKey
	 * @return 
	 * 	true：密码匹配
	 * 	false：密码不匹配
	 */
	public static boolean matches(String plainKey, String cryptKey){
		if(cryptKey.startsWith("{crypt}")){
			cryptKey = cryptKey.replaceFirst("{crypt}", "");
		}
		return cryptKey.equals(Crypt.crypt(plainKey, cryptKey));
	}
}
