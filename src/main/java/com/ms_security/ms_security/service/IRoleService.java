package com.ms_security.ms_security.service;

import org.springframework.http.ResponseEntity;

/**
 * Interface for managing roles.
 * Provides methods for retrieving, adding, updating roles, and finding roles with associated permissions.
 */
public interface IRoleService {

    /**
     * Retrieves a role by its ID.
     *
     * @param encode the encoded ID of the role
     * @return a ResponseEntity containing the role details or an error message
     */
    ResponseEntity<String> findById(String encode);

    /**
     * Retrieves all roles.
     *
     * @param encode the encoded parameter for retrieval
     * @return a ResponseEntity containing a list of all roles or an error message
     */
    ResponseEntity<String> findAll(String encode);

    /**
     * Adds a new role.
     *
     * @param encode the encoded details of the role to be added
     * @return a ResponseEntity indicating the success or failure of the addition
     */
    ResponseEntity<String> addNew(String encode);

    /**
     * Updates an existing role.
     *
     * @param encode the encoded details of the role to be updated
     * @return a ResponseEntity indicating the success or failure of the update
     */
    ResponseEntity<String> updateData(String encode);

    /**
     * Retrieves a role along with its associated permissions by role ID.
     *
     * @param encode the encoded ID of the role
     * @return a ResponseEntity containing the role and its permissions or an error message
     */
    ResponseEntity<String> findRoleWithPermissionById(String encode);
}
