package com.critiverse.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Service
public class EmailService {

    private final String apiKey;
    private final String fromEmail;
    private final HttpClient httpClient;

    public EmailService(
            @Value("${resend.api.key}") String apiKey,
            @Value("${resend.from.email}") String fromEmail) {
        this.apiKey = apiKey;
        this.fromEmail = fromEmail;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Async
    public void sendRegistrationEmail(String to, String username) {
        try {
            if (apiKey == null || apiKey.isBlank()) {
                System.err.println("✗ RESEND_API_KEY non configurata; email non inviata a " + to);
                return;
            }

            String subject = "Registrazione completata - Critiverse";
            String html = "<div style='font-family: Arial, sans-serif;'>" +
                    "<h2>Ciao " + username + "!</h2>" +
                    "<p>La tua registrazione su <strong>Critiverse</strong> è stata completata con successo!</p>" +
                    "<p>Grazie per esserti unito a noi.</p>" +
                    "<br>" +
                    "<p><em>Il Team di Critiverse</em></p>" +
                    "</div>";

            String body = String.format("{\"from\":\"%s\",\"to\":[\"%s\"],\"subject\":\"%s\",\"html\":\"%s\"}",
                    jsonEscape(fromEmail),
                    jsonEscape(to),
                    jsonEscape(subject),
                    jsonEscape(html));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.resend.com/emails"))
                    .timeout(Duration.ofSeconds(30))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                System.out.println("✓ Email inviata con successo a: " + to);
            } else {
                System.err.println("✗ Errore Resend " + response.statusCode() + " inviando a " + to + ": " + response.body());
            }
        } catch (Exception e) {
            System.err.println("✗ Errore nell'invio dell'email a " + to + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String jsonEscape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}


