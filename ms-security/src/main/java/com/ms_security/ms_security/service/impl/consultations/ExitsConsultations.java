package com.ms_security.ms_security.service.impl.consultations;

import com.ms_security.ms_security.persistence.entity.ExitsEntity;
import com.ms_security.ms_security.persistence.repository.IExitsRepository;
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
public class ExitsConsultations {

    private final IExitsRepository _exitsRepository;

    @Cacheable(value = "ExitsFindById", key = "#id")
    @Transactional(readOnly = true)
    public Optional<ExitsEntity> findById(Long id) {
        return _exitsRepository.findById(id);
    }

    @Cacheable(value = "ExitsFindAll")
    @Transactional(readOnly = true)
    public Page<ExitsEntity> findAll(Pageable pageable) {
        return _exitsRepository.findAll(pageable);
    }

    @CacheEvict(value = {"ExitsFindById", "ExitsFindAll", "ExitsConsecutive"}, allEntries = true)
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public ExitsEntity addNew(ExitsEntity entity) {
        return _exitsRepository.save(entity);
    }

    @CacheEvict(value = {"ExitsFindById", "ExitsFindAll", "ExitsConsecutive"}, allEntries = true)
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public ExitsEntity updateData(ExitsEntity entity) {
        return _exitsRepository.save(entity);
    }

    @Cacheable(value = "ExitsConsecutive")
    @Transactional(readOnly = true)
    public Long findMaxConsecutive() {
        return _exitsRepository.findMaxConsecutive();
    }
}
