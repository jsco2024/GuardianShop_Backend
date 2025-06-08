package com.ms_security.ms_security.service.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExitsDto implements Serializable {

    @NotNull(message = "The id field cannot be null", groups = Update.class)
    @Null(message = "The id field must be null", groups = Create.class)
    @Min(value = 1, message = "The minimum value for id is 1")
    private Long id;

    @NotNull(message = "The orderNumber field cannot be null", groups = {Create.class, Update.class})
    private Long orderNumber;

    @NotNull(message = "The quantity field cannot be null", groups = {Create.class, Update.class})
    private Long quantity;

    @NotNull(message = "The cost field cannot be null", groups = {EntriesDto.Create.class, EntriesDto.Update.class})
    private BigDecimal cost;

    @NotNull(message = "The ProductId field cannot be null", groups = {InventoryDto.Create.class, InventoryDto.Update.class})
    private Long ProductId;

    @NotNull(message = "The createUser field cannot be null", groups = Create.class)
    @Null(message = "The createUser field must be null", groups = Update.class)
    private String createUser;

    @NotNull(message = "The updateUser field cannot be null", groups = Update.class)
    @Null(message = "The updateUser field must be null", groups = Create.class)
    private String updateUser;

    public interface Create {}
    public interface Update {}
}
