package com.aisc.ms_security.Models;


public class EmailContent {

    private String body_html;
    private String subject;
    private String recipient;
    // Constructor sin argumentos
    public EmailContent() {
    }

    // Constructor con parámetros
    public EmailContent(String subject, String recipient, String body_html) {
        this.subject = subject;
        this.recipient = recipient;
        this.body_html = body_html;
    }

    public String getBody_html() {
        return body_html;
    }

    public void setBody_html(String body_html) {
        this.body_html = body_html;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
}

