package com.ms_security.ms_security.service.model.dto;

import com.ms_security.ms_security.persistence.entity.InventoryEntity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class OrderItemDto implements Serializable {
    @NotNull(message = "The id field cannot be null", groups = Update.class)
    @Null(message = "The id field must be null", groups = Create.class)
    @Min(value = 1, message = "The minimum value for id is 1")
    private Long id;

    @NotNull(message = "The name field cannot be null", groups = {Create.class, Update.class})
    private String name;

    @NotNull(message = "The cartId field cannot be null", groups = {Create.class, Update.class})
    private Long cartId;

    @NotNull(message = "The productId field cannot be null", groups = {Create.class, Update.class})
    private Long productId;

    @NotNull(message = "The quantity field cannot be null", groups = {Create.class, Update.class})
    private Long quantity;

    @NotNull(message = "The price field cannot be null", groups = {Create.class, Update.class})
    private BigDecimal price;

    @NotNull(message = "The createUser field cannot be null", groups = Create.class)
    @Null(message = "The createUser field must be null", groups = Update.class)
    private String createUser;

    @NotNull(message = "The updateUser field cannot be null", groups = Update.class)
    @Null(message = "The updateUser field must be null", groups = Create.class)
    private String updateUser;

    private InventoryEntity product;

    public interface Create {}
    public interface Update {}
}
