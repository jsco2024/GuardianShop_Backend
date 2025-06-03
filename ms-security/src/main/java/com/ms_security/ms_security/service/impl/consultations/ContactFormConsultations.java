package com.ms_security.ms_security.service.impl.consultations;

import com.ms_security.ms_security.persistence.entity.ContactFormEntity;
import com.ms_security.ms_security.persistence.repository.IContactFormRepository;
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
public class ContactFormConsultations {

    private final IContactFormRepository _contactFormRepository;

    @Cacheable(value = "ContactFormFindById", key = "#id")
    @Transactional(readOnly = true)
    public Optional<ContactFormEntity> findById(Long id) {
        return _contactFormRepository.findById(id);
    }

    @Cacheable(value = "ContactFormFindAll")
    @Transactional(readOnly = true)
    public Page<ContactFormEntity> findAll(Pageable pageable) {
        return _contactFormRepository.findAll(pageable);
    }


    @CacheEvict(value = {"ContactFormFindById","ContactFormFindAll", "ContactFormFindByEmail"}, allEntries = true)
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public ContactFormEntity addNew(ContactFormEntity entity) {
        return _contactFormRepository.save(entity);
    }

    @CacheEvict(value = {"ContactFormFindById","ContactFormFindAll", "ContactFormFindByEmail"}, allEntries = true)
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public ContactFormEntity updateData(ContactFormEntity entity) {
        return _contactFormRepository.save(entity);
    }

    @Cacheable(value = "ContactFormFindByEmail", key = "#email")
    @Transactional(readOnly = true)
    public Optional<ContactFormEntity> findByEmail(String email) {
        return _contactFormRepository.findByEmail(email);
    }
}
