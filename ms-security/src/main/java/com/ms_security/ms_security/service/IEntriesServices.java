package com.ms_security.ms_security.service;

import org.springframework.http.ResponseEntity;

public interface IEntriesServices {
    ResponseEntity<String> findById(String encode);

    ResponseEntity<String> findAll(String encode);

    ResponseEntity<String> addNew(String encode);

    ResponseEntity<String> updateData(String encode);
    void returnToStock(Long productId, Long quantity, String updateUser);

}
