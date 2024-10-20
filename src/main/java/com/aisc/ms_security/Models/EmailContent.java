package com.aisc.ms_security.Models;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EmailContent {

    private String body_html;
    private String subject;
    private String recipient;

}
