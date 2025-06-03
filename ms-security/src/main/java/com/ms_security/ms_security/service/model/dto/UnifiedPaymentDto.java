package com.ms_security.ms_security.service.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UnifiedPaymentDto implements Serializable {
    private Float amount;
    private String paymentMethod;
    private String currency = "USD";
    private String description = "Payment for Order";
}
