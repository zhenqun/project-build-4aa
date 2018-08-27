/**
 * 
 */
package com.ido85.party.sso.security.encoder;

import java.security.MessageDigest;

import javax.inject.Named;

import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.codec.Utf8;



/**
 * @author rongxj
 *
 */
@Named("passwordEncoder")
public class SHAPasswordEncoder extends MessageDigestPasswordEncoder{
	
	private int iterations = 1;
	
	public SHAPasswordEncoder() {
		this(1);
	}

	public SHAPasswordEncoder(int strength) {
		super("SHA-" + strength);
	}

	@Override
	public String encodePassword(String rawPass, Object salt) {
//		String saltedPass = mergePasswordAndSalt(rawPass, salt, false);

		MessageDigest messageDigest = getMessageDigest();

		if (salt != null) {
			messageDigest.update(salt.toString().getBytes());
		}

		byte[] digest = messageDigest.digest(rawPass.getBytes());

		for (int i = 1; i < iterations; i++) {
//			digest.reset();
			digest = messageDigest.digest(digest);
		}

		if (getEncodeHashAsBase64()) {
			return Utf8.decode(Base64.encode(digest));
		}
		else {
			return new String(Hex.encode(digest));
		}
	}
	
	@Override
	public boolean isPasswordValid(String encPass, String rawPass, Object salt) {
		return super.isPasswordValid(encPass, rawPass, salt);
	}
	
	@Override
	public void setIterations(int iterations) {
		this.iterations = iterations;
		super.setIterations(iterations);
	}
}
