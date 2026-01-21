package com.critiverse.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendRegistrationEmail(String to, String username) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom("Critiverse <williwonga99@gmail.com>");
            helper.setTo(to);
            helper.setSubject("Registrazione completata - Critiverse");
            helper.setText(
                "<div style='font-family: Arial, sans-serif;'>" +
                "<h2>Ciao " + username + "!</h2>" +
                "<p>La tua registrazione su <strong>Critiverse</strong> è stata completata con successo!</p>" +
                "<p>Grazie per esserti unito a noi.</p>" +
                "<br>" +
                "<p><em>Il Team di Critiverse</em></p>" +
                "</div>",
                true // HTML
            );
            
            mailSender.send(message);
            System.out.println("✓ Email inviata con successo a: " + to);
        } catch (Exception e) {
            System.err.println("✗ Errore nell'invio dell'email a " + to + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}


