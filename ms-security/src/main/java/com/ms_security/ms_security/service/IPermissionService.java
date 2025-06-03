package com.ms_security.ms_security.service;

import com.ms_security.ms_security.service.model.dto.PermissionDto;
import org.springframework.http.ResponseEntity;

import java.util.Set;

/**
 * Interface for managing permissions.
 * Provides methods for retrieving, adding, and updating permissions.
 */
public interface IPermissionService {

    /**
     * Retrieves a permission by its ID.
     *
     * @param encode the encoded ID of the permission
     * @return a ResponseEntity containing the permission details or an error message
     */
    ResponseEntity<String> findById(String encode);

    /**
     * Retrieves all permissions.
     *
     * @param encode the encoded parameter for retrieval
     * @return a ResponseEntity containing a list of all permissions or an error message
     */
    ResponseEntity<String> findAll(String encode);

    /**
     * Adds a new permission.
     *
     * @param encode the encoded details of the permission to be added
     * @return a ResponseEntity indicating the success or failure of the addition
     */
    ResponseEntity<String> addNew(String encode);

    /**
     * Updates an existing permission.
     *
     * @param encode the encoded details of the permission to be updated
     * @return a ResponseEntity indicating the success or failure of the update
     */
    ResponseEntity<String> updateData(String encode);

    public ResponseEntity<String> findByRoleId(String encode);
}
