package com.ms_security.ms_security.service.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class UserDto implements Serializable {

    @NotNull(message = "The id field cannot be null", groups = Update.class)
    @Null(message = "The id field must be null", groups = Create.class)
    @Min(value = 1, message = "The minimum value for id is 1")
    private Long id;

    @NotNull(message = "The name field cannot be null", groups = {Create.class, Update.class})
    private String name;

    @NotNull(message = "The lastName field cannot be null", groups = {Create.class, Update.class})
    private String lastName;

    @NotNull(message = "The userName field cannot be null", groups = {Create.class, Update.class})
    private String userName;

    @NotNull(message = "The email field cannot be null", groups = {Create.class, Update.class})
    @Email(message = "The email field must be a valid email address")
    private String email;

    @NotNull(message = "The password field cannot be null", groups = Create.class)
    @Null(message = "The createUser field must be null", groups = Update.class)
    private String password;

    @NotNull(message = "The status field cannot be null", groups = {Create.class, Update.class})
    private Boolean status;

    @NotNull(message = "The createUser field cannot be null", groups = Create.class)
    @Null(message = "The createUser field must be null", groups = Update.class)
    private String createUser;

    @NotNull(message = "The updateUser field cannot be null", groups = Update.class)
    @Null(message = "The updateUser field must be null", groups = Create.class)
    private String updateUser;

    private Set<Long> rolesToAdd;
    private Set<Long> rolesToRemove;
    private Set<String> roles;

    public interface Create {}
    public interface Update {}
}
