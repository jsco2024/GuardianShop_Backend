package com.ms_security.ms_security.service.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderDto implements Serializable {
    @NotNull(message = "The id field cannot be null", groups = Update.class)
    @Null(message = "The id field must be null", groups = Create.class)
    @Min(value = 1, message = "The minimum value for id is 1")
    private Long id;

    @NotNull(message = "The orderNumber field cannot be null", groups = Update.class)
    @Null(message = "The orderNumber field must be null", groups = Create.class)
    private Long orderNumber;

    @NotNull(message = "The name field cannot be null", groups = {OrderItemDto.Create.class, OrderItemDto.Update.class})
    private BigDecimal unitPrice;

    @NotNull(message = "The totalPrice field cannot be null", groups = {Create.class, Update.class})
    private BigDecimal totalPrice;

    private String status;

    @NotNull(message = "The items field cannot be null", groups = {OrderItemDto.Create.class, OrderItemDto.Update.class})
    private List<OrderItemDto> items;

    @NotNull(message = "The createUser field cannot be null", groups = Create.class)
    @Null(message = "The createUser field must be null", groups = Update.class)
    private String createUser;

    @NotNull(message = "The updateUser field cannot be null", groups = Update.class)
    @Null(message = "The updateUser field must be null", groups = Create.class)
    private String updateUser;

    private UnifiedPaymentDto unifiedPaymentDto;
    private Long userId;



    public interface Create {}
    public interface Update {}
}
