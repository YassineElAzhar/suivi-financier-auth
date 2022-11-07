package com.yasselazhar.suivifinancier.auth.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yasselazhar.suivifinancier.auth.handler.SuiviFinancierAuthHandler;
import com.yasselazhar.suivifinancier.auth.model.SecureQuestion;
import com.yasselazhar.suivifinancier.auth.model.User;

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
    public ResponseEntity<String> createUser(@RequestBody User user) {
    	try {
        	String token = suiviFinancierAuthHandler.createProfil(user);
            return new ResponseEntity<>(token, HttpStatus.OK);
		} catch (Exception e) {
            return new ResponseEntity<>("", HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
    
    @RequestMapping(value="/activateProfil",method = RequestMethod.POST) 
    public ResponseEntity<Boolean> activateProfil(@RequestParam(value="token") String token, @RequestParam(value="password") String password) {
    	try {
        	boolean result = suiviFinancierAuthHandler.activateProfil(token, password);
            return new ResponseEntity<>(result, HttpStatus.OK);
		} catch (Exception e) {
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
    
    @RequestMapping(value="/login",method = RequestMethod.POST)
    public ResponseEntity<Boolean> login(@RequestParam(value = "emailUser")  String emailUser,@RequestParam(value = "password") String password) {
    	try {
        	boolean result = suiviFinancierAuthHandler.login(emailUser, password);
            return new ResponseEntity<>(result, HttpStatus.OK);
		} catch (Exception e) {
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }

    @GetMapping("/getUser/{email}")
    public ResponseEntity<User> getUser(@PathVariable(value = "email") String email) {
    	try {
            User user = suiviFinancierAuthHandler.getUser(email);
            return new ResponseEntity<>(user, HttpStatus.OK);
		} catch (Exception e) {
            return new ResponseEntity<>(new User(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
    
    @PutMapping("/updateUser")
    public ResponseEntity<User> udpateUser(@RequestBody User user) {
    	try {
        	User userUpdated = suiviFinancierAuthHandler.updateUser(user);
        	return new ResponseEntity<>(userUpdated, HttpStatus.OK);
		} catch (Exception e) {
            return new ResponseEntity<>(new User(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
    
    @DeleteMapping(value = "/removeUser")
    public ResponseEntity<String> removeUser(
    		@RequestParam(value = "id")  int id, 
    		@RequestParam(value = "email")  String email) {
    	try {
        	String result = suiviFinancierAuthHandler.removeUser(id, email);
            return new ResponseEntity<>(result, HttpStatus.OK);
		} catch (Exception e) {
            return new ResponseEntity<>("", HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
    
    @DeleteMapping(value = "/removeUserApproved")
    public ResponseEntity<Boolean> removeUserApproved(
    		@RequestParam(value = "id")  int id, 
    		@RequestParam(value = "email")  String email, 
    		@RequestParam(value = "token")  String token) {
    	try {
        	boolean result = suiviFinancierAuthHandler.removeUserApproved(id, email, token);
            return new ResponseEntity<>(result, HttpStatus.OK);
		} catch (Exception e) {
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }

    @RequestMapping(value="/updateEmail",method = RequestMethod.POST)
    public ResponseEntity<String> updateEmail(
    		@RequestParam(value = "userId")  int userId, 
    		@RequestParam(value = "oldEmail")  String oldEmail, 
    		@RequestParam(value = "newEmail")  String newEmail){
    	String token = "";
    	try {
    		token = suiviFinancierAuthHandler.updateEmail(userId, oldEmail, newEmail);
		} catch (Exception e) {
	        return new ResponseEntity<>("error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
    	
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    @RequestMapping(value="/updateEmailApproved",method = RequestMethod.POST)
    public ResponseEntity<Boolean> updateEmailApproved(@RequestParam(value = "token")  String token){
    	boolean result = false;
    	try {
    		result = suiviFinancierAuthHandler.updateEmailApproved(token);
		} catch (Exception e) {
	        return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
		}
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/getListOfQuestions")
    public ResponseEntity<List<SecureQuestion>> getListOfQuestions() {
    	try {
            List<SecureQuestion> secureQuestions = suiviFinancierAuthHandler.getSecureQuestions();
	        return new ResponseEntity<>(secureQuestions, HttpStatus.OK);
		} catch (Exception e) {
	        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }

    @RequestMapping(value="/addUserQuestionResponse",method = RequestMethod.POST)
    public ResponseEntity<Boolean> addNewQuestion(
    		@RequestParam(value = "userId") int userId, 
    		@RequestParam(value = "questionId") int questionId,
    		@RequestParam(value = "question") String question, 
    		@RequestParam(value = "response") String response){
    	boolean result = false;
    	try {
			result = suiviFinancierAuthHandler.addUserQuestionResponse(userId, questionId, question, response);
		} catch (Exception e) {
	        return new ResponseEntity<>(false, HttpStatus.OK);
		}
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/askSecureQuestion/{email}")
    public ResponseEntity<SecureQuestion> askSecureQuestion(@PathVariable(value = "email") String email) {
    	try {
            SecureQuestion secureQuestions = suiviFinancierAuthHandler.askSecureQuestion(email);
	        return new ResponseEntity<>(secureQuestions, HttpStatus.OK);
		} catch (Exception e) {
	        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
    

    @RequestMapping(value="/passwordForget",method = RequestMethod.POST)
    public ResponseEntity<String> passwordForget(@RequestParam(value = "email")  String email){
    	String token = "";
    	try {
    		token = suiviFinancierAuthHandler.passwordForget(email);
		} catch (Exception e) {
	        return new ResponseEntity<>("error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
    	
        return new ResponseEntity<>(token, HttpStatus.OK);
    }
    

    @RequestMapping(value="/passowrdForgetApproved",method = RequestMethod.POST)
    public ResponseEntity<Boolean> passowrdForgetApproved(
    		@RequestParam(value = "email")  String email,
    		@RequestParam(value = "token")  String token,
    		@RequestParam(value = "secureQuestionId")  int secureQuestionId,
    		@RequestParam(value = "response")  String response,
    		@RequestParam(value = "newPassword")  String newPassword){
    	boolean result = false;
    	try {
    		result = suiviFinancierAuthHandler.passowrdForgetApproved(email, token, secureQuestionId, response, newPassword);
		} catch (Exception e) {
	        return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
		}
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    
}
