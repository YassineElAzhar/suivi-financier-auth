package com.yasselazha.suivifinancier.auth.service;

import java.util.Map;

public interface TokenService {
	   public abstract String encryptToken(String email, String context);
	   public abstract Map<String, String> decryptToken(String token);
}