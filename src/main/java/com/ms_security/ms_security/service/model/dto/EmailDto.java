package com.ms_security.ms_security.service.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class EmailDto implements Serializable {
    private String recipient;
    private String subject;
    private String message;
    private String resetLink;
}
