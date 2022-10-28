package com.yasselazha.suivifinancier.auth.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.springframework.stereotype.Service;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.yasselazha.suivifinancier.auth.constant.TokenContext;
import com.yasselazha.suivifinancier.auth.service.TokenService;


@Service
public class TokenServiceImpl implements TokenService {
	

	@Override
	public String encryptToken(String email, String context) {
		
		String eventDate = String.valueOf(new Date().getTime());
		
		
		
		Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, 5); //minus number would decrement the days
        String expiryDate = String.valueOf(cal.getTimeInMillis());
        
        
		String serializedEmail = "";
		String serializedEventDate = "";
		String serializedExpiryDate = "";
		String serializedContext = "";
		
		String encryptedToken = "";

		Map<String, String> map = new HashMap<String, String>();
		
		try {
			serializedEmail = serialize(email);
			serializedEventDate = serialize(eventDate);
			serializedExpiryDate = serialize(expiryDate);
			serializedContext = serialize(context);
			
			map.put("email", serializedEmail);
			map.put("context", serializedContext);
			map.put("creationDate", serializedEventDate);
			map.put("expiryDate", serializedExpiryDate);

			encryptedToken = Base64.getEncoder().encodeToString(map.toString().getBytes("utf-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return encryptedToken;
	}

	@Override
	public Map<String, String> decryptToken(String token) {

		String tokenUTF8;
		Map<String, String> mapTokenDecrypted = new HashMap<String, String>();
		try {
			tokenUTF8 = new String(Base64.getDecoder().decode(token.getBytes("utf-8")));
			
			tokenUTF8 = tokenUTF8.replace("{", "");
			tokenUTF8 = tokenUTF8.replace("}", "");
			tokenUTF8 = tokenUTF8.replace("context=", "context::::");
			tokenUTF8 = tokenUTF8.replace("creationDate=", "creationDate::::");
			tokenUTF8 = tokenUTF8.replace("email=", "email::::");
			tokenUTF8 = tokenUTF8.replace("expiryDate=", "expiryDate::::");
			tokenUTF8 = tokenUTF8.replace("\r", "");
			tokenUTF8 = tokenUTF8.replace("\n", "");
			tokenUTF8 = tokenUTF8.replace("\r\\n", "");
			tokenUTF8 = tokenUTF8.replace("\n\r", "");
			tokenUTF8 = tokenUTF8.replace(", ", ",");
			
	        String[] tokenSplit = tokenUTF8.split(",");
	        String[] val0Split = tokenSplit[0].split("::::");
	        String[] val1Split = tokenSplit[1].split("::::");
	        String[] val2Split = tokenSplit[2].split("::::");
	        String[] val3Split = tokenSplit[3].split("::::");
	        

	        String val0ClearText = deserialize(val0Split[1], new TypeToken<String>() {private static final long serialVersionUID = 1L;}.getType());
	        String val1ClearText = deserialize(val1Split[1], new TypeToken<String>() {private static final long serialVersionUID = 2L;}.getType());
	        String val2ClearText = deserialize(val2Split[1], new TypeToken<String>() {private static final long serialVersionUID = 3L;}.getType());
	        String val3ClearText = deserialize(val3Split[1], new TypeToken<String>() {private static final long serialVersionUID = 4L;}.getType());

	        mapTokenDecrypted.put(val0Split[0], val0ClearText);
	        mapTokenDecrypted.put(val1Split[0], val1ClearText);
	        mapTokenDecrypted.put(val2Split[0], val2ClearText);
	        mapTokenDecrypted.put(val3Split[0], val3ClearText);

		} catch (Exception e) {
			e.printStackTrace();
	        mapTokenDecrypted.put("context", "error");
	        mapTokenDecrypted.put("creationDate", "1900-01-01");
	        mapTokenDecrypted.put("expiryDate", "1900-01-01");
	        mapTokenDecrypted.put("email", "error");
		}
		return mapTokenDecrypted;
	}
	
	public static SecretKey generateKey(int n) throws NoSuchAlgorithmException {
	    KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
	    keyGenerator.init(n);
	    SecretKey key = keyGenerator.generateKey();
	    return key;
	}
	
	public static SecretKey getKeyFromPassword(String password, String salt)
		    throws NoSuchAlgorithmException, InvalidKeySpecException {
		    
	    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
	    KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
	    SecretKey secret = new SecretKeySpec(factory.generateSecret(spec)
	        .getEncoded(), "AES");
	    return secret;
	}
	
	public static IvParameterSpec generateIv() {
	    byte[] iv = new byte[16];
	    new SecureRandom().nextBytes(iv);
	    return new IvParameterSpec(iv);
	}
	
	public static String encrypt(String algorithm, String input, SecretKey key,
		    IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException,
		    InvalidAlgorithmParameterException, InvalidKeyException,
		    BadPaddingException, IllegalBlockSizeException {
		    
		    Cipher cipher = Cipher.getInstance(algorithm);
		    cipher.init(Cipher.ENCRYPT_MODE, key, iv);
		    byte[] cipherText = cipher.doFinal(input.getBytes());
		    return Base64.getEncoder()
		        .encodeToString(cipherText);
	}
	
	public static String decrypt(String algorithm, String cipherText, SecretKey key,
		    IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException,
		    InvalidAlgorithmParameterException, InvalidKeyException,
		    BadPaddingException, IllegalBlockSizeException {
	    
	    Cipher cipher = Cipher.getInstance(algorithm);
	    cipher.init(Cipher.DECRYPT_MODE, key, iv);
	    byte[] plainText = cipher.doFinal(Base64.getDecoder()
	        .decode(cipherText));
	    return new String(plainText);
	}
	
	//Pour passer une map string string en base64
	public static String serialize(Object object) throws IOException {
	    ByteArrayOutputStream byteaOut = new ByteArrayOutputStream();
	    GZIPOutputStream gzipOut = null;
	    try {
	        gzipOut = new GZIPOutputStream(new Base64OutputStream(byteaOut));
	        gzipOut.write(new Gson().toJson(object).getBytes("UTF-8"));
	    } finally {
	        if (gzipOut != null) try { gzipOut.close(); } catch (IOException logOrIgnore) {}
	    }
	    return new String(byteaOut.toByteArray());
	}

	public static <T> T deserialize(String string, Type type) throws IOException {
	    ByteArrayOutputStream byteaOut = new ByteArrayOutputStream();
	    GZIPInputStream gzipIn = null;
	    try {
	        gzipIn = new GZIPInputStream(new Base64InputStream(new ByteArrayInputStream(string.getBytes("UTF-8"))));
	        for (int data; (data = gzipIn.read()) > -1;) {
	            byteaOut.write(data);
	        }
	    } finally {
	        if (gzipIn != null) try { gzipIn.close(); } catch (IOException logOrIgnore) {}
	    }
	    return new Gson().fromJson(new String(byteaOut.toByteArray()), type);
	}
	
	public void testMapStringStringB64() throws IOException{
		Map<String, String> map = new HashMap<String, String>();
		map.put("email", "yassine.elazhar@gmail.com");
		map.put("context", TokenContext.PASSWORD_INIT.toString());
		map.put("creationDate", "2022-10-26");
		
		String serialized = serialize(map);
		Map<String, String> deserialized = deserialize(serialized, new TypeToken<Map<String, String>>() {
		
		private static final long serialVersionUID = 1L;}.getType());

		System.out.println("serialized = "+ serialized + " ----end");
		System.out.println("deserialized = "+ deserialized);
	}
	
	public void givenString_whenEncrypt_thenSuccess()
		    throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException,
		    BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException, IOException {
		

	    String input = "GhroGpkB5VJzEzNzgIKVicXFmXmpeqk5iVUZiUUO6SBxveT8XKVaALgNgnBbAAAA";//"baeldung";
	    SecretKey key = generateKey(128);
	    IvParameterSpec ivParameterSpec = generateIv();
	    String algorithm = "AES/CBC/PKCS5Padding";
	    String cipherText = encrypt(algorithm, input, key, ivParameterSpec);
	    String plainText = decrypt(algorithm, cipherText, key, ivParameterSpec);
	    

	    System.out.println("input = "+ input);
	    System.out.println("key = "+ key.toString());
	    System.out.println("ivParameterSpec = "+ ivParameterSpec);
	    System.out.println("cipherText = "+ cipherText);
	    System.out.println("plainText = "+ plainText);
    }
	

}
