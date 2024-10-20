package com.aisc.ms_security.Services;

import com.aisc.ms_security.Models.User;
import com.aisc.ms_security.Models.EmailContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

@Service
public class RequestService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${api.ms-notificacion-url}")
    public String notificationUrl;

    public List<User> getUser() {
        String endpointName = "get-user";
        String url = notificationUrl + endpointName;
        ResponseEntity<User[]> response = restTemplate.getForEntity(url, User[].class);
        User[] user = response.getBody();
        return Arrays.asList(user);
    }

    public void sendEmail(EmailContent emailContent) {
        String endpointName = "send-email";
        String url = notificationUrl + endpointName;

        // Aquí pasamos el cuerpo del emailContent y esperamos una respuesta tipo String
        String response = restTemplate.postForObject(url, emailContent, String.class);

        // Si necesitas manejar la respuesta
        System.out.println("Response from email service: " + response);
    }
}
