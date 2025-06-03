package com.ms_security.ms_security.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "INVENTORY", schema = "ECOMERS_WITH_INVENTORY")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class InventoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INVENTORY_ID")
    private Long id;

    @Column(name = "PRODUCT_CODE")
    private String productCode;

    @Column(name = "PRODUCT_NAME")
    private String name;

    @Column(name = "REFERENCE")
    private String reference;

    @Column(name = "UNIT_OF_MEASURE")
    private String unitOfMeasure;

    @Column(name = "INITIAL_STOCKS")
    private Long initialStock;

    @Column(name = "STOCK")
    private Long stock;

    @Column(name = "PENDING_STOCK")
    private Long pendingStock;

    @Column(name = "CANCELED_STOCK")
    private Long canceledStock;

    @Column(name = "RETURNED_STOCK")
    private Long returnedStock;

    @Column(name = "ADJUSTED_STOCK")
    private Long adjustedStock;

    @Column(name = "SALE_PRICE")
    private BigDecimal salePrice;

    @Column(name = "COST")
    private BigDecimal cost;

    @Column(name = "AVERAGE_COST")
    private BigDecimal averageCost;

    @Column(name = "TOTAL_COST")
    private BigDecimal totalCost;

    @Column(name = "STATUS")
    private Boolean status;

    @Column(name = "LAST_ENTRY_DATE")
    private String lastEntryDate;

    @Column(name = "LAST_EXIT_DATE")
    private String lastExitDate;

    @Column(name = "CREATE_USER")
    private String createUser;

    @Column(name = "UPDATE_USER")
    private String updateUser;

    @Column(name = "DATE_TIME_CREATION")
    private String dateTimeCreation;

    @Column(name = "DATE_TIME_UPDATE")
    private String dateTimeUpdate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SERVICE_ID", insertable = false, updatable = false)
    @JsonIgnore
    private ServicesEntity service;

    @Column(name = "SERVICE_ID")
    private Long serviceId;

    @OneToMany(mappedBy = "product", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<EntriesEntity> entries;

    @OneToMany(mappedBy = "product", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<ExitsEntity> exits;

    @Column(name = "MONTHLY_CLODING_DATE")
    private String monthlyClosingDate;

    @Column(name = "CLOSED_QUANTITY")
    private Long closedQuantity;
}
