package com.ms_security.ms_security.persistence.repository;

import com.ms_security.ms_security.persistence.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IOrderRepository extends JpaRepository<OrderEntity, Long> {
    Page<OrderEntity> findAll(Pageable pageable);
    @Query("SELECT COALESCE(MAX(o.orderNumber), 0) FROM OrderEntity o")
    Long findMaxOrderNumber();
    Optional<OrderEntity> findByOrderNumber(Long orderNumber);
    List<OrderEntity> findAllByStatus(String status);
    @Query("SELECT o FROM OrderEntity o LEFT JOIN FETCH o.items WHERE o.orderNumber = :orderNumber")
    Optional<OrderEntity> findByOrderNumberWithItems(@Param("orderNumber") Long orderNumber);

}
