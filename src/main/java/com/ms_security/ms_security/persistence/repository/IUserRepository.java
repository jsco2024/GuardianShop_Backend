package com.ms_security.ms_security.persistence.repository;

import com.ms_security.ms_security.persistence.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<UserEntity, Long> {
    Page<UserEntity> findAll(Pageable pageable);
    Optional<UserEntity> findByUserName(String name);
    Optional<UserEntity> findUserWithRolesById(Long userId);
    Optional<UserEntity> findByEmail(String email);

}
