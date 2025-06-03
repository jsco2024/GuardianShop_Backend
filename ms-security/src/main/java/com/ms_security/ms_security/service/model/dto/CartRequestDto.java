package com.ms_security.ms_security.service.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class CartRequestDto implements Serializable {
    private Long cartId;
    private Long inventoryId;
    private Long quantity;
    private Long userId;
    private List<OrderItemDto> items;
    private String createUser;
}
