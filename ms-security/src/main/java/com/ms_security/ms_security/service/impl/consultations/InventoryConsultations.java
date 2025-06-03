package com.ms_security.ms_security.service.impl.consultations;

import com.ms_security.ms_security.persistence.entity.InventoryEntity;
import com.ms_security.ms_security.persistence.entity.ServicesEntity;
import com.ms_security.ms_security.persistence.repository.IInventoryRepository;
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
public class InventoryConsultations {

    private final IInventoryRepository _inventoryRepository;

    @Cacheable(value = "InventoryFindById", key = "#id")
    @Transactional(readOnly = true)
    public Optional<InventoryEntity> findById(Long id) {
        return _inventoryRepository.findById(id);
    }

    @Cacheable(value = "InventoryFindAll")
    @Transactional(readOnly = true)
    public Page<InventoryEntity> findAll(Pageable pageable) {
        return _inventoryRepository.findAll(pageable);
    }

    @CacheEvict(value = {"InventoryFindById", "InventoryFindAll", "InventoryFindByCode", "InventoryFindAllByIds", "InventoryFindAllByServiceId"}, allEntries = true)
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public InventoryEntity addNew(InventoryEntity entity) {
        return _inventoryRepository.save(entity);
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    @CacheEvict(value = {"InventoryFindById", "InventoryFindAll", "InventoryFindByCode", "InventoryFindAllByIds", "InventoryFindAllByServiceId"}, allEntries = true)
    public List<InventoryEntity> updateBatch(List<InventoryEntity> solicitud){
        return _inventoryRepository.saveAll(solicitud);
    }

    @CacheEvict(value = {"InventoryFindById", "InventoryFindAll", "InventoryFindByCode"}, allEntries = true)
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public InventoryEntity updateData(InventoryEntity entity) {
        return _inventoryRepository.save(entity);
    }

    @Cacheable(value = "InventoryFindByCode", key = "#name")
    @Transactional(readOnly = true)
    public Optional<InventoryEntity> findByProductCode(String code) {
        return _inventoryRepository.findByProductCode(code);
    }


    @Transactional(readOnly = true)
    public List<InventoryEntity> getsAll() {
        return _inventoryRepository.findAll();
    }

    @Cacheable(value = "InventoryFindAllByIds", key = "#ids")
    @Transactional(readOnly = true)
    public List<InventoryEntity> findAllByIds(List<Long> ids) {
        return _inventoryRepository.findAllById(ids);
    }

    @Cacheable(value = "InventoryFindAllByServiceId", key = "#serviceId")
    @Transactional(readOnly = true)
    public List<InventoryEntity> findAllByServiceId(Long serviceId) {
        return _inventoryRepository.findAllByServiceId(serviceId);
    }
}
