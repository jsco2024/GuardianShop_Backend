package com.ms_security.ms_security.service.impl.consultations;

import com.ms_security.ms_security.persistence.entity.EntriesEntity;
import com.ms_security.ms_security.persistence.repository.IEntriesRepository;
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
public class EntriesConsultations {

    private final IEntriesRepository _entriesRepository;

    @Cacheable(value = "EntriesFindById", key = "#id")
    @Transactional(readOnly = true)
    public Optional<EntriesEntity> findById(Long id) {
        return _entriesRepository.findById(id);
    }

    @Cacheable(value = "EntriesFindAll")
    @Transactional(readOnly = true)
    public Page<EntriesEntity> findAll(Pageable pageable) {
        return _entriesRepository.findAll(pageable);
    }

    @CacheEvict(value = {"EntriesFindById", "EntriesFindAll", "EntriesFindByInvoiceNumber", "EntriesConsecutive"}, allEntries = true)
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public EntriesEntity addNew(EntriesEntity entity) {
        return _entriesRepository.save(entity);
    }

    @CacheEvict(value = {"EntriesFindById", "EntriesFindAll", "EntriesFindByInvoiceNumber", "EntriesConsecutive"}, allEntries = true)
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public EntriesEntity updateData(EntriesEntity entity) {
        return _entriesRepository.save(entity);
    }

    @Cacheable(value = "EntriesFindByInvoiceNumber", key = "#number")
    @Transactional(readOnly = true)
    public Optional<EntriesEntity> findByInvoiceNumber(String number) {
        return _entriesRepository.findByInvoiceNumber(number);
    }

    @Cacheable(value = "EntriesConsecutive")
    @Transactional(readOnly = true)
    public Long findMaxConsecutive() {
        return _entriesRepository.findMaxConsecutive();
    }

    @Transactional(readOnly = true)
    public Optional<EntriesEntity> findFirstByProductIdOrderByDateTimeCreationDesc(Long productId) {
        return _entriesRepository.findFirstByProductIdOrderByDateTimeCreationDesc(productId);
    }
}
