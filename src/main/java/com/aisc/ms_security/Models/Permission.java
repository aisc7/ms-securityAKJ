package com.aisc.ms_security.Models;

import lombok.Data;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Getter
@Data
@Document
public class Permission {
    @Id
    private String _id;
    private String description;
    private String url;
    private String method;

    public Permission(String description, String url, String method){
        this.description = description;
        this.url = url;
        this.method = method;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
