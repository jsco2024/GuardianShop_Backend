package com.ms_security.ms_security.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CONTACT_FORM", schema = "ECOMERS_WITH_INVENTORY")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ContactFormEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONTACT_FORM_ID")
    private Long id;
    @Column(name = "NAME", nullable = false)
    private String name;
    @Column(name = "LAST_NAME", nullable = false)
    private String lastName;
    @Column(name = "PHONE", nullable = false)
    private Long phone;
    @Column(name = "EMAIL", nullable = false)
    private String email;
    @Column(name = "MESSAGE", nullable = false)
    private String message;
    @Column(name = "STATUS", nullable = false)
    private Boolean status;
    @Column(name = "DATE_TIME_RECEIVED")
    private String dateTimeReceived;


}
