package com.critiverse.service;

import com.resend.*;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

    @Value("${resend.api.key}")
    private String resendApiKey;

    @Value("${resend.from.email:onboarding@resend.dev}")
    private String fromEmail;

    @Async
    public void sendRegistrationEmail(String to, String username) {
        try {
            Resend resend = new Resend(resendApiKey);

            CreateEmailOptions sendEmailRequest = CreateEmailOptions.builder()
                    .from(fromEmail)
                    .to(List.of(to))
                    .subject("Registrazione completata - Critiverse")
                    .html("<div style='font-family: Arial, sans-serif;'>" +
                          "<h2>Ciao " + username + "!</h2>" +
                          "<p>La tua registrazione su <strong>Critiverse</strong> è stata completata con successo!</p>" +
                          "<p>Grazie per esserti unito a noi.</p>" +
                          "<br>" +
                          "<p><em>Il Team di Critiverse</em></p>" +
                          "</div>")
                    .build();

            CreateEmailResponse response = resend.emails().send(sendEmailRequest);
            System.out.println("✓ Email inviata con successo a: " + to + " (ID: " + response.getId() + ")");
        } catch (ResendException e) {
            System.err.println("✗ Errore Resend nell'invio dell'email a " + to + ": " + e.getMessage());
        } catch (Exception e) {
            System.err.println("✗ Errore nell'invio dell'email a " + to + ": " + e.getMessage());
        }
    }
}


