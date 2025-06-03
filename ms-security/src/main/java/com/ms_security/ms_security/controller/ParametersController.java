package com.ms_security.ms_security.controller;

import com.ms_security.ms_security.persistence.entity.ParametersEntity;
import com.ms_security.ms_security.service.IParametersService;
import com.ms_security.ms_security.service.model.dto.ParametersDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/parameters")
@RequiredArgsConstructor
public class ParametersController {

    private final IParametersService _parametersService;

    @PostMapping("/list/all")
    public ResponseEntity<String> getParametersEntityList() {
        return _parametersService.getParametersEntityList();
    }

    @PostMapping("/code")
    public Optional<ParametersEntity> findByCodeParameter(@RequestBody ParametersDTO parametersDTO) {
        return _parametersService.findByCodeParameter(parametersDTO.getCodeParameter());
    }

    @PostMapping("/create")
    public ResponseEntity<String> createParameter(@RequestBody String data) {
        return _parametersService.createParameter(data);
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateParameter(@RequestBody String data) {
        return _parametersService.updateParameter(data);
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteParameter(@RequestBody String data) {
        return _parametersService.deleteParameter(data);
    }
}
