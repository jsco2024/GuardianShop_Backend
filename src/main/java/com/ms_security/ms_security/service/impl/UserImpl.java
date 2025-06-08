package com.ms_security.ms_security.service.impl;

import com.ms_security.ms_security.persistence.entity.ParametersEntity;
import com.ms_security.ms_security.persistence.entity.RoleEntity;
import com.ms_security.ms_security.persistence.entity.UserEntity;
import com.ms_security.ms_security.service.IParametersService;
import com.ms_security.ms_security.service.IUserService;
import com.ms_security.ms_security.service.impl.consultations.RoleConsultations;
import com.ms_security.ms_security.service.impl.consultations.UserConsultations;
import com.ms_security.ms_security.service.model.dto.FindByPageDto;
import com.ms_security.ms_security.service.model.dto.UserDto;
import com.ms_security.ms_security.utilities.EncoderUtilities;
import com.ms_security.ms_security.utilities.ErrorControlUtilities;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of the IUserService interface for managing user-related operations.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class UserImpl implements IUserService {
    private final UserConsultations _userConsultations;
    private final RoleConsultations _roleConsultations;
    private final ErrorControlUtilities _errorControlUtilities;
    private final IParametersService _iParametersService;
    private final PasswordEncoder _passwordEncoder;

    /**
     * Searches for a user by ID.
     *
     * @param encode Base64 encoded string containing the user ID.
     * @return ResponseEntity with the user data or an error message if not found.
     */
    @Override
    public ResponseEntity<String> findById(String encode) {
        log.info("PROCEED TO SEARCH BY ID");
        EncoderUtilities.validateBase64(encode);
        UserDto findByIdDto = EncoderUtilities.decodeRequest(encode, UserDto.class);
        EncoderUtilities.validator(findByIdDto);
        log.info(EncoderUtilities.formatJson(findByIdDto));
        Optional<UserEntity> userEntity = _userConsultations.findById(findByIdDto.getId());
        if (userEntity.isEmpty()) return _errorControlUtilities.handleSuccess(null, 3L);
        UserEntity userEntities = userEntity.get();
        UserDto userDto = parse(userEntities);
        log.info("SEARCH BY ID IS ENDED");
        return _errorControlUtilities.handleSuccess(userDto, 1L);
    }

    /**
     * Retrieves a paginated list of all users.
     *
     * @param encode Base64 encoded string containing pagination parameters.
     * @return ResponseEntity with a paginated list of users.
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
        Pageable pageable = PageRequest.of(request.getPage() - 1,
                Integer.parseInt(pageSizeParam.get().getParameter()));
        Page<UserEntity> pageResult = _userConsultations.findAll(pageable);
        List<UserDto> userDtoList = pageResult.stream()
                .map(this::parse)
                .toList();
        PageImpl<UserDto> response = new PageImpl<>(userDtoList, pageable, pageResult.getTotalElements());
        log.info("PAGINATED SEARCH COMPLETED");
        return _errorControlUtilities.handleSuccess(response, 1L);
    }


    /**
     * Adds a new user.
     *
     * @param encode Base64 encoded string containing the new user details.
     * @return ResponseEntity with the result of the addition.
     */
    @Override
    public ResponseEntity<String> addNew(String encode) {
        EncoderUtilities.validateBase64(encode);
        log.info("START INSERT");
        UserDto userDto = EncoderUtilities.decodeRequest(encode, UserDto.class);
        EncoderUtilities.validator(userDto, UserDto.Create.class);
        log.info(EncoderUtilities.formatJson(userDto));
        log.info("START SEARCH BY NAME");
        Optional<UserEntity> existingUser = _userConsultations.findByUserName(userDto.getName());
        if (existingUser.isPresent()) return _errorControlUtilities.handleSuccess(null, 8L);
        log.info("END SEARCH BY NAME");
        UserEntity userEntity = parseEntCreate(userDto, new UserEntity());
        userEntity.setCreateUser(userDto.getCreateUser());
        userEntity.setDateTimeCreation(new Date().toString());
        if (userDto.getRolesToAdd() != null) {
            log.info("START SEARCH ROLE BY ID");
            Set<RoleEntity> roles = userDto.getRolesToAdd().stream()
                    .map(_roleConsultations::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
            userEntity.getRoles().addAll(roles);
            log.info("END SEARCH ROLE BY ID");
        }else _errorControlUtilities.handleSuccess(null, 13L);
        UserEntity savedUser = _userConsultations.addNew(userEntity);
        UserDto savedUserDto = parse(savedUser);
        log.info("INSERT ENDED");
        return _errorControlUtilities.handleSuccess(savedUserDto, 1L);
    }

    /**
     * Updates existing user data.
     *
     * @param encode Base64 encoded string containing the updated user details.
     * @return ResponseEntity with the result of the update.
     */
    @Override
    public ResponseEntity<String> updateData(String encode) {
        EncoderUtilities.validateBase64(encode);
        log.info("UPDATE BEGINS");
        UserDto userDto = EncoderUtilities.decodeRequest(encode, UserDto.class);
        EncoderUtilities.validator(userDto, UserDto.Update.class);
        log.info(EncoderUtilities.formatJson(userDto));
        Optional<UserEntity> existingUserOpt = _userConsultations.findById(userDto.getId());
        if (existingUserOpt.isEmpty()) return _errorControlUtilities.handleSuccess(null, 3L);
        UserEntity existingUser = existingUserOpt.get();
        if (!existingUser.getUserName().equals(userDto.getUserName())) return _errorControlUtilities.handleSuccess(null, 9L);
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) existingUser.setPassword(_passwordEncoder.encode(userDto.getPassword()));
        UserEntity updatedUser = parseEntUpdate(userDto, existingUser);
        updatedUser.setUpdateUser(userDto.getUpdateUser());
        updatedUser.setDateTimeUpdate(new Date().toString());
        if (userDto.getRolesToAdd() != null) {
            log.info("START SEARCH ROLE TO ADD BY ID");
            Set<RoleEntity> newRoles = userDto.getRolesToAdd().stream()
                    .map(_roleConsultations::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
            existingUser.getRoles().addAll(newRoles);
            log.info("END SEARCH ROLE TO ADD BY ID");
        }else _errorControlUtilities.handleSuccess(null, 13L);
        if (userDto.getRolesToRemove() != null) {
            log.info("START SEARCH ROLE TO REMOVE BY ID");
            Set<RoleEntity> rolesToRemove = userDto.getRolesToRemove().stream()
                    .map(_roleConsultations::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
            existingUser.getRoles().removeAll(rolesToRemove);
        }else _errorControlUtilities.handleSuccess(null, 13L);
        UserEntity savedUser = _userConsultations.updateData(existingUser);
        UserDto savedUserDto = parse(savedUser);
        log.info("UPDATE ENDED");
        return _errorControlUtilities.handleSuccess(savedUserDto, 1L);
    }

    /**
     * Retrieves a user with their roles by ID.
     *
     * @param encode Base64 encoded string containing the user ID.
     * @return ResponseEntity with the user data along with role names.
     */
    @Override
    public ResponseEntity<String> findUserWithRolesById(String encode) {
        EncoderUtilities.validateBase64(encode);
        log.info("PROCEED TO SEARCH USER WITH ROLES BY ID");
        UserDto findByIdDto = EncoderUtilities.decodeRequest(encode, UserDto.class);
        EncoderUtilities.validator(findByIdDto);
        log.info(EncoderUtilities.formatJson(findByIdDto));
        Optional<UserEntity> userEntity = _userConsultations.findById(findByIdDto.getId());
        if (userEntity.isEmpty()) return _errorControlUtilities.handleSuccess(null, 3L);
        UserEntity userEntities = userEntity.get();
        UserDto userDto = parse(userEntities);
        log.info("SEARCH USER AND ROLE BY ID IS ENDED");
        return _errorControlUtilities.handleSuccess(userDto, 1L);
    }

    /**
     * Converts a UserDto to UserEntity for creation.
     *
     * @param dto UserDto to be converted.
     * @param entity New UserEntity instance.
     * @return UserEntity with the data from UserDto.
     */
    private UserEntity parseEntCreate(UserDto dto, UserEntity entity) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(dto.getId());
        userEntity.setName(dto.getName());
        userEntity.setLastName(dto.getLastName());
        userEntity.setUserName(dto.getUserName());
        userEntity.setEmail(dto.getEmail());
        userEntity.setPassword(_passwordEncoder.encode(dto.getPassword()));
        userEntity.setStatus(dto.getStatus());
        userEntity.setCreateUser(entity.getCreateUser());
        userEntity.setUpdateUser(entity.getUpdateUser());
        userEntity.setDateTimeCreation(entity.getDateTimeCreation());
        return userEntity;
    }

    /**
     * Converts a UserDto to UserEntity for updating.
     *
     * @param dto UserDto with updated data.
     * @param entity Existing UserEntity to be updated.
     * @return Updated UserEntity.
     */
    private UserEntity parseEntUpdate(UserDto dto, UserEntity entity) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(dto.getId());
        userEntity.setName(dto.getName());
        userEntity.setLastName(dto.getLastName());
        userEntity.setUserName(dto.getUserName());
        userEntity.setEmail(dto.getEmail());
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            userEntity.setPassword(_passwordEncoder.encode(dto.getPassword()));
        }else userEntity.setPassword(entity.getPassword());
        userEntity.setStatus(dto.getStatus());
        userEntity.setDateTimeUpdate(entity.getDateTimeUpdate());
        return userEntity;
    }

    /**
     * Converts UserEntity to UserDto.
     *
     * @param entity UserEntity to be converted.
     * @return UserDto with data from UserEntity.
     */
    private UserDto parse(UserEntity entity) {
        UserDto userDto = new UserDto();
        userDto.setId(entity.getId());
        userDto.setName(entity.getName());
        userDto.setLastName(entity.getLastName());
        userDto.setUserName(entity.getUserName());
        userDto.setEmail(entity.getEmail());
        userDto.setStatus(entity.getStatus());
        userDto.setCreateUser(entity.getCreateUser());
        userDto.setUpdateUser(entity.getUpdateUser());
        Set<String> roleNames = entity.getRoles().stream()
                .map(RoleEntity::getName)
                .collect(Collectors.toSet());
        userDto.setRoles(roleNames);
        return userDto;
    }
}
