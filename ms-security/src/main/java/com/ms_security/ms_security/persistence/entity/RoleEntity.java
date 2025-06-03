package com.ms_security.ms_security.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Table(name = "ROLE", schema = "ECOMERS_WITH_INVENTORY")
@Entity
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ROLE_ID", nullable = false)
    private Long id;
    @Column(name = "NAME_ROLE", nullable = false)
    private String name;
    @Column(name = "DESCRIPTION", nullable = false)
    private String description;
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
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "ROLE_PERMISSION",
            joinColumns = @JoinColumn(name = "ROLE_ID", referencedColumnName = "ROLE_ID"),
            inverseJoinColumns = @JoinColumn(name = "PERMISSION_ID", referencedColumnName = "PERMISSION_ID")
    )
    @JsonIgnore
    private Set<PermissionEntity> permissions = new HashSet<>();


}
