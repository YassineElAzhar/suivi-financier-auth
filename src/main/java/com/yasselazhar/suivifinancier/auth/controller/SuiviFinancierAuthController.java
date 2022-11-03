package com.yasselazhar.suivifinancier.auth.controller;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yasselazhar.suivifinancier.auth.handler.SuiviFinancierAuthHandler;

@RestController
@RequestMapping("/suivi-financier-auth")
public class SuiviFinancierAuthController {

	
    @Autowired
    SuiviFinancierAuthHandler suiviFinancierAuthHandler;
   
    //REST conventions
    	//GET : to get a data
		//POST : to create something or for login
		//PUT : to update something
		//DELETE : to remove something
    
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
    	String token = suiviFinancierAuthHandler.createProfil(userDetails);
    	return token;
    }
    
    @RequestMapping(value="/activateProfil",method = RequestMethod.POST) 
    public String activateProfil(@RequestParam(value="token") String token, @RequestParam(value="password") String password) {
    	return suiviFinancierAuthHandler.activateProfil(token, password);
    }
    
    @RequestMapping(value="/login",method = RequestMethod.POST)
    public String login(@RequestParam(value = "emailUser")  String emailUser,@RequestParam(value = "password") String password) {
    	return suiviFinancierAuthHandler.login(emailUser, password);
    }

    
    
}
