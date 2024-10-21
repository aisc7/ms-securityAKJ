package com.aisc.ms_security.Services;

import com.aisc.ms_security.Models.User;
import com.aisc.ms_security.Models.EmailContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class RequestService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ms-notifications.base-url}")
    public String notificationUrl;

    // Método para obtener usuarios desde el microservicio de usuarios
    public List<User> getUser() {
        String endpointName = "get-user";
        String url = notificationUrl.endsWith("/") ? notificationUrl + endpointName : notificationUrl + "/" + endpointName;
        ResponseEntity<User[]> response = restTemplate.getForEntity(url, User[].class);
        return Arrays.asList(response.getBody());
    }

    // Método para enviar correos electrónicos
    public void sendEmail(EmailContent emailContent) {
        String endpointName = "send-email";
        String url = notificationUrl.endsWith("/") ? notificationUrl + endpointName : notificationUrl + "/" + endpointName;

        // Manejar posibles excepciones al hacer la petición
        try {
            String response = restTemplate.postForObject(url, emailContent, String.class);
            System.out.println("Response from email service: " + response);
        } catch (Exception e) {
            System.err.println("Error al enviar el correo: " + e.getMessage());
        }
    }
}
