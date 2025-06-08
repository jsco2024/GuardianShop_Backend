package com.ms_security.ms_security.service.model.dto;

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
public class ServicesDto implements Serializable {
    @NotNull(message = "The id field cannot be null", groups = Update.class)
    @Null(message = "The id field must be null", groups = Create.class)
    @Min(value = 1, message = "The minimum value for id is 1")
    private Long id;

    @NotNull(message = "The code field cannot be null", groups = {Create.class, Update.class})
    private Long code;

    @NotNull(message = "The name field cannot be null", groups = {Create.class, Update.class})
    private String name;

    @NotNull(message = "The description field cannot be null", groups = {Create.class, Update.class})
    private String description;

    @NotNull(message = "The imageUrl field cannot be null", groups = {Create.class, Update.class})
    private String imageUrl;

    private BigDecimal salePrice;

    @NotNull(message = "The status field cannot be null", groups = {Create.class, Update.class})
    private Boolean status;

    @NotNull(message = "The status field cannot be null", groups = {Create.class, Update.class})
    private Long categoryId;

    @NotNull(message = "The createUser field cannot be null", groups = Create.class)
    @Null(message = "The createUser field must be null", groups = Update.class)
    private String createUser;

    @NotNull(message = "The updateUser field cannot be null", groups = Update.class)
    @Null(message = "The updateUser field must be null", groups = Create.class)
    private String updateUser;

    private List<InventoryDto> inventories;

    public interface Create {}
    public interface Update {}
}
