package com.ms_security.ms_security.service.impl.consultations;

import com.ms_security.ms_security.persistence.entity.UserEntity;
import com.ms_security.ms_security.persistence.repository.IUserRepository;
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
public class UserConsultations {

    private final IUserRepository _iUserRepository;

    @Cacheable(value = "UserFindById", key = "#id")
    @Transactional(readOnly = true)
    public Optional<UserEntity> findById(Long id) {
        return _iUserRepository.findById(id);
    }

    @Cacheable(value = "UserFindByAll")
    @Transactional(readOnly = true)
    public Page<UserEntity> findAll(Pageable pageable) {
        return _iUserRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<UserEntity> findAll() {
        return _iUserRepository.findAll();
    }

    @CacheEvict(value = {"UserFindById","UserFindByAll", "UserFindByUserName", "UserFindUserWithRolesById", "UserFindByEmail"}, allEntries = true)
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public UserEntity addNew(UserEntity entity) {
        return _iUserRepository.save(entity);
    }

    @CacheEvict(value = {"UserFindById","UserFindByAll", "UserFindByUserName", "UserFindUserWithRolesById", "UserFindByEmail"}, allEntries = true)
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public UserEntity updateData(UserEntity entity) {
        return _iUserRepository.save(entity);
    }

    @Cacheable(value = "UserFindByUserName", key = "#name")
    @Transactional(readOnly = true)
    public Optional<UserEntity> findByUserName(String name) {
        return _iUserRepository.findByUserName(name);
    }

    @Cacheable(value = "UserFindUserWithRolesById", key = "#id")
    @Transactional(readOnly = true)
    public Optional<UserEntity> findUserWithRolesById(Long id) {
        return _iUserRepository.findUserWithRolesById(id);
    }

    @Cacheable(value = "UserFindByEmail", key = "#email")
    @Transactional(readOnly = true)
    public Optional<UserEntity> findByEmail(String email) {
        return _iUserRepository.findByEmail(email);
    }

}
