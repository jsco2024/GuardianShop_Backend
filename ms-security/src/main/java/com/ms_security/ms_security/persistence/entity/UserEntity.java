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
@Table(name = "USERS", schema = "ECOMERS_WITH_INVENTORY")
@Entity
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID", nullable = false)
    private Long id;
    @Column(name = "NAME", nullable = false)
    private String name;
    @Column(name = "LAST_NAME", nullable = false)
    private String lastName;
    @Column(name = "USER_NAME", nullable = false)
    private String userName;
    @Column(name = "EMAIL", nullable = false)
    private String email;
    @Column(name = "PASSWORD", nullable = false)
    private String password;
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
            name = "ROLE_USER",
            joinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID", referencedColumnName = "ROLE_ID")
    )
    @JsonIgnore
    private Set<RoleEntity> roles = new HashSet<>();
}
