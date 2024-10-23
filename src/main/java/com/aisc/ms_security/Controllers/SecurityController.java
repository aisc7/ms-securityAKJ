package com.aisc.ms_security.Controllers;

import com.aisc.ms_security.Models.Session;
import com.aisc.ms_security.Models.User;
import com.aisc.ms_security.Repositories.SessionRepository;
import com.aisc.ms_security.Repositories.UserRepository;
import com.aisc.ms_security.Services.EncryptionService;
import com.aisc.ms_security.Services.JwtService;
import com.aisc.ms_security.Services.JSONResponsesService;
import com.aisc.ms_security.Services.OAuth2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@CrossOrigin
@RestController
@RequestMapping("/api/public/security")
public class SecurityController {

    @Autowired
    private UserRepository theUserRepository;

    @Autowired
    private EncryptionService theEncryptionService;

    @Autowired
    private JwtService theJwtService;

    @Autowired
    private JSONResponsesService jsonResponsesService;

    @Autowired
    private SessionRepository theSessionRepository;

    @Autowired
    private OAuth2Service oAuth2Service; // Inyectar el servicio

    @Value("${ms-notifications.base-url}")
    private String baseUrlNotifications;

    // Método para iniciar sesión
    @PostMapping("/login")
    public HashMap<String, Object> login(@RequestBody User theNewUser, final HttpServletResponse response) throws IOException {
        HashMap<String, Object> theResponse = new HashMap<>();
        String token = "";

        // Verificar si el email ingresado existe en el repositorio
        User theActualUser = this.theUserRepository.getUserByEmail(theNewUser.getEmail());

        // Validar contraseña
        if (theActualUser != null &&
                theActualUser.getPassword().equals(theEncryptionService.convertSHA256(theNewUser.getPassword()))) {
            // Generar el token de inicio de sesión
            token = theJwtService.generateToken(theActualUser);
            theActualUser.setPassword("");
            theResponse.put("token", token);
            theResponse.put("user", theActualUser);
            return theResponse;
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return theResponse;
        }
    }

    @PostMapping("/2Fa/{userId}")
    public ResponseEntity<HashMap<String, Object>> factorAuthentication(@RequestBody Session theSession, @PathVariable String userId) {
        HashMap<String, Object> theResponse = new HashMap<>();

        // Obtener las sesiones válidas para el usuario
        List<Session> validSessions = theSessionRepository.getSessionsByUserId(userId);

        // Validar si hay alguna sesión activa con el token proporcionado
        Session validSession = validSessions.stream()
                .filter(session -> session.getToken().equals(theSession.getToken()))
                .findFirst()
                .orElse(null);

        // Identificar al usuario y obtener su email
        User theUser = this.theUserRepository.findById(userId).orElse(null);
        if (theUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        String email = theUser.getEmail();

        // Si la sesión es válida y el usuario existe
        if (validSession != null) {
            // Crear un código random para 2FA
            String code2FA = generateVerificationCode();

            // Guarda el código 2FA en la sesión
            validSession.setCode2FA(code2FA); // Asegúrate que la sesión tenga este campo
            theSessionRepository.save(validSession); // Guarda la sesión actualizada con el código

            // Prepara la respuesta
            theResponse.put("code2FA", code2FA); // Código de 2FA generado
            theResponse.put("email", email); // Email del usuario

            // Llamar al servicio de OAuth2 para enviar el código 2FA al correo
            oAuth2Service.validationEmail(email, code2FA); // Usamos el método ValidationEmail para enviar el código

            return ResponseEntity.ok(theResponse);
        } else {
            // Sesión no encontrada o no válida
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    // Restablecimiento de contraseña
    @PatchMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody User user) {
        try {
            User currentUser = theUserRepository.getUserByEmail(user.getEmail());
            if (currentUser != null) {
                String generatedPassword = generateRandomPassword(10); // Genera una nueva contraseña aleatoria
                currentUser.setPassword(theEncryptionService.convertSHA256(generatedPassword)); // Encripta la nueva contraseña
                theUserRepository.save(currentUser); // Guarda el usuario actualizado

                // Enviar nueva contraseña por correo
                RestTemplate restTemplate = new RestTemplate();
                String urlPost = baseUrlNotifications + "reset-password";
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                String requestBody = "{\"email\":\"" + user.getEmail() + "\",\"new_password\":\"" + generatedPassword + "\"}";
                HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
                restTemplate.postForEntity(urlPost, requestEntity, String.class); // Envío del correo

                jsonResponsesService.setData(currentUser);
                jsonResponsesService.setMessage("Contraseña cambiada con éxito. Revisa tu correo.");
                return ResponseEntity.ok(jsonResponsesService.getFinalJSON());
            } else {
                jsonResponsesService.setMessage("El usuario no está registrado.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(jsonResponsesService.getFinalJSON());
            }
        } catch (Exception e) {
            jsonResponsesService.setError(e.toString());
            jsonResponsesService.setMessage("Error al generar una nueva contraseña.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jsonResponsesService.getFinalJSON());
        }
    }

    // Método para generar una contraseña aleatoria
    private String generateRandomPassword(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()"; // Caracteres permitidos
        StringBuilder password = new StringBuilder(length);
        SecureRandom random = new SecureRandom(); // Para generar números aleatorios de manera segura
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            password.append(characters.charAt(index));
        }
        return password.toString();
    }

    // Método para generar un código de verificación 2FA aleatorio
    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000)); // Código de 6 dígitos
    }
}
