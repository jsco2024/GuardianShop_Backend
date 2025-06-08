package com.ms_security.ms_security.persistence.repository;

import com.ms_security.ms_security.persistence.entity.CategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ICategoryRepository extends JpaRepository<CategoryEntity, Long> {
    Page<CategoryEntity> findAll(Pageable pageable);
    Optional<CategoryEntity> findByName(String name);
}
