package com.ms_security.ms_security.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "ORDER_ITEM", schema = "ECOMERS_WITH_INVENTORY")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class OrderItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_ITEM_ID")
    private Long id;

    @Column(name = "PRODUCT_NAME")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CART_ID", insertable = false, updatable = false)
    @JsonIgnore
    private CartEntity cart;

    @Column(name = "CART_ID")
    private Long cartId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INVENTORY_ID", insertable = false, updatable = false)
    @JsonIgnore
    private InventoryEntity product;

    @Column(name = "INVENTORY_ID")
    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID", insertable = false, updatable = false)
    @JsonIgnore
    private OrderEntity order;

    @Column(name = "ORDER_ID")
    private Long orderId;

    @Column(name = "QUANTITY")
    private Long quantity;

    @Column(name = "PRICE")
    private BigDecimal price;

    @Column(name = "CREATE_USER")
    private String createUser;

    @Column(name = "UPDATE_USER")
    private String updateUser;

    @Column(name = "DATE_TIME_CREATION")
    private String dateTimeCreation;

    @Column(name = "DATE_TIME_UPDATE")
    private String dateTimeUpdate;


}
