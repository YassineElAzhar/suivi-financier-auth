package com.yasselazhar.suivifinancier.auth.service;


public interface PasswordService {
	   public abstract String hashPassword(String password);
	   
	   public abstract boolean verifyPassword(String plainTextPassword, String hashPasswordStored);
	   
	   public abstract String hashPasswordPBKDF2(String password);
	   
	   public abstract String hashPasswordBCrypt(String password);
	   
	   public abstract String hashPasswordSCrypt(String password);
	   
	   public abstract String hashPasswordArgon2Password(String password);
	   
	   public abstract boolean verifyPasswordBCrypt(String plainTextPassword, String hashPasswordStored);
	   
	   public abstract boolean verifyPasswordSCrypt(String plainTextPassword, String hashPasswordStored);
	   
	   public abstract boolean verifyPasswordArgon2Password(String plainTextPassword, String hashPasswordStored);
	   
	   public abstract boolean verifyPasswordPBKDF2(String plainTextPassword, String hashPasswordStored);
}