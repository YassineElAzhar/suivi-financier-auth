package com.yasselazhar.suivifinancier.auth.handler;

import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.yasselazhar.suivifinancier.auth.constant.TokenContext;
import com.yasselazhar.suivifinancier.auth.entity.EmailDetails;
import com.yasselazhar.suivifinancier.auth.model.Password;
import com.yasselazhar.suivifinancier.auth.model.Token;
import com.yasselazhar.suivifinancier.auth.model.User;
import com.yasselazhar.suivifinancier.auth.repository.PasswordRepository;
import com.yasselazhar.suivifinancier.auth.repository.TokenRepository;
import com.yasselazhar.suivifinancier.auth.repository.UserRepository;
import com.yasselazhar.suivifinancier.auth.service.EmailService;
import com.yasselazhar.suivifinancier.auth.service.PasswordService;
import com.yasselazhar.suivifinancier.auth.service.TokenService;

@Configuration
public class SuiviFinancierAuthHandler {
	
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    TokenRepository tokenRepository;
    
    @Autowired
    PasswordRepository passwordRepository;

    @Autowired
    TokenService tokenService;

    @Autowired
    EmailService emailService;

    @Autowired
    PasswordService passwordService;
    
	public SuiviFinancierAuthHandler() {}
	
	
	public String createProfil(Map<String,String> userDetails) {
		User newUser = new User();
		Token newToken = new Token();
		EmailDetails emailDetails = new EmailDetails();
		String tokenContext = TokenContext.PASSWORD_INIT.toString();
		String tokenResult = "";
		
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
	    	/*Nous allons envoyer le mail*/
	    	//String statusEmail = emailService.sendSimpleMail(emailDetails);
	    	
	    	tokenResult = newToken.getToken();
		} else {
			// We gone to check if we have a token for this user
			newToken = tokenRepository.findByUserId(String.valueOf(userRepository.findByEmail(userDetails.get("email")).getId()));
			if((Objects.nonNull(newToken)) && (newToken.getTokenContext().equalsIgnoreCase(tokenContext))){
				Map<String,String> tokenDetails = tokenService.decryptToken(newToken.getToken());
				if((new Date(Long.valueOf(tokenDetails.get("expiryDate")))).before(new Date())) {
					tokenResult = newToken.getToken();
				} else {
					tokenResult = "error";
				}
			} else {
				tokenResult = "error";
			}
			
		}		
		return tokenResult;
	}
	

	public boolean activateProfil(String token, String password) {
		boolean resultFlag = true;
		try {
			Base64.Decoder decoder = Base64.getDecoder();
	        String validationRegEx = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[-+!*$@%_])([-+!*$@%_\\w]{8,25})$";
	        Token tokenDB = new Token();
	        User newUser = new User();
	        
	        //We convert the Base64 password to utf8 password
	        password = new String(decoder.decode(password));
	        Map<String,String> tokenDetails = tokenService.decryptToken(token);
	        
			if((resultFlag) && (!password.matches(validationRegEx))) { resultFlag = false; }

			if((resultFlag) && (Objects.nonNull(userRepository.findById(Integer.valueOf(tokenDetails.get("userId")))))){
				newUser = userRepository.findById(Integer.valueOf(tokenDetails.get("userId"))).orElse(new User());
			} else { resultFlag = false; }
			
			if((resultFlag) && (Objects.nonNull(passwordRepository.findByUserId(tokenDetails.get("userId"))))) { resultFlag = false; }
			
			tokenDB = tokenRepository.findByUserId(tokenDetails.get("userId"));
			
			if((resultFlag) && (Objects.isNull(tokenDB.getId()))) { resultFlag = false; }
			
			if((resultFlag) && (tokenDB.getTokenContext().equalsIgnoreCase(tokenDetails.get("context")))){
				Password newPassword = new Password();
				
				String hashPassword = passwordService.hashPassword(password);
				newPassword.setPassword(hashPassword);
				
				newPassword.setUserId(tokenDetails.get("userId"));
				newPassword = passwordRepository.save(newPassword);
				
				newUser.setPassword(newPassword.getId());
				newUser.setActif(1);
				newUser.setDateModification(new Date());
				newUser = userRepository.save(newUser);
				
				tokenRepository.deleteById(tokenDB.getId());
				
			}
		} catch (Exception e) {
			resultFlag = false;
		}
        
		
        
        return resultFlag;
	}
	
	
	public boolean login(String emailUser, String password) {
		boolean result = false;
		try {
	        Base64.Decoder decoder = Base64.getDecoder();
	        //We convert the Base64 password to utf8 password
	        password = new String(decoder.decode(password));
			User storedUser = userRepository.findByEmail(emailUser);
			if(Objects.nonNull(storedUser)) {
				int userid = storedUser.getId();
				Password storedPassword = passwordRepository.findByUserId(String.valueOf(userid));
				if(Objects.nonNull(storedPassword)) {
					result = passwordService.verifyPassword(password, storedPassword.getPassword());
				}
			}
		} catch (Exception e) {
			result = false;
		}
		return result;
	}
	
	
	
}