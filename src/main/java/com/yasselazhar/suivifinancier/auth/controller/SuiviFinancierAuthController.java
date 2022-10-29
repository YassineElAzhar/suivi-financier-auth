package com.yasselazhar.suivifinancier.auth.controller;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yasselazhar.suivifinancier.auth.constant.TokenContext;
import com.yasselazhar.suivifinancier.auth.model.User;
import com.yasselazhar.suivifinancier.auth.repository.UserRepository;
import com.yasselazhar.suivifinancier.auth.service.TokenService;

@RestController
@RequestMapping("/suivi-financier-auth")
public class SuiviFinancierAuthController {

    @Autowired
    UserRepository userRepository;
    
    @Autowired
    TokenService tokenService;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    @GetMapping("/encryptToken")
    public String encryptToken() {
    	return tokenService.encryptToken("yassine.elazhar@gmail.com", TokenContext.PASSWORD_INIT.toString());
    }
    
    @GetMapping("/decryptToken/{token}")
    public Map<String, String> decryptToken(@PathVariable(value = "token") String token) {
    	return tokenService.decryptToken(token);
    }

    
    
}
