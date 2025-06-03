package com.ms_security.ms_security.service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponsePaymentDto implements Serializable {

    private String paymentId;
    private String clientSecret;
}
