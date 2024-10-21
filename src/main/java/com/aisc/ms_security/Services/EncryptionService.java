package com.aisc.ms_security.Services;

import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

// Una contraseña se envía al método y este genera una representación del texto en SHA-256 (toma cadenas de texto y las convierte a códigos)
@Service
// El service es porque se puede inyectar esta clase
public class EncryptionService {

    // Método para convertir una contraseña a su hash en SHA-256
    public String convertSHA256(String password) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        byte[] hash = md.digest(password.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    // Método para verificar si una contraseña coincide con el hash almacenado
    public boolean verifyPassword(String rawPassword, String hashedPassword) {
        String hashedInput = convertSHA256(rawPassword);
        return hashedInput != null && hashedInput.equals(hashedPassword);
    }

    // Método para generar una contraseña aleatoria
    public String generateRandomPassword(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        StringBuilder password = new StringBuilder(length);
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            password.append(characters.charAt(index));
        }
        return password.toString();
    }
}
