package com.ms_security.ms_security.persistence.repository;

import com.ms_security.ms_security.persistence.entity.PermissionEntity;
import com.ms_security.ms_security.persistence.entity.RoleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IPermissionRepository extends JpaRepository<PermissionEntity, Long> {
    Page<PermissionEntity> findAll(Pageable pageable);
    Optional<PermissionEntity> findByName(String name);

    @Query("SELECT r FROM RoleEntity r JOIN FETCH r.permissions WHERE r.name = :roleName")
    Optional<RoleEntity> findByNameWithPermissions(@Param("roleName") String roleName);
}
