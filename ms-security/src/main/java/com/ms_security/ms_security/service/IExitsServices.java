package com.ms_security.ms_security.service;

import org.springframework.http.ResponseEntity;

public interface IExitsServices {
    ResponseEntity<String> findById(String encode);
    ResponseEntity<String> findAll(String encode);
    ResponseEntity<String> addNew(String encode);
    ResponseEntity<String> updateData(String encode);
    ResponseEntity<String> exitOnPayment(String encode);
}
