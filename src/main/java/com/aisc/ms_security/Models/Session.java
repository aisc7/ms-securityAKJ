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
    //
    private int token; // Asegúrate de que este campo se utilice para almacenar el token 2FA
    private String expiration;

  

    // Apunta directamente al usuario
    @DBRef
    private User user;

    // Constructor actualizado para incluir el token
    public Session(int token, String expiration) {
        this.token = token; // Cambiar a token
        this.expiration = expiration;
    }

}