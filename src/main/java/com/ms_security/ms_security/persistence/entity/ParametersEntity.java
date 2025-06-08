package com.ms_security.ms_security.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "parameters", schema = "railway")
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

    @Column(name = "CREATE_USER")
    private String userCreation;

    @Column(name = "UPDATE_USER")
    private String userUpdate;

    @Column(name = "DATE_TIME_CREATION")
    private String dateCreation;

    @Column(name = "DATE_TIME_UPDATE")
    private String dateUpdate;
}
