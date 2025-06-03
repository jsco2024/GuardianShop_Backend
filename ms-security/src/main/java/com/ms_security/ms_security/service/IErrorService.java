package com.ms_security.ms_security.service;

import com.ms_security.ms_security.service.model.dto.ErrorResponseDto;

import java.util.List;

/**
 * Interface for services related to error handling.
 * Provides methods for retrieving error details based on error IDs.
 */
public interface IErrorService {

    /**
     * Retrieves details of an error based on its ID.
     *
     * @param errorId the ID of the error to retrieve
     * @return an ErrorResponseDto containing details about the error
     */
    public ErrorResponseDto findByErrorId(String errorId);

    /**
     * Retrieves details of all errors.
     *
     * @return a List of ErrorResponseDto containing details about all errors
     */
    List<ErrorResponseDto> findAllErrors();
}
