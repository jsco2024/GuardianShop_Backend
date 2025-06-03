package com.ms_security.ms_security.service;

import com.ms_security.ms_security.persistence.entity.ParametersEntity;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface IParametersService {
    ResponseEntity<String> getParametersEntityList();
   Optional<ParametersEntity> findByCodeParameter(Long data);
    ResponseEntity<String> createParameter(String data);
    ResponseEntity<String> updateParameter(String data);
    ResponseEntity<String> deleteParameter(String data);
}