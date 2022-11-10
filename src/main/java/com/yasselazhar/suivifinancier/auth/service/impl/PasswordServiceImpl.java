package com.yasselazhar.suivifinancier.auth.service.impl;



import java.security.SecureRandom;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.yasselazhar.suivifinancier.auth.service.PasswordService;

@Service
public class PasswordServiceImpl implements PasswordService {

	   public String hashPassword(String password) {
		   String result = hashPasswordBCrypt(password);
		   return result;
	   }
	   
	   
	   public boolean verifyPassword(String plainTextPassword, String hashPasswordStored) {
		   return verifyPasswordBCrypt(plainTextPassword, hashPasswordStored);
	   }
	   

	   


	   /* *
	    * 
	    * Hashing part
	    * 
	    * */
	   

	   public String hashPasswordPBKDF2(String password) {
		   String result = "";
		   
		   
		   String pepper = "suivi-financier-01"; // secret key used by password encoding
		   int iterations = 200000;  // number of hash iteration
		   int hashWidth = 256;      // hash width in bits

		   Pbkdf2PasswordEncoder pbkdf2PasswordEncoder = new Pbkdf2PasswordEncoder(pepper, iterations, hashWidth);
		   pbkdf2PasswordEncoder.setEncodeHashAsBase64(true);
		   result = pbkdf2PasswordEncoder.encode(password);
		   
		   return result;
	   }
	   


	   public String hashPasswordBCrypt(String password) {
		   String result = "";
		   
		   int strength = 10; // work factor of bcrypt
		   BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(strength, new SecureRandom());
		   result = bCryptPasswordEncoder.encode(password);
		   
		   
		   return result;
	   }


	   public String hashPasswordSCrypt(String password) {
		   String result = "";
		   
		   
		   int cpuCost = (int) Math.pow(2, 14); // factor to increase CPU costs
		   int memoryCost = 8;      // increases memory usage
		   int parallelization = 1; // currently not supported by Spring Security
		   int keyLength = 32;      // key length in bytes
		   int saltLength = 64;     // salt length in bytes

		   SCryptPasswordEncoder sCryptPasswordEncoder = new SCryptPasswordEncoder(
		     cpuCost, 
		     memoryCost,
		     parallelization,
		     keyLength,
		     saltLength);
		   result = sCryptPasswordEncoder.encode(password);
		   
		   
		   return result;
	   }

	   public String hashPasswordArgon2Password(String password) {
		   String result = "";
		   
		   
		   int saltLength = 16; // salt length in bytes
		   int hashLength = 32; // hash length in bytes
		   int parallelism = 1; // currently not supported by Spring Security
		   int memory = 4096;   // memory costs
		   int iterations = 3;

		   Argon2PasswordEncoder argon2PasswordEncoder = new Argon2PasswordEncoder(
		     saltLength,
		     hashLength,
		     parallelism,
		     memory,
		     iterations);
		   result = argon2PasswordEncoder.encode(password);
		   
		   
		   return result;
	   }

	   /* *
	    * 
	    * Verification part
	    * 
	    * */
	   
	   public boolean verifyPasswordPBKDF2(String plainTextPassword, String hashPasswordStored) {
		   String pepper = "suivi-financier-01"; // secret key used by password encoding
		   int iterations = 200000;  // number of hash iteration
		   int hashWidth = 256;      // hash width in bits

		   Pbkdf2PasswordEncoder pbkdf2PasswordEncoder = new Pbkdf2PasswordEncoder(pepper, iterations, hashWidth);
		   pbkdf2PasswordEncoder.setEncodeHashAsBase64(true);
		   
		   boolean matches = pbkdf2PasswordEncoder.matches(plainTextPassword, hashPasswordStored);
		   
		   return matches;
	   }

	   public boolean verifyPasswordBCrypt(String plainTextPassword, String hashPasswordStored) {
		   int strength = 10; // work factor of bcrypt
		   BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(strength, new SecureRandom());
		   boolean matches = bCryptPasswordEncoder.matches(plainTextPassword, hashPasswordStored);
		   //System.out.println(String.valueOf(matches));
		   return matches;
	   }

	   public boolean verifyPasswordSCrypt(String plainTextPassword, String hashPasswordStored) {
		   int cpuCost = (int) Math.pow(2, 14); // factor to increase CPU costs
		   int memoryCost = 8;      // increases memory usage
		   int parallelization = 1; // currently not supported by Spring Security
		   int keyLength = 32;      // key length in bytes
		   int saltLength = 64;     // salt length in bytes

		   SCryptPasswordEncoder sCryptPasswordEncoder = new SCryptPasswordEncoder(
		     cpuCost, 
		     memoryCost,
		     parallelization,
		     keyLength,
		     saltLength);
		   
		   boolean matches = sCryptPasswordEncoder.matches(plainTextPassword, hashPasswordStored);
		   
		   return matches;
	   }

	   public boolean verifyPasswordArgon2Password(String plainTextPassword, String hashPasswordStored) {
		   int saltLength = 16; // salt length in bytes
		   int hashLength = 32; // hash length in bytes
		   int parallelism = 1; // currently not supported by Spring Security
		   int memory = 4096;   // memory costs
		   int iterations = 3;

		   Argon2PasswordEncoder argon2PasswordEncoder = new Argon2PasswordEncoder(
		     saltLength,
		     hashLength,
		     parallelism,
		     memory,
		     iterations);
		   
		   boolean matches = argon2PasswordEncoder.matches(plainTextPassword, hashPasswordStored);
		   
		   return matches;
	   }
}