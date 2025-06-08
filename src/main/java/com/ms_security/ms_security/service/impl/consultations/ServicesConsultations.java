package com.ms_security.ms_security.service.impl.consultations;

import com.ms_security.ms_security.persistence.entity.InventoryEntity;
import com.ms_security.ms_security.persistence.entity.ServicesEntity;
import com.ms_security.ms_security.persistence.repository.IServicesRepository;
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
public class ServicesConsultations {

    private final IServicesRepository _servicesRepository;

    @Cacheable(value = "ServicesFindById", key = "#id")
    @Transactional(readOnly = true)
    public Optional<ServicesEntity> findById(Long id) {
        return _servicesRepository.findById(id);
    }

    @Cacheable(value = "ServicesFindByAll")
    @Transactional(readOnly = true)
    public Page<ServicesEntity> findAll(Pageable pageable) {
        return _servicesRepository.findAll(pageable);
    }

    @CacheEvict(value = {"ServicesFindById","ServicesFindByAll", "ServicesFindByName", "ServicesFindByCode", "ServiceFindByCategoryId"}, allEntries = true)
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public ServicesEntity addNew(ServicesEntity entity) {
        return _servicesRepository.save(entity);
    }

    @CacheEvict(value = {"ServicesFindById","ServicesFindByAll", "ServicesFindByName", "ServicesFindByCode", "ServiceFindByCodeAndCategoryId"}, allEntries = true)
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public ServicesEntity updateData(ServicesEntity entity) {
        return _servicesRepository.save(entity);
    }

    @Cacheable(value = "ServicesFindByCode", key = "#code")
    @Transactional(readOnly = true)
    public Optional<ServicesEntity> findByCode(Long code) {
        return _servicesRepository.findByCode(code);
    }

    @Cacheable(value = "ServicesFindByName", key = "#name")
    @Transactional(readOnly = true)
    public Optional<ServicesEntity> findByName(String name) {
        return _servicesRepository.findByName(name);
    }

    @Cacheable(value = "ServiceFindByCategoryId", key = "#categoriId")
    @Transactional(readOnly = true)
    public Optional<ServicesEntity> findByCategoryId(Long categoriId) {
        return _servicesRepository.findByCategoryId(categoriId);
    }

    @Cacheable(value = "ServiceFindByCodeAndCategoryId", key = "#code + '_' + #categoryId")
    @Transactional(readOnly = true)
    public Optional<ServicesEntity> findByCodeAndCategoryId(Long code,Long categoriId) {
        return _servicesRepository.findByCodeAndCategoryId(code, categoriId);
    }

    @Cacheable(value = "ServicesFindByIdWithInventories", key = "#id")
    @Transactional(readOnly = true)
    public Optional<ServicesEntity> findByIdWithInventories(Long id) {
        return _servicesRepository.findByIdWithInventories(id);
    }


}
