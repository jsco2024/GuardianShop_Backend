package com.ms_security.ms_security.service.impl.consultations;

import com.ms_security.ms_security.persistence.entity.PermissionEntity;
import com.ms_security.ms_security.persistence.entity.RoleEntity;
import com.ms_security.ms_security.persistence.repository.IPermissionRepository;
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
import java.util.Set;

@Service
@RequiredArgsConstructor
@Log4j2
public class PermissionConsultations {
    private final IPermissionRepository _permissionRepository;
    private final RoleConsultations _roleConsultations;

    @Cacheable(value = "PermissionFindById", key = "#id")
    @Transactional(readOnly = true)
    public Optional<PermissionEntity> findById(Long id) {
        return _permissionRepository.findById(id);
    }

    @Cacheable(value = "PermissionFindAll")
    @Transactional(readOnly = true)
    public Page<PermissionEntity> findAll(Pageable pageable) {
        return _permissionRepository.findAll(pageable);
    }

    @CacheEvict(value = {"PermissionFindById","PermissionFindAll", "PermissionFindByName", "PermissionFindByRoleId"}, allEntries = true)
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public PermissionEntity addNew(PermissionEntity entity) {
        return _permissionRepository.save(entity);
    }

    @CacheEvict(value = {"PermissionFindById","PermissionFindAll", "PermissionFindByName", "PermissionFindByRoleId"}, allEntries = true)
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public PermissionEntity updateData(PermissionEntity entity) {
        return _permissionRepository.save(entity);
    }

    @Cacheable(value = "PermissionFindByName", key = "#name")
    @Transactional(readOnly = true)
    public Optional<PermissionEntity> findByName(String name) {
        return _permissionRepository.findByName(name);
    }

    @Cacheable(value = "PermissionFindByRoleId", key = "#roleName")
    @Transactional(readOnly = true)
    public Set<PermissionEntity> findPermissionsByRoleName(String roleName) {
        Optional<RoleEntity> roleEntityOptional = _roleConsultations.findByNameWithPermissions(roleName);
        return roleEntityOptional.map(RoleEntity::getPermissions).orElse(Set.of());
    }
}
