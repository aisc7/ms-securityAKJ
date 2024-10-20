package com.aisc.ms_security.Models;

//Clase intermedia
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
public class UserRole {
    @Id
    private String _id;
    @DBRef
    private Role role;
    @DBRef
    private User user;

    //No es necesario el constructor con parametros aqui porque no tiene atributos propios
    public UserRole() {
    }

}
