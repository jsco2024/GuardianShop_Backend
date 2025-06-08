package com.ms_security.ms_security.persistence.repository;

import com.ms_security.ms_security.persistence.entity.ExitsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface IExitsRepository extends JpaRepository <ExitsEntity, Long> {
    Page<ExitsEntity> findAll(Pageable pageable);
    @Query("SELECT COALESCE(MAX(e.consecutive), 0) FROM ExitsEntity e")
    Long findMaxConsecutive();

}
