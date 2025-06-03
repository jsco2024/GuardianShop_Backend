package com.ms_security.ms_security.service.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ResponseErrorDto implements Serializable {
    private int numOfErrors;
    private String message;
}
