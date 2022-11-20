package com.yasselazhar.suivifinancier.auth.handler;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.yasselazhar.suivifinancier.auth.constant.TokenContext;
import com.yasselazhar.suivifinancier.auth.entity.EmailDetails;
import com.yasselazhar.suivifinancier.auth.model.Password;
import com.yasselazhar.suivifinancier.auth.model.SecureQuestion;
import com.yasselazhar.suivifinancier.auth.model.SecureResponse;
import com.yasselazhar.suivifinancier.auth.model.Token;
import com.yasselazhar.suivifinancier.auth.model.TypeProfile;
import com.yasselazhar.suivifinancier.auth.model.User;
import com.yasselazhar.suivifinancier.auth.repository.PasswordRepository;
import com.yasselazhar.suivifinancier.auth.repository.SecureQuestionRepository;
import com.yasselazhar.suivifinancier.auth.repository.SecureResponseRepository;
import com.yasselazhar.suivifinancier.auth.repository.TokenRepository;
import com.yasselazhar.suivifinancier.auth.repository.TypeProfileRepository;
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
    SecureQuestionRepository secureQuestionRepository;
    
    @Autowired
    SecureResponseRepository secureResponseRepository;
    
    @Autowired
    TypeProfileRepository typeProfileRepository;

    @Autowired
    TokenService tokenService;

    @Autowired
    EmailService emailService;

    @Autowired
    PasswordService passwordService;
    
	public SuiviFinancierAuthHandler() {}
	
	public String createProfile(User newUser) {
		Token newToken = new Token();
		EmailDetails emailDetails = new EmailDetails();
		String tokenContext = TokenContext.PASSWORD_INIT.toString();
		String tokenResult = "";
		
		
		if(Objects.isNull(userRepository.findByEmail(newUser.getEmail()))) {
			//Here we  gone to set the logic for the account creation
			
			//On verifie si le type de profile existe et si il est différent de 1 (Admin)
			if(!typeProfileRepository.findById(Integer.valueOf(newUser.getTypeProfil())).isEmpty() && (newUser.getTypeProfil() != "1")) {
				// new account creation
				newUser.setId(0);
				newUser.setPassword(0);
				newUser.setDateCreation(null);
				newUser.setDateModification(null);
				newUser.setActif(0);
				//TODO: Nous devrions enregister l'url base dans un fichier properties
				newUser.setPictureUrl("http://localhost:7070/mock-data/img/"+newUser.getEmail()+".jpg");
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
				tokenResult = "error";
			}
		} else {
			// We gone to check if we have a token for this user
			newToken = tokenRepository.findByUserId(String.valueOf(userRepository.findByEmail(newUser.getEmail()).getId()));
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

	public boolean activateProfile(String token, String password) {
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
	        emailUser = new String(decoder.decode(emailUser));
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
	
	public User getUser(String user) {
		User storedUser = new User();
		storedUser = userRepository.findByEmail(user);
		TypeProfile typeProfile = typeProfileRepository.findById(Integer.valueOf(storedUser.getTypeProfil())).orElse(new TypeProfile());
		storedUser.setTypeProfil(typeProfile.getTypeProfile());
		//we hide some values
		storedUser.setPassword(0);
		storedUser.setDateCreation(null);
		storedUser.setDateModification(null);
		
		return storedUser;
	}
	
	public User updateUser(User user) {
		User newStoredUser = userRepository.findByEmail(user.getEmail());
		if(Objects.nonNull(newStoredUser)) {
			if(user.getAdresse() != null) {
				newStoredUser.setAdresse(user.getAdresse());
			}
			if(user.getVille() != null) {
				newStoredUser.setVille(user.getVille());
			}
			if(user.getZip() != null) {
				newStoredUser.setZip(user.getZip());
			}
			if(user.getDateNaissance() != null) {
				newStoredUser.setDateNaissance(user.getDateNaissance());
			}
			if(user.getNom() != null) {
				newStoredUser.setNom(user.getNom());
			}
			if(user.getPrenom() != null) {
				newStoredUser.setPrenom(user.getPrenom());
			}
			userRepository.save(newStoredUser);
			
			//We hide some values
			newStoredUser.setPassword(0);
			newStoredUser.setDateCreation(null);
			newStoredUser.setDateModification(null);
		} else {
			newStoredUser = new User();
		}
		
		return newStoredUser;
	}
	
	public String removeUser(int id, String email) {
		String tokenContext = TokenContext.USER_DELETE.toString();
		String token = "";
		User userToRemove = userRepository.findByEmail(email);
		if(Objects.nonNull(userToRemove) && Objects.isNull(tokenRepository.findByUserId(String.valueOf(id)))) {
			if(userToRemove.getId() == id) {
				token = tokenService.encryptToken(String.valueOf(id), email, tokenContext);
				Token storedToken = new Token();
				storedToken.setToken(token);
				storedToken.setTokenContext(tokenContext);
				storedToken.setUserId(String.valueOf(id));
				tokenRepository.save(storedToken);
			}
		}
		
		return token;
	}

	public boolean removeUserApproved(int id, String email, String token) {
		boolean result = false;
		try {
			String tokenContext = TokenContext.USER_DELETE.toString();
			
			Map<String,String> tokenDecrypted = tokenService.decryptToken(token);
			if(tokenDecrypted.get("context").equalsIgnoreCase(tokenContext)
				&& tokenDecrypted.get("email").equalsIgnoreCase(email)
				&& tokenDecrypted.get("userId").equalsIgnoreCase(String.valueOf(id))){
				Token storedToken = tokenRepository.findByUserId(String.valueOf(id));
				
				if(storedToken.getToken().equalsIgnoreCase(token)) {
					tokenRepository.delete(storedToken);
					userRepository.deleteById(id);
					result = true;
				}
			}
		} catch (Exception e) {
			result = false;
		}
		
		return result;
	}
	
	public String updateEmail(int userId, String oldEmail, String newEmail) {
		String tokenContext = TokenContext.EMAIL_UPDATE.toString();

		String token = "";
		if(!oldEmail.equalsIgnoreCase(newEmail)) {
			User userToUpdate = userRepository.findByEmail(oldEmail);
			if(Objects.nonNull(userToUpdate) && Objects.isNull(tokenRepository.findByUserId(String.valueOf(userId)))) {
				if(userToUpdate.getId() == userId) {
					token = tokenService.encryptToken(String.valueOf(userId), newEmail, tokenContext);
					Token storedToken = new Token();
					storedToken.setToken(token);
					storedToken.setTokenContext(tokenContext);
					storedToken.setUserId(String.valueOf(userId));
					tokenRepository.save(storedToken);

					/*
					EmailDetails emailDetails = new EmailDetails();
					emailDetails.setRecipient(newEmail);
					emailDetails.setSubject("change email");
					emailDetails.setMsgBody(token);
					
					emailService.sendSimpleMail(emailDetails);
					*/			
				}
			}
		}
		
		return token;
	}
	
	public boolean updateEmailApproved(String token) {

		boolean result = false;
		try {
			String tokenContext = TokenContext.EMAIL_UPDATE.toString();
			
			Map<String,String> tokenDecrypted = tokenService.decryptToken(token);
			if(tokenDecrypted.get("context").equalsIgnoreCase(tokenContext)){
				Token storedToken = tokenRepository.findByUserId(String.valueOf(tokenDecrypted.get("userId")));
				
				if((Objects.nonNull(storedToken)) && (storedToken.getToken().equalsIgnoreCase(token)))  {
					tokenRepository.delete(storedToken);
					User storedUser = userRepository.findById(Integer.valueOf(tokenDecrypted.get("userId"))).orElse(new User());
					if(Objects.nonNull(storedUser.getId())) {
						storedUser.setEmail(tokenDecrypted.get("email"));
						userRepository.save(storedUser);
					}
					result = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		
		return result;
	}
	
	public List<SecureQuestion> getSecureQuestions(){
		List<SecureQuestion> listSecureQuestion = secureQuestionRepository.findByUserId(0);
		return listSecureQuestion;
	}
	
	public boolean addUserQuestionResponse(int userId, int questionId, String question, String response) {
		boolean result = false;
		try {
			if(Objects.nonNull((userRepository.findById(userId)))
					&& (secureResponseRepository.findByUserId(userId).size() < 3)) {
				if(questionId == 0) {
					if((secureQuestionRepository.findByUserId(userId).isEmpty())) {
						SecureQuestion secureQuestion = new SecureQuestion();
						secureQuestion.setQuestion(question);
						secureQuestion.setUserId(userId);
						secureQuestion = secureQuestionRepository.save(secureQuestion);

						SecureResponse secureResponse = new SecureResponse();
						secureResponse.setSecureQuestionId(secureQuestion.getId());
						secureResponse.setUserId(userId);
						secureResponse.setResponse(response);
						secureResponse = secureResponseRepository.save(secureResponse);
						
						result = true;
					}
					
				} else {
					if(secureResponseRepository.findBySecureQuestionId(questionId).isEmpty()) {
						SecureResponse secureResponse = new SecureResponse();
						secureResponse.setSecureQuestionId(questionId);
						secureResponse.setUserId(userId);
						secureResponse.setResponse(response);
						secureResponse = secureResponseRepository.save(secureResponse);
						result = true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		
		return result;
	}
	
	public SecureQuestion askSecureQuestion(String email) {
		User storedUser = userRepository.findByEmail(email);
		SecureQuestion secureQuestion = new SecureQuestion();
		if(Objects.nonNull(storedUser)) {
			List<SecureResponse> listSecureResponse = secureResponseRepository.findByUserId(storedUser.getId());
			if(!listSecureResponse.isEmpty()) {
			    Random rand = new Random();
			    SecureResponse randomSecureResponse = listSecureResponse.get(rand.nextInt(listSecureResponse.size()));
			    
			    secureQuestion = secureQuestionRepository.findById(randomSecureResponse.getSecureQuestionId()).orElse(new SecureQuestion());
			}
		}
		return secureQuestion;
	}
	
	public String passwordForget(String email) {
		String tokenContext = TokenContext.PASSWORD_FORGETED.toString();

		String token = "";
		User storedUser = userRepository.findByEmail(email);
		if((Objects.nonNull(storedUser))
			&& (Objects.isNull(tokenRepository.findByUserId(String.valueOf(storedUser.getId()))))
			&& (Objects.isNull(tokenRepository.findByTokenContextAndUserId(tokenContext,String.valueOf(storedUser.getId()))))) {
			
			token = tokenService.encryptToken(String.valueOf(storedUser.getId()), email, tokenContext);
			Token storedToken = new Token();
			storedToken.setToken(token);
			storedToken.setTokenContext(tokenContext);
			storedToken.setUserId(String.valueOf(storedUser.getId()));
			tokenRepository.save(storedToken);

			/*
			//Envoyer un mail sur une page qui appelera ask question
			EmailDetails emailDetails = new EmailDetails();
			emailDetails.setRecipient(newEmail);
			emailDetails.setSubject("change password form");
			emailDetails.setMsgBody(token);
			
			emailService.sendSimpleMail(emailDetails);
			*/
		}
		
		return token;
	}
	
	public boolean passowrdForgetApproved(String email, String token, int secureQuestionId, String response, String newPassword) {
		boolean result = false;
        Base64.Decoder decoder = Base64.getDecoder();
        //We convert the Base64 password to utf8 password
        newPassword = new String(decoder.decode(newPassword));
		String tokenContext = TokenContext.PASSWORD_FORGETED.toString();
		User storedUser = userRepository.findByEmail(email);
		SecureResponse secureResponse = secureResponseRepository.findBySecureQuestionIdAndUserId(secureQuestionId,storedUser.getId());
		Map<String,String> tokenDetails = tokenService.decryptToken(token);
		Token storedToken = tokenRepository.findByTokenContextAndUserId(tokenContext, String.valueOf(storedUser.getId()));
		Password storedPassword = passwordRepository.findByUserId(String.valueOf(storedUser.getId()));
		
		if(!passwordService.verifyPassword(newPassword, storedPassword.getPassword())) {
			if( Objects.nonNull(secureResponse) && Objects.nonNull(storedUser) && Objects.nonNull(storedToken) && Objects.nonNull(storedPassword)) {
				if(storedToken.getToken().equalsIgnoreCase(token) && ((new Date(Long.valueOf(tokenDetails.get("expiryDate")))).after(new Date()))) {
					if(secureResponse.getResponse().equalsIgnoreCase(response)) {
						newPassword = passwordService.hashPassword(newPassword);
						storedPassword.setPassword(newPassword);
						tokenRepository.delete(storedToken);
						passwordRepository.save(storedPassword);
						

						/*
						EmailDetails emailDetails = new EmailDetails();
						emailDetails.setRecipient(storedUser.getEmail());
						emailDetails.setSubject("change password confirmation");
						emailDetails.setMsgBody("your password has been changed");
						
						emailService.sendSimpleMail(emailDetails);
						*/
						
						result = true;
					}
				}
			}
		}
		return result;
	}
	
	public String deactivateProfile(int userId, String email) {
		String tokenContext = TokenContext.DEACTIVATE_USER.toString();

		String token = "";
		User storedUser = userRepository.findByEmail(email);
		if((Objects.nonNull(storedUser) && storedUser.getId() == userId)
			&& (Objects.isNull(tokenRepository.findByUserId(String.valueOf(storedUser.getId()))))
			&& (Objects.isNull(tokenRepository.findByTokenContextAndUserId(tokenContext,String.valueOf(storedUser.getId()))))) {
			
			token = tokenService.encryptToken(String.valueOf(storedUser.getId()), email, tokenContext);
			Token storedToken = new Token();
			storedToken.setToken(token);
			storedToken.setTokenContext(tokenContext);
			storedToken.setUserId(String.valueOf(storedUser.getId()));
			tokenRepository.save(storedToken);

			/*
			//Envoyer un mail sur une page qui appelera ask question
			EmailDetails emailDetails = new EmailDetails();
			emailDetails.setRecipient(email);
			emailDetails.setSubject("desactivate account");
			emailDetails.setMsgBody(token);
			
			emailService.sendSimpleMail(emailDetails);
			*/
		}
		
		return token;
	}
	
	public boolean deactivateProfileApproved(String token) {
		boolean result = false;
		try {
			String tokenContext = TokenContext.DEACTIVATE_USER.toString();
			
			Map<String,String> tokenDecrypted = tokenService.decryptToken(token);
			if(tokenDecrypted.get("context").equalsIgnoreCase(tokenContext)){
				Token storedToken = tokenRepository.findByUserId(String.valueOf(tokenDecrypted.get("userId")));
				
				if((Objects.nonNull(storedToken)) && (storedToken.getToken().equalsIgnoreCase(token)))  {
					tokenRepository.delete(storedToken);
					User storedUser = userRepository.findById(Integer.valueOf(tokenDecrypted.get("userId"))).orElse(new User());

					if((new Date(Long.valueOf(tokenDecrypted.get("expiryDate")))).after(new Date())){
		
						if(Objects.nonNull(storedUser.getId())) {
							storedUser.setActif(0);
							userRepository.save(storedUser);
						}
						result = true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		
		return result;
	}

	public String reactivateProfile(String email) {
		String tokenContext = TokenContext.ACTIVATE_USER.toString();

		String token = "";
		User storedUser = userRepository.findByEmail(email);
		if((Objects.nonNull(storedUser))
			&& (Objects.isNull(tokenRepository.findByUserId(String.valueOf(storedUser.getId()))))
			&& (Objects.isNull(tokenRepository.findByTokenContextAndUserId(tokenContext,String.valueOf(storedUser.getId()))))) {
			
			token = tokenService.encryptToken(String.valueOf(storedUser.getId()), email, tokenContext);
			Token storedToken = new Token();
			storedToken.setToken(token);
			storedToken.setTokenContext(tokenContext);
			storedToken.setUserId(String.valueOf(storedUser.getId()));
			tokenRepository.save(storedToken);

			/*
			//Envoyer un mail sur une page qui appelera ask question
			EmailDetails emailDetails = new EmailDetails();
			emailDetails.setRecipient(email);
			emailDetails.setSubject("desactivate account");
			emailDetails.setMsgBody(token);
			
			emailService.sendSimpleMail(emailDetails);
			*/
		}
		
		return token;
	}
	
	public boolean reactivateProfileApproved(String token) {
		boolean result = false;
		try {
			String tokenContext = TokenContext.ACTIVATE_USER.toString();
			
			Map<String,String> tokenDecrypted = tokenService.decryptToken(token);
			if(tokenDecrypted.get("context").equalsIgnoreCase(tokenContext)){
				Token storedToken = tokenRepository.findByUserId(String.valueOf(tokenDecrypted.get("userId")));
				
				if((Objects.nonNull(storedToken)) && (storedToken.getToken().equalsIgnoreCase(token)))  {
					tokenRepository.delete(storedToken);
					User storedUser = userRepository.findById(Integer.valueOf(tokenDecrypted.get("userId"))).orElse(new User());

					if((new Date(Long.valueOf(tokenDecrypted.get("expiryDate")))).after(new Date())){
		
						if(Objects.nonNull(storedUser.getId())) {
							storedUser.setActif(1);
							userRepository.save(storedUser);
						}
						result = true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		
		return result;
	}
	
	public String updateTypeProfile(String email) {
		String tokenContext = TokenContext.TYPE_PROFIL_UPDATE.toString();

		String token = "";
		User storedUser = userRepository.findByEmail(email);
		if((Objects.nonNull(storedUser))
			&& (Objects.isNull(tokenRepository.findByUserId(String.valueOf(storedUser.getId()))))
			&& (Objects.isNull(tokenRepository.findByTokenContextAndUserId(tokenContext,String.valueOf(storedUser.getId()))))) {
			
			token = tokenService.encryptToken(String.valueOf(storedUser.getId()), email, tokenContext);
			Token storedToken = new Token();
			storedToken.setToken(token);
			storedToken.setTokenContext(tokenContext);
			storedToken.setUserId(String.valueOf(storedUser.getId()));
			tokenRepository.save(storedToken);

			/*
			//Envoyer un mail sur une page qui appelera ask question
			EmailDetails emailDetails = new EmailDetails();
			emailDetails.setRecipient(email);
			emailDetails.setSubject("update type profile");
			emailDetails.setMsgBody(token);
			
			emailService.sendSimpleMail(emailDetails);
			*/
		}
		
		return token;
	}
	
	public boolean updateTypeProfileApproved(String token, String typeProfile) {
		boolean result = false;
		try {
			String tokenContext = TokenContext.TYPE_PROFIL_UPDATE.toString();
			
			Map<String,String> tokenDecrypted = tokenService.decryptToken(token);

			if(!typeProfileRepository.findById(Integer.valueOf(typeProfile)).isEmpty()) {
				if(tokenDecrypted.get("context").equalsIgnoreCase(tokenContext)){
					Token storedToken = tokenRepository.findByUserId(String.valueOf(tokenDecrypted.get("userId")));
					
					if((Objects.nonNull(storedToken)) && (storedToken.getToken().equalsIgnoreCase(token)))  {
						tokenRepository.delete(storedToken);
						User storedUser = userRepository.findById(Integer.valueOf(tokenDecrypted.get("userId"))).orElse(new User());

						if((new Date(Long.valueOf(tokenDecrypted.get("expiryDate")))).after(new Date())){
			
							if(Objects.nonNull(storedUser.getId())) {
								storedUser.setTypeProfil(typeProfile);
								userRepository.save(storedUser);
							}
							result = true;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		
		return result;
	}
	
}