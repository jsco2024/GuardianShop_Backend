package com.ms_security.ms_security.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "ENTRIES", schema = "ECOMERS_WITH_INVENTORY")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class EntriesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ENTRIES_ID", nullable = false)
    private Long id;

    @Column(name = "INVOICE_NUMBER", nullable = false)
    private String invoiceNumber;

    @Column(name = "QUANTITY", nullable = false)
    private Long quantity;

    @Column(name = "COST", nullable = false)
    private BigDecimal cost;

    @Column(name = "CREATE_USER")
    private String createUser;

    @Column(name = "UPDATE_USER")
    private String updateUser;

    @Column(name = "DATE_TIME_CREATION")
    private String dateTimeCreation;

    @Column(name = "DATE_TIME_UPDATE")
    private String dateTimeUpdate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INVENTORY_ID", referencedColumnName = "INVENTORY_ID", insertable = false, updatable = false)
    private InventoryEntity product;

    @Column(name = "INVENTORY_ID", nullable = false)
    private Long productId;

    @Column(name = "consecutive", nullable = false, unique = true)
    private Long consecutive;
}
