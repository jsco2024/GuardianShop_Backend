package com.ms_security.ms_security.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms_security.ms_security.persistence.entity.ParametersEntity;
import com.ms_security.ms_security.service.IParametersService;
import com.ms_security.ms_security.service.impl.consultations.ParametersConsultations;
import com.ms_security.ms_security.service.model.dto.ParametersDTO;
import com.ms_security.ms_security.utilities.EncoderUtilities;
import com.ms_security.ms_security.utilities.ErrorControlUtilities;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ParametersImpl implements IParametersService {

    private final ParametersConsultations _parametersConsultations;
    private final Validator _validator;
    private final ErrorControlUtilities _errorControlUtilities;
    private final ObjectMapper modelMapper;

    @Override
    public ResponseEntity<String> getParametersEntityList() {
        List<ParametersEntity> parametersEntity = _parametersConsultations.findAll();
        List<ParametersDTO> response = parametersEntity.stream()
                .map(param -> modelMapper.convertValue(param, ParametersDTO.class))
                .toList();
        return _errorControlUtilities.handleSuccess(response, 1L);
    }

    @Override
    public Optional<ParametersEntity> findByCodeParameter(Long data) {
        ParametersDTO request = new ParametersDTO();
        request.setCodeParameter(data);
        Set<ConstraintViolation<ParametersDTO>> violation = _validator.validate(request, ParametersDTO.ParametersDTOFindByCode.class);
        if (!violation.isEmpty()) throw new ConstraintViolationException(violation);
        return _parametersConsultations.findByCodeParameter(request.getCodeParameter());
    }


    @Override
    public ResponseEntity<String> createParameter(String data) {
        EncoderUtilities.validateBase64(data);
        ParametersDTO param = EncoderUtilities.decodeRequest(data, ParametersDTO.class);
        Set<ConstraintViolation<ParametersDTO>> violation = _validator.validate(param);
        if (!violation.isEmpty()) throw new ConstraintViolationException(violation);
        ParametersEntity parametersEntity = refactorDTO(param);
        ParametersEntity response = _parametersConsultations.save(parametersEntity);
        return _errorControlUtilities.handleSuccess(response, 1L);
    }

    @Override
    public ResponseEntity<String> updateParameter(String data) {
        EncoderUtilities.validateBase64(data);
        ParametersDTO param = EncoderUtilities.decodeRequest(data, ParametersDTO.class);
        Set<ConstraintViolation<ParametersDTO>> violation = _validator.validate(param);
        if (!violation.isEmpty()) throw new ConstraintViolationException(violation);
        Optional<ParametersEntity> existingParameter = _parametersConsultations.findById(param.getId());
        if (existingParameter.isEmpty()) return _errorControlUtilities.handleGeneral(null, 3L);
        ParametersEntity returnParameter = existingParameter.get();
        returnParameter.setDescriptionParameter(param.getDescriptionParameter());
        returnParameter.setParameter(param.getParameter());
        returnParameter.setUserUpdate(param.getUserUpdate());
        _parametersConsultations.save(returnParameter);
        return _errorControlUtilities.handleSuccess(null, 1L);

    }

    @Override
    public ResponseEntity<String> deleteParameter(String data) {
        ParametersDTO request = EncoderUtilities.decodeRequest(data, ParametersDTO.class);
        Set<ConstraintViolation<ParametersDTO>> violation = _validator.validate(request, ParametersDTO.ParametersDTOFindByCode.class);
        if (!violation.isEmpty()) throw new ConstraintViolationException(violation);
        Optional<ParametersEntity> existingParameter = _parametersConsultations.findByCodeParameter(request.getCodeParameter());
        if (existingParameter.isPresent()) {
            _parametersConsultations.deleteByCodeParameter(request.getCodeParameter());
            return _errorControlUtilities.handleSuccess(null, 1L);
        }
        return _errorControlUtilities.handleGeneral(null, 3L);
    }

    private ParametersEntity refactorDTO(ParametersDTO parameters) {
        ParametersEntity response = new ParametersEntity();
        response.setCodeParameter(parameters.getCodeParameter());
        response.setDescriptionParameter(parameters.getDescriptionParameter());
        response.setParameter(parameters.getParameter());
        return response;
    }
}
