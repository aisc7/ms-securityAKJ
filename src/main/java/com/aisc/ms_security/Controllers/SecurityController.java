package com.aisc.ms_security.Controllers;

import com.aisc.ms_security.Models.Session;
import com.aisc.ms_security.Models.User;
import com.aisc.ms_security.Repositories.SessionRepository;
import com.aisc.ms_security.Repositories.UserRepository;
import com.aisc.ms_security.Services.EncryptionService;
import com.aisc.ms_security.Services.JwtService;
import com.aisc.ms_security.Services.JSONResponsesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashMap;
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

    @Value("${ms-notifications.base-url}")
    private String baseUrlNotifications;

    // Método para iniciar sesión
    @PostMapping("/login")
    public HashMap<String,Object> login(@RequestBody User theNewUser,
                                        final HttpServletResponse response)throws IOException {
        HashMap<String, Object> theResponse = new HashMap<>();
        String token = "";
        //Se verifica si el email ingresado existe en el repositorio
        User theActualUser = this.theUserRepository.getUserByEmail(theNewUser.getEmail());
        //Se verifica si el email ingresado si esta y si tambien la contraseña ingresada es igual a la contraseña del usuario
        if (theActualUser != null &&
                theActualUser.getPassword().equals(theEncryptionService.convertSHA256(theNewUser.getPassword()))) {
            //Se genera el token de inicio de sesion
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

    // Autenticación de 2 factores
    @PostMapping("/login/{userId}")
    public ResponseEntity<HashMap<String, Object>> factorAuthentication(@RequestBody Session theSession, @PathVariable String userId) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            // Obtén el token 2FA desde la sesión
            int token2FA = theSession.getToken(); // Usa el método correcto

            // Busca el usuario correspondiente al userId
            User theUser = theUserRepository.getUserById(userId);

            // Verifica si hay una sesión válida para el usuario y el token 2FA
            Session validSession = theSessionRepository.getSessionbyUserId(userId, token2FA);

            if (validSession != null && theUser != null) {
                // Genera el JWT
                String jwtToken = theJwtService.generateToken(theUser);

                // Guarda el token en la sesión
                validSession.setToken(token2FA); // Guarda el token 2FA
                theSessionRepository.save(validSession); // Guarda la sesión actualizada

                // Limpia la contraseña antes de devolver el usuario
                theUser.setPassword("");

                // Prepara la respuesta
                response.put("token", jwtToken); // JWT que generaste
                response.put("user", theUser); // Usuario autenticado

                // Devuelve un 200 OK con el token y el usuario
                return ResponseEntity.ok(response);
            } else {
                // Devuelve un 401 Unauthorized si la sesión no es válida
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            // Manejo de excepciones
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
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
                String urlPost = baseUrlNotifications + "email_reset_password";
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
}
