package com.ms_security.ms_security.service.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ResetPasswordRequestDto implements Serializable {

    private String email;
}
