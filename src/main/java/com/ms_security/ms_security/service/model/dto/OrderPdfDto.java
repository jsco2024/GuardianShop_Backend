package com.ms_security.ms_security.service.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class OrderPdfDto implements Serializable {

    private Long orderNumber;
    private String name;
    private Long quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}
