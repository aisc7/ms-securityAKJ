package com.aisc.ms_security.Models;

public class EmailContent {

    private String body_html;
    private String subject;
    private String recipient;

    public String getbody_html() {
        return body_html;
    }

    public void setbody_html(String body_html) {
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
