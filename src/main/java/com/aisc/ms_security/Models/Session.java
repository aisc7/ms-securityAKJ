package com.aisc.ms_security.Models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@Data
@Document
public class Session {
    @Id
    private String _id;

    // Cambiar el tipo de token a String si es un token JWT u otro formato similar
    private String token; // Se recomienda que sea de tipo String si el token es una cadena
    private String expiration;
    private String code2FA; // Nuevo campo para almacenar el código 2FA

    // Apunta directamente al usuario
    @DBRef
    private User user;

    // Constructor actualizado para incluir el token
    public Session(String token, String expiration) {
        this.token = token; // Cambiar a token
        this.expiration = expiration;
    }

    // Constructor vacío para la inicialización de MongoDB
    public Session() {}
}
