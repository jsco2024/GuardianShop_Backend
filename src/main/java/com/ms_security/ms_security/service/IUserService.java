package com.ms_security.ms_security.service;

import org.springframework.http.ResponseEntity;

/**
 * Interface for managing users.
 * Provides methods for retrieving, adding, updating users, and fetching user roles.
 */
public interface IUserService {

    /**
     * Retrieves a user by their ID.
     *
     * @param encode the encoded ID of the user
     * @return a ResponseEntity containing the user details or an error message
     */
    ResponseEntity<String> findById(String encode);

    /**
     * Retrieves all users.
     *
     * @param encode the encoded parameter for retrieval
     * @return a ResponseEntity containing a list of all users or an error message
     */
    ResponseEntity<String> findAll(String encode);

    /**
     * Adds a new user.
     *
     * @param encode the encoded details of the user to be added
     * @return a ResponseEntity indicating the success or failure of the addition
     */
    ResponseEntity<String> addNew(String encode);

    /**
     * Updates an existing user.
     *
     * @param encode the encoded details of the user to be updated
     * @return a ResponseEntity indicating the success or failure of the update
     */
    ResponseEntity<String> updateData(String encode);

    /**
     * Retrieves a user along with their associated roles by user ID.
     *
     * @param encode the encoded ID of the user
     * @return a ResponseEntity containing the user details along with their roles or an error message
     */
    ResponseEntity<String> findUserWithRolesById(String encode);
}
