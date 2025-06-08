package com.ms_security.ms_security.service.impl.consultations;

import com.ms_security.ms_security.persistence.entity.RoleEntity;
import com.ms_security.ms_security.persistence.repository.IRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class RoleConsultations {

    private final IRoleRepository _iRoleRepository;

    @Cacheable(value = "RoleFindById", key = "#id")
    @Transactional(readOnly = true)
    public Optional<RoleEntity> findById(Long id) {
        return _iRoleRepository.findById(id);
    }

    @Cacheable(value = "RoleFindAll")
    @Transactional(readOnly = true)
    public Page<RoleEntity> findAll(Pageable pageable) {
        return _iRoleRepository.findAll(pageable);
    }

    @CacheEvict(value = {"RoleFindById","RoleFindAll", "RoleFindByName", "RoleFindRoleWithPermissionById"}, allEntries = true)
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public RoleEntity addNew(RoleEntity entity) {
        return _iRoleRepository.save(entity);
    }

    @CacheEvict(value = {"RoleFindById","RoleFindAll", "RoleFindByName", "RoleFindRoleWithPermissionById"}, allEntries = true)
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public RoleEntity updateData(RoleEntity entity) {
        return _iRoleRepository.save(entity);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "RoleFindByName", key = "#name")
    public Optional<RoleEntity> findByName(String name) {
        return _iRoleRepository.findByName(name);
    }

    @Cacheable(value = "RoleFindRoleWithPermissionById", key = "#roleId")
    @Transactional(readOnly = true)
    public Optional<RoleEntity> findRoleWithPermissionById(Long roleId) {
        return _iRoleRepository.findRoleWithPermissionById(roleId);
    }
    public Optional<RoleEntity> findByNameWithPermissions(String name) {
        return _iRoleRepository.findByNameWithPermissions(name);
    }

}
