package com.kdcd.ms_security.Services;

import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


//una contraseña se envia al metodo y este genera una representacion del texto en SHA-256(toma cadenas de texto  y las convierte a codigos )
@Service
//el service es porque se puede inyectar esta clase
public class EncryptionService {
    //cada vez que se pone la mima palabra se obtiene el mismo cifrado
    public String convertSHA256(String password) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        byte[] hash = md.digest(password.getBytes());
        StringBuffer sb = new StringBuffer();
        for(byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
