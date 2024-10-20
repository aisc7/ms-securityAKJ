package com.aisc.ms_security.Models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


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

    public String get_id() {
        return _id;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
