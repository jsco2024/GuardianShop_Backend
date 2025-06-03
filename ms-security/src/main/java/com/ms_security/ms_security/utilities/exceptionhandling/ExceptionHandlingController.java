package com.ms_security.ms_security.utilities.exceptionhandling;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.ms_security.ms_security.utilities.ErrorControlUtilities;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionHandlingController {

    private final ErrorControlUtilities _errorControlUtilities;

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> argumentNotValid(ConstraintViolationException e) {
        log.error("VALIDATION TYPE ERROR: {}", e.getMessage());
        return _errorControlUtilities.handleGeneral(null, 2L);
    }

    @ExceptionHandler(UnrecognizedPropertyException.class)
    public ResponseEntity<String> propertiesException(UnrecognizedPropertyException e) {
        log.error("UNRECOGNIZED PROPERTY ERROR: {}", e.getMessage());
        return _errorControlUtilities.handleGeneral(null, 2L);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> validationException(ValidationException e) {
        log.error("VALIDATION ERROR: {}", e.getMessage());
        return _errorControlUtilities.handleGeneral(null, 2L);
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<String> base64NoValid(JsonProcessingException e) {
        log.error("JSON PROCESSING ERROR: {}", e.getMessage());
        return _errorControlUtilities.handleGeneral(null, 98L);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> base64NoValid(IllegalArgumentException e) {
        log.error("ILLEGAL ARGUMENT ERROR: {}", e.getMessage());
        return _errorControlUtilities.handleGeneral(null, 98L);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> generalHandler(Exception e) {
        log.error("GENERAL ERROR: {}", e.getMessage());
        return _errorControlUtilities.handleGeneral(null, 99L);
    }
}
