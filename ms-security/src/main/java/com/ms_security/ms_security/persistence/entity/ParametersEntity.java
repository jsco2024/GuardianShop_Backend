package com.ms_security.ms_security.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "parameters")
@Data
public class ParametersEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "CODE_PARAMETER", nullable = false, unique = true)
    private Long codeParameter;

    @Column(name = "DESCRIPTION_PARAMETER")
    private String descriptionParameter;

    @Column(name = "PARAMETER")
    private String parameter;

    @Column(name = "USER_CREATION")
    private String userCreation;

    @Column(name = "USER_UPDATE")
    private String userUpdate;

    @Column(name = "DATE_CREATION")
    private String dateCreation;

    @Column(name = "DATE_UPDATE")
    private String dateUpdate;
}
