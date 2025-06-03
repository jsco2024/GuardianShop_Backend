package com.ms_security.ms_security.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "CART", schema = "ECOMERS_WITH_INVENTORY")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class CartEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CART_ID")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", insertable = false, updatable = false)
    @JsonIgnore
    private UserEntity user;

    @Column(name = "USER_ID", nullable = false)
    private Long userId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<OrderItemEntity> items;

    @Column(name = "reservation_time")
    private String reservationTime;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "CREATE_USER")
    private String createUser;

    @Column(name = "UPDATE_USER")
    private String updateUser;

    @Column(name = "DATE_TIME_CREATION")
    private String dateTimeCreation;

    @Column(name = "DATE_TIME_UPDATE")
    private String dateTimeUpdate;

}
