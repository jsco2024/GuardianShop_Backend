package com.ms_security.ms_security.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "ORDERS", schema = "ECOMERS_WITH_INVENTORY")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_ID", nullable = false)
    private Long id;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_NUMBER", unique = true, nullable = false)
    private Long orderNumber;

    @Column(name = "PRODUCT_NAME", nullable = false)
    private String productName;

    @Column(name = "QUANTITY", nullable = false)
    private Long quantity;

    @Column(name = "UNIT_PRICE", nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "TOTAL_PRICE", nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "STATUS", nullable = false)
    private String status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonIgnore
    private List<OrderItemEntity> items;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", insertable = false, updatable = false)
    @JsonIgnore
    private UserEntity user;

    @Column(name = "USER_ID", nullable = false)
    private Long userId;

    @Column(name = "CREATE_USER")
    private String createUser;

    @Column(name = "UPDATE_USER")
    private String updateUser;

    @Column(name = "DATE_TIME_CREATION")
    private String dateTimeCreation;

    @Column(name = "DATE_TIME_UPDATE")
    private String dateTimeUpdate;

    @Column(name = "DATE_TIME_ORDER")
    private String dateTimeOrder;

}
