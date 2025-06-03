package com.ms_security.ms_security.service.impl;

import com.ms_security.ms_security.persistence.entity.ParametersEntity;
import com.ms_security.ms_security.persistence.entity.PermissionEntity;
import com.ms_security.ms_security.persistence.entity.RoleEntity;
import com.ms_security.ms_security.service.IParametersService;
import com.ms_security.ms_security.service.IPermissionService;
import com.ms_security.ms_security.service.impl.consultations.PermissionConsultations;
import com.ms_security.ms_security.service.impl.consultations.RoleConsultations;
import com.ms_security.ms_security.service.model.dto.FindByIdDto;
import com.ms_security.ms_security.service.model.dto.FindByPageDto;
import com.ms_security.ms_security.service.model.dto.PermissionDto;
import com.ms_security.ms_security.utilities.EncoderUtilities;
import com.ms_security.ms_security.utilities.ErrorControlUtilities;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Service
public class PermissionImpl implements IPermissionService {

    private final PermissionConsultations _permissionConsultations;
    private final RoleConsultations _roleConsultations;
    private final IParametersService _iParametersService;
    private final ErrorControlUtilities _errorControlUtilities;

    /**
     * Method responsible for searching for a record by its ID.
     *
     * @param encode Base64 encoded request containing the ID of the record to be retrieved.
     * @return A ResponseEntity object containing the requested data or an error message.
     */
    @Override
    public ResponseEntity<String> findById(String encode) {
        log.info("PROCEED TO SEARCH BY ID");
        EncoderUtilities.validateBase64(encode);
        PermissionDto findByIdDto = EncoderUtilities.decodeRequest(encode, PermissionDto.class);
        EncoderUtilities.validator(findByIdDto);
        log.info(EncoderUtilities.formatJson(findByIdDto));
        Optional<PermissionEntity> permissionEntity = _permissionConsultations.findById(findByIdDto.getId());
        if(permissionEntity.isEmpty()) return _errorControlUtilities.handleSuccess(null, 3L);
        PermissionEntity permission = permissionEntity.get();
        PermissionDto permissionDto = parse(permission);
        log.info("SEARCH BY ID IS ENDED");
        return _errorControlUtilities.handleSuccess(permissionDto, 1L);
    }

    /**
     * Method responsible for retrieving all records with pagination.
     *
     * @param encode Base64 encoded request containing pagination information.
     * @return A ResponseEntity object containing a paginated list of records or an error message.
     */
    @Override
    public ResponseEntity<String> findAll(String encode) {
        EncoderUtilities.validateBase64(encode);
        log.info("INITIATING PAGINATED SEARCH");
        FindByPageDto request = EncoderUtilities.decodeRequest(encode, FindByPageDto.class);
        EncoderUtilities.validator(request);
        log.info(EncoderUtilities.formatJson(request));
        log.info("INITIATING PARAMETER QUERY");
        Optional<ParametersEntity> pageSizeParam = _iParametersService.findByCodeParameter(1L);
        log.info("PARAMETER QUERY COMPLETED");
        Pageable pageable = PageRequest.of(
                request.getPage() - 1,
                Integer.parseInt(pageSizeParam.get().getParameter()));
        Page<PermissionEntity> pageResult = _permissionConsultations.findAll(pageable);
        List<PermissionDto> permissionDto = pageResult.stream()
                .map(this::parse)
                .toList();
        PageImpl<PermissionDto> response = new PageImpl<>(permissionDto, pageable, pageResult.getTotalElements());
        log.info("PAGINATED SEARCH COMPLETED");
        return _errorControlUtilities.handleSuccess(response, 1L);
    }


    /**
     * Method responsible for creating a new record.
     *
     * @param encode Base64 encoded request containing the details of the record to be created.
     * @return A ResponseEntity object containing the created record or an error message.
     */
    @Override
    public ResponseEntity<String> addNew(String encode) {
        EncoderUtilities.validateBase64(encode);
        log.info("INSERT BEGINS");
        PermissionDto permissionDto = EncoderUtilities.decodeRequest(encode, PermissionDto.class);
        EncoderUtilities.validator(permissionDto, PermissionDto.Create.class);
        log.info(EncoderUtilities.formatJson(permissionDto));
        log.info("START SEARCH BY NAME");
        Optional<PermissionEntity> name = _permissionConsultations.findByName(permissionDto.getName());
        if (name.isPresent()) return _errorControlUtilities.handleSuccess(null, 4L);
        log.info("END SEARCH BY NAME");
        PermissionEntity existingEntity = parseEnt(permissionDto, new PermissionEntity());
        existingEntity.setCreateUser(permissionDto.getCreateUser());
        existingEntity.setDateTimeCreation(new Date().toString());
        PermissionEntity permissionEntity = _permissionConsultations.addNew(existingEntity);
        PermissionDto permissionDtos = parse(permissionEntity);
        log.info("INSERT ENDED");
        return _errorControlUtilities.handleSuccess(permissionDtos, 1L);
    }

