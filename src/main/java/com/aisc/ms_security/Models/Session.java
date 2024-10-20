package com.aisc.ms_security.Models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Session {
    @Id
    private String _id;
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

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public int getToken() { //
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }
}
