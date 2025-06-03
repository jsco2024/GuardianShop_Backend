package com.ms_security.ms_security.persistence.repository;

import com.ms_security.ms_security.persistence.entity.ParametersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IParametersRepository extends JpaRepository<ParametersEntity, Long> {
    Optional<ParametersEntity> findByCodeParameter(Long codeParameter);

    void deleteByCodeParameter(Long codeParameter);

}
