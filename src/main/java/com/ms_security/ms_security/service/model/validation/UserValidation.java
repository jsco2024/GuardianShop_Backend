package com.ms_security.ms_security.service.model.validation;


import com.ms_security.ms_security.persistence.entity.UserEntity;
import com.ms_security.ms_security.service.model.dto.ResponseErrorDto;

/**
 * Class for validating user data.
 * Validates fields of a UserEntity object such as name, last name, email, and password against specified criteria.
 */
public class UserValidation {
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final String PASSWORD_REGEX = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W_]).{8,16}$";

    /**
     * Validates the fields of a UserEntity object.
     * Checks if the user's name, last name, email, and password meet the specified criteria.
     *
     * @param user the UserEntity object to validate
     * @return a ResponseDto containing the number of errors and error messages
     */
    public ResponseErrorDto validate(UserEntity user) {
        ResponseErrorDto responseDto = new ResponseErrorDto();
        responseDto.setNumOfErrors(0);
        StringBuilder errorMessage = new StringBuilder();
        if (isInvalidName(user.getName())) {
            responseDto.setNumOfErrors(responseDto.getNumOfErrors() + 1);
            errorMessage.append("The name must be between 3 and 15 characters.\n");
        }
        if (isInvalidLastName(user.getLastName())) {
            responseDto.setNumOfErrors(responseDto.getNumOfErrors() + 1);
            errorMessage.append("The last name must be between 3 and 30 characters.\n");
        }
        if (isInvalidEmail(user.getEmail())) {
            responseDto.setNumOfErrors(responseDto.getNumOfErrors() + 1);
            errorMessage.append("Wrong email format.\n");
        }
        if (isInvalidPassword(user.getPassword())) {
            responseDto.setNumOfErrors(responseDto.getNumOfErrors() + 1);
            errorMessage.append("The password must be between 8 and 16 characters, with at least one number, one lowercase letter, one uppercase letter, and one special character.");
        }
        responseDto.setMessage(errorMessage.toString().trim());
        return responseDto;
    }

    /**
     * Checks if the user's name is invalid.
     * A valid name must be between 3 and 15 characters long.
     *
     * @param name the name to check
     * @return true if the name is invalid, false otherwise
     */
    private boolean isInvalidName(String name) {
        return name == null || name.length() < 3 || name.length() > 50;
    }

    /**
     * Checks if the user's last name is invalid.
     * A valid last name must be between 3 and 30 characters long.
     *
     * @param lastName the last name to check
     * @return true if the last name is invalid, false otherwise
     */
    private boolean isInvalidLastName(String lastName) {
        return lastName == null || lastName.length() < 3 || lastName.length() > 30;
    }

    /**
     * Checks if the user's email is invalid.
     * A valid email must match the specified email regex pattern.
     *
     * @param email the email to check
     * @return true if the email is invalid, false otherwise
     */
    private boolean isInvalidEmail(String email) {
        return email == null || !email.matches(EMAIL_REGEX);
    }

    /**
     * Checks if the user's password is invalid.
     * A valid password must match the specified password regex pattern.
     *
     * @param password the password to check
     * @return true if the password is invalid, false otherwise
     */
    private boolean isInvalidPassword(String password) {
        return password == null || !password.matches(PASSWORD_REGEX);
    }
}
