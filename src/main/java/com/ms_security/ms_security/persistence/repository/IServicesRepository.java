package com.ms_security.ms_security.persistence.repository;

import com.ms_security.ms_security.persistence.entity.InventoryEntity;
import com.ms_security.ms_security.persistence.entity.ServicesEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IServicesRepository extends JpaRepository<ServicesEntity, Long> {
    Page<ServicesEntity> findAll(Pageable pageable);
    Optional<ServicesEntity> findByCode(Long name);
    Optional<ServicesEntity> findByName(String name);
    Optional<ServicesEntity> findByCategoryId(Long categoriId);
    Optional<ServicesEntity> findByCodeAndCategoryId(Long code, Long categoryId);

    @Query("SELECT s FROM ServicesEntity s LEFT JOIN FETCH s.inventories WHERE s.id = :id")
    Optional<ServicesEntity> findByIdWithInventories(@Param("id") Long id);

}
