package com.ms_security.ms_security.persistence.repository;

import com.ms_security.ms_security.persistence.entity.EntriesEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IEntriesRepository extends JpaRepository<EntriesEntity, Long> {
    Page<EntriesEntity> findAll(Pageable pageable);

    Optional<EntriesEntity> findByInvoiceNumber(String number);

    @Query("SELECT COALESCE(MAX(e.consecutive), 0) FROM EntriesEntity e")
    Long findMaxConsecutive();

    Optional<EntriesEntity> findFirstByProductIdOrderByDateTimeCreationDesc(Long productId);
}