    /**
     * Method responsible for updating an existing record.
     *
     * @param encode Base64 encoded request containing the details of the record to be updated.
     * @return A ResponseEntity object containing the updated record or an error message.
     */
    @Override
    public ResponseEntity<String> updateData(String encode) {
        EncoderUtilities.validateBase64(encode);
        log.info("INSERT BEGINS");
        PermissionDto permissionDto = EncoderUtilities.decodeRequest(encode, PermissionDto.class);
        EncoderUtilities.validator(permissionDto, PermissionDto.Update.class);
        log.info(EncoderUtilities.formatJson(permissionDto));
        log.info("START SEARCH BY ID");
        Optional<PermissionEntity> permission = _permissionConsultations.findById(permissionDto.getId());
        if (permission.isEmpty()) return _errorControlUtilities.handleSuccess(null, 3L);
        log.info("START SEARCH BY ID");
        log.info("START SEARCH BY NAME");
        PermissionEntity permissionEntity = permission.get();
        if (!permissionEntity.getName().equals(permissionDto.getName())) return _errorControlUtilities.handleSuccess(null, 5L);
        log.info("END SEARCH BY NAME");
        PermissionEntity existingEntity = parseEnt(permissionDto, new PermissionEntity());
        existingEntity.setUpdateUser(permissionDto.getUpdateUser());
        existingEntity.setDateTimeUpdate(new Date().toString());
        PermissionEntity permissionEntities = _permissionConsultations.updateData(existingEntity);
        PermissionDto permissionDtos = parse(permissionEntities);
        log.info("INSERT ENDED");
        return _errorControlUtilities.handleSuccess(permissionDtos, 1L);
    }

    @Override
    public ResponseEntity<String> findByRoleId(String encode) {
        EncoderUtilities.validateBase64(encode);
        log.info("PROCEED TO SEARCH BY ROLE ID");
        FindByIdDto roleId = EncoderUtilities.decodeRequest(encode, FindByIdDto.class);
        Optional<RoleEntity> roleEntity = _roleConsultations.findRoleWithPermissionById(roleId.getId());
        if (roleEntity.isEmpty()) {
            return _errorControlUtilities.handleSuccess(null, 3L);
        }
        Set<PermissionEntity> permissions = roleEntity.get().getPermissions();
        List<PermissionDto> permissionDto = permissions.stream()
                .map(this::parse)
                .collect(Collectors.toList());
        log.info("SEARCH BY ROLE ID IS ENDED");
        return _errorControlUtilities.handleSuccess(permissionDto, 1L);
    }

    /**
     * Converts a PermissionEntity object to a PermissionDto object.
     *
     * @param entity The PermissionEntity object to be converted.
     * @return The corresponding PermissionDto object.
     */
    private PermissionDto parse(PermissionEntity entity){
        PermissionDto permissionDto = new PermissionDto();
        permissionDto.setId(entity.getId());
        permissionDto.setName(entity.getName());
        permissionDto.setUrl(entity.getUrl());
        permissionDto.setMethod(entity.getMethod());
        permissionDto.setMenuItem(entity.getMenuItem());
        permissionDto.setStatus(entity.getStatus());
        permissionDto.setCreateUser(entity.getCreateUser());
        permissionDto.setUpdateUser(entity.getUpdateUser());
        return permissionDto;
    }

    /**
     * Converts a PermissionDto object to a PermissionEntity object.
     *
     * @param dto The PermissionDto object to be converted.
     * @param entity The PermissionEntity object to be updated with values from the DTO.
     * @return The updated PermissionEntity object.
     */
    private PermissionEntity parseEnt(PermissionDto dto, PermissionEntity entity){
        PermissionEntity permissionEntity = new PermissionEntity();
        permissionEntity.setId(dto.getId());
        permissionEntity.setName(dto.getName());
        permissionEntity.setMenuItem(dto.getMenuItem());
        permissionEntity.setUrl(dto.getUrl());
        permissionEntity.setMethod(dto.getMethod());
        permissionEntity.setStatus(dto.getStatus());
        permissionEntity.setCreateUser(entity.getCreateUser());
        permissionEntity.setUpdateUser(entity.getUpdateUser());
        permissionEntity.setDateTimeCreation(entity.getDateTimeCreation());
        permissionEntity.setDateTimeUpdate(entity.getDateTimeCreation());
        return permissionEntity;
    }
}
