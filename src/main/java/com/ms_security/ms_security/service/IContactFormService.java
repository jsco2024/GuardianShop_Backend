package com.ms_security.ms_security.service;

import org.springframework.http.ResponseEntity;

/**
 * Interface for services related to contact forms.
 * Provides methods for managing contact form data.
 */
public interface IContactFormService {
    /**
     * Retrieves a contact form by its ID.
     *
     * @param encode the encoded ID of the contact form to retrieve
     * @return a ResponseEntity containing the contact form data in JSON format or an error message
     */
    ResponseEntity<String> findById(String encode);

    /**
     * Retrieves all contact forms.
     *
     * @param encode an optional parameter for encoding or filtering the results
     * @return a ResponseEntity containing a list of contact forms in JSON format or an error message
     */
    ResponseEntity<String> findAll(String encode);

    /**
     * Adds a new contact form.
     *
     * @param encode the encoded contact form data to add
     * @return a ResponseEntity indicating the result of the operation, such as success or error message
     */
    ResponseEntity<String> addNew(String encode);

    /**
     * Updates an existing contact form.
     *
     * @param encode the encoded contact form data to update
     * @return a ResponseEntity indicating the result of the operation, such as success or error message
     */
    ResponseEntity<String> updateData(String encode);


}
