package com.ms_security.ms_security.service;

import org.springframework.http.ResponseEntity;

/**
 * Interface for managing order item services.
 * Provides methods for retrieving, adding, and updating order items.
 */
public interface IOrderItemService {
    ResponseEntity<String> findById(String encode);
    ResponseEntity<String> findAll(String encode);
//    ResponseEntity<String> addNew(String encode);
    ResponseEntity<String> updateData(String encode);
    ResponseEntity<String> deleteById(String encode);
    ResponseEntity<String> findByCartIdAndCreateUser(String username, Long cartId);
    ResponseEntity<String> findByCartId(Long cartId, int page, int size);
}
