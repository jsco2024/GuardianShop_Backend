package com.ms_security.ms_security.service;

import org.springframework.http.ResponseEntity;

public interface ICategoryService {

    /**
     * Retrieves a service by its ID.
     *
     * @param encode the encoded ID of the service
     * @return a ResponseEntity containing the service details or an error message
     */
    ResponseEntity<String> findById(String encode);

    /**
     * Retrieves all services.
     *
     * @param encode the encoded parameter for retrieval
     * @return a ResponseEntity containing a list of all services or an error message
     */
    ResponseEntity<String> findAll(String encode);

    /**
     * Adds a new service.
     *
     * @param encode the encoded details of the service to be added
     * @return a ResponseEntity indicating the success or failure of the addition
     */
    ResponseEntity<String> addNew(String encode);

    /**
     * Updates an existing service.
     *
     * @param encode the encoded details of the service to be updated
     * @return a ResponseEntity indicating the success or failure of the update
     */
    ResponseEntity<String> updateData(String encode);
}
