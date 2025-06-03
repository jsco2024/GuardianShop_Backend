package com.ms_security.ms_security.service.impl.consultations;

import com.ms_security.ms_security.persistence.entity.ParametersEntity;
import com.ms_security.ms_security.persistence.repository.IParametersRepository;
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

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class ParametersConsultations {

    private final IParametersRepository _parametersRepository;

    @Cacheable(value = "ParametersFindById", key = "#id")
    @Transactional(readOnly = true)
    public Optional<ParametersEntity> findById(Long id) {
        return _parametersRepository.findById(id);
    }

    @Cacheable(value = "ParametersFindByAll")
    @Transactional(readOnly = true)
    public List<ParametersEntity> findAll() {
        return _parametersRepository.findAll();
    }

    @CacheEvict(value = {"ParametersFindById", "ParametersFindByAll", "ParametersFindByCode"}, allEntries = true)
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public ParametersEntity save(ParametersEntity entity) {
        return _parametersRepository.save(entity);
    }

    @CacheEvict(value = {"ParametersFindById", "ParametersFindByAll", "ParametersFindByCode"}, allEntries = true)
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void deleteByCodeParameter(Long codeParameter) {
        _parametersRepository.deleteByCodeParameter(codeParameter);
    }

    @Cacheable(value = "ParametersFindByCode", key = "#codeParameter")
    @Transactional(readOnly = true)
    public Optional<ParametersEntity> findByCodeParameter(Long codeParameter) {
        return _parametersRepository.findByCodeParameter(codeParameter);
    }
}
