package com.ms_security.ms_security.service.impl;

import com.ms_security.ms_security.persistence.entity.ParametersEntity;
import com.ms_security.ms_security.persistence.entity.PermissionEntity;
import com.ms_security.ms_security.persistence.entity.RoleEntity;
import com.ms_security.ms_security.service.IParametersService;
import com.ms_security.ms_security.service.IRoleService;
import com.ms_security.ms_security.service.impl.consultations.PermissionConsultations;
import com.ms_security.ms_security.service.impl.consultations.RoleConsultations;
import com.ms_security.ms_security.service.model.dto.FindByPageDto;
import com.ms_security.ms_security.service.model.dto.RoleDto;
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
@Service
@RequiredArgsConstructor
public class RoleImpl implements IRoleService {

    private final RoleConsultations _roleConsultations;
    private final PermissionConsultations _permissionConsultations;
    private final IParametersService _iParametersService;
    private final ErrorControlUtilities _errorControlUtilities;

    /**
     * Method responsible for searching for a record by the RequestDetail ID.
     *
     * @param encode Base64 encoded request containing the ID of the record to be retrieved.
     * @return A ResponseEntity object containing the requested data or an error message.
     */
    @Override
    public ResponseEntity<String> findById(String encode) {
        log.info("PROCEED TO SEARCH BY ID");
        EncoderUtilities.validateBase64(encode);
        RoleDto findByIdDto = EncoderUtilities.decodeRequest(encode, RoleDto.class);
        EncoderUtilities.validator(findByIdDto);
        log.info(EncoderUtilities.formatJson(findByIdDto));
        Optional<RoleEntity> roleEntity = _roleConsultations.findById(findByIdDto.getId());
        if(roleEntity.isEmpty()) return _errorControlUtilities.handleSuccess(null, 3L);
        RoleEntity role = roleEntity.get();
        RoleDto roleDto = parse(role);
        log.info("SEARCH BY ID IS ENDED");
        return _errorControlUtilities.handleSuccess(roleDto, 1L);
    }

    /**
     * Retrieves all records with pagination.
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
        Page<RoleEntity> pageResult = _roleConsultations.findAll(pageable);
        List<RoleDto> roleDto = pageResult.stream()
                .map(this::parse)
                .toList();
        PageImpl<RoleDto> response = new PageImpl<>(roleDto, pageable, pageResult.getTotalElements());
        log.info("PAGINATED SEARCH COMPLETED");
        return _errorControlUtilities.handleSuccess(response, 1L);
    }


    /**
     * Responsible for creating a new record.
     *
     * @param encode Base64 encoded request containing the details of the record to be created.
     * @return A ResponseEntity object containing the created record or an error message.
     */
    @Override
    public ResponseEntity<String> addNew(String encode) {
        EncoderUtilities.validateBase64(encode);
        log.info("INSERT BEGINS");
        RoleDto roleDto = EncoderUtilities.decodeRequest(encode, RoleDto.class);
        EncoderUtilities.validator(roleDto, RoleDto.Create.class);
        log.info(EncoderUtilities.formatJson(roleDto));
        log.info("START SEARCH BY NAME");
        Optional<RoleEntity> name = _roleConsultations.findByName(roleDto.getName());
        if (name.isPresent()) return _errorControlUtilities.handleSuccess(null, 6L);
        log.info("END SEARCH BY NAME");
        RoleEntity existingEntity = parseEntCreate(roleDto, new RoleEntity());
        existingEntity.setCreateUser(roleDto.getCreateUser());
        existingEntity.setDateTimeCreation(new Date().toString());
        if (roleDto.getPermissionsToAdd() != null) {
            Set<PermissionEntity> permissions = roleDto.getPermissionsToAdd().stream()
                    .map(permissionId -> _permissionConsultations.findById(permissionId).orElseThrow(() -> new RuntimeException("PERMISSION NOT FOUND WITH ID: " + permissionId)))
                    .collect(Collectors.toSet());
            existingEntity.getPermissions().addAll(permissions);
        }
        RoleEntity roleEntity = _roleConsultations.addNew(existingEntity);
        RoleDto roleDtos = parse(roleEntity);
        log.info("INSERT ENDED");
        return _errorControlUtilities.handleSuccess(roleDtos, 1L);
    }

