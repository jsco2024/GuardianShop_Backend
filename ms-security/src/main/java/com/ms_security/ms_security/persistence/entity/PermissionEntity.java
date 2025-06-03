package com.ms_security.ms_security.persistence.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Table(name = "PERMISSION", schema = "ECOMERS_WITH_INVENTORY")
@Entity
public class PermissionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PERMISSION_ID", nullable = false)
    private Long id;
    @Column(name = "NAME_PERMISSION", nullable = false)
    private String name;
    @Column(name = "URL", nullable = false)
    private String url;
    @Column(name = "METHOD", nullable = false)
    private String method;
    @Column(name = "MENU_ITEM", nullable = false)
    private String menuItem;
    @Column(name = "STATUS", nullable = false)
    private Boolean status;
    @Column(name = "CREATE_USER")
    private String createUser;
    @Column(name = "UPDATE_USER")
    private String updateUser;
    @Column(name = "DATE_TIME_CREATION")
    private String dateTimeCreation;
    @Column(name = "DATE_TIME_UPDATE")
    private String dateTimeUpdate;


}
