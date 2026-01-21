package com.critiverse.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendRegistrationEmail(String to, String username) {
        try {
            System.out.println("Tentativo di invio email a: " + to);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("critiversemailservice@gmail.com");
            message.setTo(to);
            message.setSubject("Registrazione completata - Critiverse");
            message.setText("Ciao " + username + ",\n\n" +
                    "La tua registrazione su Critiverse è stata completata con successo!\n\n" +
                    "Grazie per esserti unito a noi.\n\n" +
                    "Il Team di Critiverse");
            
            mailSender.send(message);
            System.out.println("Email inviata con successo a: " + to);
        } catch (Exception e) {
            System.err.println("Errore nell'invio dell'email a " + to + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
