package com.yasselazhar.suivifinancier.auth.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yasselazhar.suivifinancier.auth.constant.TokenContext;
import com.yasselazhar.suivifinancier.auth.handler.SuiviFinancierAuthHandler;
import com.yasselazhar.suivifinancier.auth.model.User;
import com.yasselazhar.suivifinancier.auth.repository.UserRepository;
import com.yasselazhar.suivifinancier.auth.service.TokenService;

@RestController
@RequestMapping("/suivi-financier-auth")
public class SuiviFinancierAuthController {

	
    @Autowired
    SuiviFinancierAuthHandler suiviFinancierAuthHandler;
    

    @Autowired
    UserRepository userRepository;
    
    @Autowired
    TokenService tokenService;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    @RequestMapping(value="/createUser",method = RequestMethod.POST) 
    public String createUser( 
    		@RequestParam(value="email") String email, 
    		@RequestParam(value="nom") String nom, 
    		@RequestParam(value="prenom") String prenom, 
    		@RequestParam(value="dateNaissance") String dateNaissance, 
    		@RequestParam(value="adresse") String adresse, 
    		@RequestParam(value="ville") String ville, 
    		@RequestParam(value="zip") String zip, 
    		@RequestParam(value="typeProfile") String typeProfile ) {
    	
    	Map<String,String> userDetails = new HashMap<String, String>();
    	userDetails.put("email", email);
    	userDetails.put("nom", nom);
    	userDetails.put("prenom", prenom);
    	userDetails.put("dateNaissance", dateNaissance);
    	userDetails.put("adresse", adresse);
    	userDetails.put("ville", ville);
    	userDetails.put("zip", zip);
    	userDetails.put("typeProfile", typeProfile);
    	suiviFinancierAuthHandler.createProfil(userDetails);
    	return "hohoho";
    }
    
    @GetMapping("/encryptToken")
    public String encryptToken() {
    	return tokenService.encryptToken("1","yassine.elazhar@gmail.com", TokenContext.PASSWORD_INIT.toString());
    }
    
        
    @GetMapping("/decryptToken/{token}")
    public Map<String, String> decryptToken(@PathVariable(value = "token") String token) {
    	return tokenService.decryptToken(token);
    }

    
    
}
