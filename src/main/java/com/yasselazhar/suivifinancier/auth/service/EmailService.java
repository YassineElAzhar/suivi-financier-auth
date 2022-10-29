package com.yasselazhar.suivifinancier.auth.service;

import com.yasselazhar.suivifinancier.auth.entity.EmailDetails;

//Interface
public interface EmailService {

 // Method
 // To send a simple email
 String sendSimpleMail(EmailDetails details);

 // Method
 // To send an email with attachment
 String sendMailWithAttachment(EmailDetails details);
}