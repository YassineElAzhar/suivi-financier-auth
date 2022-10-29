package com.yasselazhar.suivifinancier.auth.handler;

import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import com.yasselazhar.suivifinancier.auth.constant.TokenContext;
import com.yasselazhar.suivifinancier.auth.entity.EmailDetails;
import com.yasselazhar.suivifinancier.auth.model.Token;
import com.yasselazhar.suivifinancier.auth.model.User;
import com.yasselazhar.suivifinancier.auth.repository.TokenRepository;
import com.yasselazhar.suivifinancier.auth.repository.UserRepository;
import com.yasselazhar.suivifinancier.auth.service.EmailService;
import com.yasselazhar.suivifinancier.auth.service.TokenService;

@Configuration
public class SuiviFinancierAuthHandler {
	
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    TokenService tokenService;

    @Autowired
    EmailService emailService;
    
	public SuiviFinancierAuthHandler() {}
	
	
	public HttpStatus createProfil(Map<String,String> userDetails) {

		User newUser = new User();
		Token newToken = new Token();
		EmailDetails emailDetails = new EmailDetails();
		String tokenContext = TokenContext.PASSWORD_INIT.toString();
		if(Objects.isNull(userRepository.findByEmail(userDetails.get("email")))) {
			//Here we  gone to set the logic for the account creation
			
			// new account creation
			newUser.setActif(0);
			newUser.setEmail(userDetails.get("email"));
			newUser.setNom(userDetails.get("nom"));
			newUser.setPrenom(userDetails.get("prenom"));
	    	newUser.setDateNaissance(userDetails.get("dateNaissance"));
			newUser.setAdresse(userDetails.get("adresse"));
			newUser.setVille(userDetails.get("ville"));
			newUser.setZip(userDetails.get("zip"));
			newUser.setTypeProfil(Integer.valueOf(userDetails.get("typeProfile")));
			newUser = userRepository.save(newUser);
			
			String token = tokenService.encryptToken(String.valueOf(newUser.getId()), newUser.getEmail(), tokenContext);
	    	newToken.setToken(token);
	    	newToken.setTokenContext(tokenContext);
	    	newToken.setUserId(String.valueOf(newUser.getId()));
	    	newToken = tokenRepository.save(newToken);
	    	
	    	emailDetails.setSubject("Nouveau Token");
	    	emailDetails.setMsgBody(token);
	    	emailDetails.setRecipient("yassine.elazhar@gmail.com");
	    	emailService.sendSimpleMail(emailDetails);
	    	
	    	
			System.out.println("user null");
		}

		
		
		
		return HttpStatus.OK;
		
	}
	
	
	
}