package com.ms_security.ms_security.service.model.dto;

import com.ms_security.ms_security.persistence.entity.EntriesEntity;
import com.ms_security.ms_security.persistence.entity.ExitsEntity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class InventoryDto implements Serializable {
    @NotNull(message = "The id field cannot be null", groups = Update.class)
    @Null(message = "The id field must be null", groups = Create.class)
    @Min(value = 1, message = "The minimum value for id is 1")
    private Long id;

    @NotNull(message = "The productCode field cannot be null", groups = {Create.class, Update.class})
    private String productCode;

    private String name;

    @NotNull(message = "The reference field cannot be null", groups = {Create.class, Update.class})
    private String reference;

    @NotNull(message = "The unitOfMeasure field cannot be null", groups = {Create.class, Update.class})
    private String unitOfMeasure;

    @Null(message = "The initialStock field cannot be null", groups = Update.class)
    private Long initialStock;

    public Long getInitialStock() {
        return initialStock != null ? initialStock : 0L;
    }

    @Null(message = "The stock field cannot be null", groups = Update.class)
    private Long stock;

    @Null(message = "The pendingStock field cannot be null", groups = Update.class)
    private Long pendingStock;

    @Null(message = "The canceledStock field cannot be null", groups = Update.class)
    private Long canceledStock;

    @Null(message = "The returnedStock field cannot be null", groups = Update.class)
    private Long returnedStock;

    @Null(message = "The adjustedStock field cannot be null", groups = Update.class)
    private Long adjustedStock;

    @Null(message = "The salePrice field cannot be null", groups = Update.class)
    private BigDecimal salePrice;

    @Null(message = "The cost field cannot be null", groups = Update.class)
    private BigDecimal cost;

    @Null(message = "The averageCost field cannot be null", groups = Update.class)
    private BigDecimal averageCost;

    @Null(message = "The totalCost field cannot be null", groups = Update.class)
    private BigDecimal totalCost;

    @NotNull(message = "The status field cannot be null", groups = {Create.class, Update.class})
    private Boolean status;

    @NotNull(message = "The serviceId field cannot be null", groups = {Create.class, Update.class})
    private Long serviceId;

    @NotNull(message = "The createUser field cannot be null", groups = Create.class)
    @Null(message = "The createUser field must be null", groups = Update.class)
    private String createUser;

    @NotNull(message = "The updateUser field cannot be null", groups = Update.class)
    @Null(message = "The updateUser field must be null", groups = Create.class)
    private String updateUser;

    private String lastEntryDate;
    private String lastExitDate;

    private List<EntriesEntity> entries;
    private List<ExitsEntity> exits;

    public interface Create {}
    public interface Update {}
}
