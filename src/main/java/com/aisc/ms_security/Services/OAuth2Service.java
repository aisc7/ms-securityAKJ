package com.aisc.ms_security.Services;

import com.aisc.ms_security.Models.User;
import com.aisc.ms_security.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OAuth2Service {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EncryptionService encryptionService;  // Inyectamos el servicio de encriptación

    // Método para obtener un usuario por su correo electrónico
    public Optional<User> getUserByEmail(String email) {
        return Optional.ofNullable(userRepository.getUserByEmail(email));
    }

    // Método para validar las credenciales del usuario
    public boolean validateUserCredentials(String email, String password) {
        User user = userRepository.getUserByEmail(email);
        if (user != null) {
            // Verificar la contraseña usando el método de cifrado
            String encryptedPassword = encryptionService.convertSHA256(password);
            return encryptedPassword.equals(user.getPassword());
        }
        return false;
    }
}
