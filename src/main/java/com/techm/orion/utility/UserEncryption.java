package com.techm.orion.utility;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class UserEncryption {
	private static final Logger logger = LogManager.getLogger(UserEncryption.class);

	private static SecretKeySpec secretKey;
	private static byte[] key;

	public static void setKey(String myKey) {
		MessageDigest sha = null;
		try {
			key = myKey.getBytes("UTF-8");
			sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);
			secretKey = new SecretKeySpec(key, "AES");
		} catch (NoSuchAlgorithmException e) {
			logger.error("Exception occured in setKey method in UserEncryption - NoSuchAlgorithmException " + e.getMessage());
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			logger.error("Exception occured in setKey method in UserEncryption - UnsupportedEncodingException " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static String encrypt(String passwordToEncrypt, String secret) {
		try {
			setKey(secret);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			return Base64.getEncoder().encodeToString(cipher.doFinal(passwordToEncrypt.getBytes("UTF-8")));
		} catch (Exception e) {
			logger.error("Error while encrypting in UserEncryption : " + e.toString());
		}
		return null;
	}

	public static String decrypt(String passwordToDecrypt, String secret) {
		try {
			setKey(secret);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			return new String(cipher.doFinal(Base64.getDecoder().decode(passwordToDecrypt)));
		} catch (Exception e) {
			logger.error("Error while decrypting in UserEncryption : " + e.toString());
		}
		return null;
	}
}