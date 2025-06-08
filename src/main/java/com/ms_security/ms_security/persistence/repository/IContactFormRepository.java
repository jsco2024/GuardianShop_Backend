package com.ms_security.ms_security.persistence.repository;

import com.ms_security.ms_security.persistence.entity.ContactFormEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IContactFormRepository extends JpaRepository<ContactFormEntity, Long> {
    Optional<ContactFormEntity> findByEmail(String email);
    Page<ContactFormEntity> findAll(Pageable pageable);
}
