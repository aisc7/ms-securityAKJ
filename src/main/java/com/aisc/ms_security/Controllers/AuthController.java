package com.aisc.ms_security.Controllers;

import com.aisc.ms_security.Models.EmailContent;
import com.aisc.ms_security.Models.User;
import com.aisc.ms_security.Repositories.UserRepository;
import com.aisc.ms_security.Services.EncryptionService;
import com.aisc.ms_security.Services.JwtService;
import com.aisc.ms_security.Services.OAuth2Service;
import com.aisc.ms_security.Services.RequestService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private OAuth2Service oAuth2Service;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RequestService requestService;

    // Login para autenticación con JWT
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        // Validación de entrada
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email y contraseña son obligatorios");
        }

        // Buscar al usuario por email
        User user = userRepository.getUserByEmail(email);

        // Verificar si el usuario existe y si la contraseña es correcta
        if (user == null || !encryptionService.verifyPassword(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
        }

        // Generar JWT token
        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(token); // Retornar el token directamente
    }

    // Restablecimiento de contraseña (envía correo con nueva contraseña)
    @PatchMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> userRequest) {
        String email = userRequest.get("email");

        // Validación de entrada
        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El email es obligatorio");
        }

        // Buscar usuario por email
        User currentUser = userRepository.getUserByEmail(email);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        // Generar nueva contraseña
        String generatedPassword = encryptionService.generateRandomPassword(10);
        currentUser.setPassword(encryptionService.convertSHA256(generatedPassword)); // Encriptar la nueva contraseña
        userRepository.save(currentUser); // Guardar el usuario actualizado

        // Enviar nueva contraseña por correo usando el microservicio de correo
        EmailContent emailContent = new EmailContent();
        emailContent.setRecipient(email);
        emailContent.setSubject("Restablecimiento de contraseña");
        emailContent.setBody_html("<p>Su nueva contraseña es: " + generatedPassword + "</p>");

        // Manejar posibles errores de envío de correo
        try {
            requestService.sendEmail(emailContent);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al enviar el correo: " + e.getMessage());
        }

        return ResponseEntity.ok("Contraseña cambiada y enviada por correo");
    }

    // Enviar correos generales usando otro microservicio
    @PostMapping("/send-email")
    public ResponseEntity<String> sendEmail(@RequestBody EmailContent emailContent) {
        try {
            requestService.sendEmail(emailContent); // Usamos el microservicio para el envío de emails
            return ResponseEntity.ok("Email enviado con éxito");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al enviar el correo: " + e.getMessage());
        }
    }

    // Obtener todos los usuarios (puede servir para administración)
    @GetMapping("/user")
    public ResponseEntity<List<User>> getUser() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }


    // Endpoint para iniciar autenticación con Google
    @GetMapping("/google")
    public RedirectView authenticationWithGoogle(HttpSession session) {
        String state = UUID.randomUUID().toString();
        session.setAttribute("oauth_state", state);
        String authUrl = oAuth2Service.getGoogleAuthUrl(state);
        return new RedirectView(authUrl);
    }

    // Endpoint para iniciar autenticación con GitHub
    @GetMapping("/github")
    public RedirectView authenticationWithGithub(HttpSession session) {
        String state = UUID.randomUUID().toString();
        session.setAttribute("oauth_state", state);
        String authUrl = oAuth2Service.getGithubAuthUrl(state);

        // Imprime la URL generada
        System.out.println("GitHub Auth URL: " + authUrl);

        return new RedirectView(authUrl);
    }

    // Endpoint de callback para manejar la respuesta de Google y GitHub
    @GetMapping("/callback/google")
    public ResponseEntity<?> callBack(@RequestParam String code, @RequestParam String state, HttpSession session) {
        String sessionState = (String) session.getAttribute("oauth_state");
        if (sessionState == null || !sessionState.equals(state)) {
            return ResponseEntity.badRequest().body("Estado inválido");
        }

        try {
            // Intercambiar código por token
            Map<String, Object> tokenResponse = oAuth2Service.getGoogleAccessToken(code);
            String accessToken = (String) tokenResponse.get("access_token");

            // Obtener información del usuario
            Map<String, Object> userInfo = oAuth2Service.getGoogleUserInfo(accessToken);
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            // Manejo de errores, podrías registrar el error o devolver un mensaje adecuado
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar la autenticación");
        }
    }


    @GetMapping("/callback/github")
    public ResponseEntity<?> callBackGitHub(@RequestParam String code, @RequestParam String state, HttpSession session) {
        String sessionState = (String) session.getAttribute("oauth_state");
        if (sessionState == null || !sessionState.equals(state)) {
            return ResponseEntity.badRequest().body("Estado inválido");
        }

        try {
            // Intercambiar código por token de acceso
            Map<String, Object> tokenResponse = oAuth2Service.getGitHubAccessToken(code);
            String accessToken = (String) tokenResponse.get("access_token");

            // Obtener información del usuario de GitHub
            Map<String, Object> userInfo = oAuth2Service.getGitHubUserInfo(accessToken);
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            // Registro del error
            System.err.println("Error durante la autenticación de GitHub: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar la autenticación de GitHub");
        }
    }


    // Método para verificar si una cadena está vacía
    private boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }
}





