package com.ms_security.ms_security.persistence.repository;

import com.ms_security.ms_security.persistence.entity.InventoryEntity;
import com.ms_security.ms_security.persistence.entity.ServicesEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface  IInventoryRepository extends JpaRepository<InventoryEntity, Long> {

    Page<InventoryEntity> findAll(Pageable pageable);
    Optional<InventoryEntity> findByProductCode(String code);
    List<InventoryEntity> findAllById(Iterable<Long> ids);

    List<InventoryEntity> findAllByServiceId(Long serviceId);


}