    /**
     * Responsible for updating an existing record.
     *
     * @param encode Base64 encoded request containing the details of the record to be updated.
     * @return A ResponseEntity object containing the updated record or an error message.
     */
    @Override
    public ResponseEntity<String> updateData(String encode) {
        EncoderUtilities.validateBase64(encode);
        log.info("UPDATE BEGINS");
        RoleDto roleDto = EncoderUtilities.decodeRequest(encode, RoleDto.class);
        EncoderUtilities.validator(roleDto, RoleDto.Update.class);
        log.info(EncoderUtilities.formatJson(roleDto));

        log.info("START SEARCH BY ID");
        Optional<RoleEntity> existingRoleOpt = _roleConsultations.findById(roleDto.getId());
        if (existingRoleOpt.isEmpty()) return _errorControlUtilities.handleSuccess(null, 3L);

        RoleEntity existingRole = existingRoleOpt.get();
        log.info("ROLE FOUND");

        // Update the role details
        existingRole = parseEntUpdate(roleDto, existingRole);

        // Handle adding new permissions
        if (roleDto.getPermissionsToAdd() != null) {
            Set<PermissionEntity> newPermissions = roleDto.getPermissionsToAdd().stream()
                    .map(permissionId -> _permissionConsultations.findById(permissionId)
                            .orElseThrow(() -> new RuntimeException("PERMISSION NOT FOUND WITH ID: " + permissionId)))
                    .collect(Collectors.toSet());
            existingRole.getPermissions().addAll(newPermissions);
        }

        // Handle removing permissions
        if (roleDto.getPermissionsToRemove() != null) {
            Set<PermissionEntity> permissionsToRemove = roleDto.getPermissionsToRemove().stream()
                    .map(permissionId -> _permissionConsultations.findById(permissionId)
                            .orElseThrow(() -> new RuntimeException("PERMISSION NOT FOUND WITH ID: " + permissionId)))
                    .collect(Collectors.toSet());
            existingRole.getPermissions().removeAll(permissionsToRemove);
        }

        // Save the updated role
        RoleEntity updatedRole = _roleConsultations.updateData(existingRole);
        RoleDto updatedRoleDto = parse(updatedRole);
        log.info("UPDATE ENDED");

        return _errorControlUtilities.handleSuccess(updatedRoleDto, 1L);
    }


    /**
     * Searches for a role with permissions by ID.
     *
     * @param encode Base64 encoded request containing the ID of the role.
     * @return A ResponseEntity object containing the role with permissions or an error message.
     */
    @Override
    public ResponseEntity<String> findRoleWithPermissionById(String encode) {
        EncoderUtilities.validateBase64(encode);
        log.info("PROCEED TO SEARCH USER WITH ROLES BY ID");
        RoleDto findByIdDto = EncoderUtilities.decodeRequest(encode, RoleDto.class);
        EncoderUtilities.validator(findByIdDto);
        log.info(EncoderUtilities.formatJson(findByIdDto));
        log.info("START SEARCH BY ROLE AND PERMISSION");
        Optional<RoleEntity> userEntity = _roleConsultations.findRoleWithPermissionById(findByIdDto.getId());
        if (userEntity.isEmpty()) return _errorControlUtilities.handleSuccess(null, 3L);
        log.info("END SEARCH BY ROLE AND PERMISSION");
        RoleEntity role = userEntity.get();
        RoleDto roleDto = parse(role);
        Set<String> roleNames = role.getPermissions().stream()
                .map(PermissionEntity::getUrl)
                .collect(Collectors.toSet());
        roleDto.setPermissions(roleNames);
        log.info("SEARCH USER WITH ROLES IS ENDED");
        return _errorControlUtilities.handleSuccess(roleDto, 1L);
    }

    /**
     * Converts a RoleEntity object to a RoleDto object.
     *
     * @param entity The RoleEntity object to be converted.
     * @return The corresponding RoleDto object.
     */
    private RoleDto parse(RoleEntity entity){
        RoleDto roleDto = new RoleDto();
        roleDto.setId(entity.getId());
        roleDto.setName(entity.getName());
        roleDto.setDescription(entity.getDescription());
        roleDto.setStatus(entity.getStatus());
        roleDto.setCreateUser(entity.getCreateUser());
        roleDto.setUpdateUser(entity.getUpdateUser());
        return roleDto;
    }

    /**
     * Converts a RoleDto object to a RoleEntity object.
     *
     * @param dto The RoleDto object to be converted.
     * @param entity The RoleEntity object to be updated.
     * @return The updated RoleEntity object.
     */
    private RoleEntity parseEntCreate(RoleDto dto, RoleEntity entity){
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setId(dto.getId());
        roleEntity.setName(dto.getName());
        roleEntity.setDescription(dto.getDescription());
        roleEntity.setStatus(dto.getStatus());
        roleEntity.setCreateUser(entity.getCreateUser());
        roleEntity.setUpdateUser(entity.getUpdateUser());
        roleEntity.setDateTimeCreation(entity.getDateTimeCreation());
        roleEntity.setDateTimeUpdate(entity.getDateTimeCreation());
        return roleEntity;
    }

    /**
     * Converts a RoleDto object to a RoleEntity object for updating.
     *
     * @param dto The RoleDto object to convert.
     * @param entity The RoleEntity object to populate.
     * @return The populated RoleEntity object.
     */
    private RoleEntity parseEntUpdate(RoleDto dto, RoleEntity entity) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setStatus(dto.getStatus());
        entity.setUpdateUser(dto.getUpdateUser());
        entity.setDateTimeUpdate(new Date().toString());
        entity.setDateTimeCreation(entity.getDateTimeCreation());
        return entity;
    }

}
