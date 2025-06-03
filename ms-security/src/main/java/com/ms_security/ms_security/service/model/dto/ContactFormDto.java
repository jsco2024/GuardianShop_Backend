package com.ms_security.ms_security.service.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ContactFormDto implements Serializable {

    @NotNull(message = "The id field cannot be null", groups = Update.class)
    @Null(message = "The id field must be null", groups = Create.class)
    @Min(value = 1, message = "The minimum value for id is 1")
    private Long id;

    @NotNull(message = "The name field cannot be null", groups = {Create.class, Update.class})
    private String name;

    @NotNull(message = "The lastName field cannot be null", groups = {Create.class, Update.class})
    private String lastName;

    @NotNull(message = "The phone field cannot be null", groups = {Create.class, Update.class})
    @Min(value = 1, message = "The phone field must be a valid number")
    private Long phone;

    @NotNull(message = "The email field cannot be null", groups = Create.class)
    @Email(message = "The email field must be a valid email address")
    private String email;

    @NotNull(message = "The message field cannot be null", groups = {Create.class, Update.class})
    private String message;

    @NotNull(message = "The status field cannot be null", groups = {Create.class, Update.class})
    private Boolean status;

    public interface Create {}
    public interface Update {}
}
