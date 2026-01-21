package com.critiverse.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendRegistrationEmail(String to, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Registrazione completata - Critiverse");
        message.setText("Ciao " + username + ",\n\n" +
                "La tua registrazione su Critiverse è stata completata con successo!\n\n" +
                "Grazie per esserti unito a noi.\n\n" +
                "Il Team di Critiverse");
        
        mailSender.send(message);
    }
}
