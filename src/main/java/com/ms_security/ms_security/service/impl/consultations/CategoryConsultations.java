package com.ms_security.ms_security.service.impl.consultations;

import com.ms_security.ms_security.persistence.entity.CategoryEntity;
import com.ms_security.ms_security.persistence.repository.ICategoryRepository;
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
public class CategoryConsultations {

    private final ICategoryRepository _categoryRepository;

    @Cacheable(value = "CategoryFindById", key = "#id")
    @Transactional(readOnly = true)
    public Optional<CategoryEntity> findById(Long id) {
        return _categoryRepository.findById(id);
    }

    @Cacheable(value = "CategoryFindAll")
    @Transactional(readOnly = true)
    public Page<CategoryEntity> findAll(Pageable pageable) {
        return _categoryRepository.findAll(pageable);
    }

    @CacheEvict(value = {"CategoryFindById", "CategoryFindAll", "CategoryFindByName"}, allEntries = true)
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public CategoryEntity addNew(CategoryEntity entity) {
        return _categoryRepository.save(entity);
    }

    @CacheEvict(value = {"CategoryFindById", "CategoryFindAll", "CategoryFindByName"}, allEntries = true)
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public CategoryEntity updateData(CategoryEntity entity) {
        return _categoryRepository.save(entity);
    }

    @Cacheable(value = "CategoryFindByName", key = "#code")
    @Transactional(readOnly = true)
    public Optional<CategoryEntity> findByName(String name) {
        return _categoryRepository.findByName(name);
    }
}
