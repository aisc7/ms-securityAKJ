package com.aisc.ms_security.Controllers;

import com.aisc.ms_security.Models.EmailContent;
import com.aisc.ms_security.Models.User;
import com.aisc.ms_security.Services.OAuth2Service;
import com.aisc.ms_security.Services.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private OAuth2Service oAuth2Service;

    @Autowired
    private RequestService requestService;

    @PostMapping("/send-email")
    public ResponseEntity<String> sendEmail(@RequestBody EmailContent emailContent) {
        requestService.sendEmail(emailContent);
        // Crea un ResponseEntity con el cuerpo y el estado HTTP
        return new ResponseEntity<>("Email sent successfully", HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<List<User>> getUser() {
        List<User> users = requestService.getUser();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}


