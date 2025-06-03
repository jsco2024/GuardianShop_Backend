package com.ms_security.ms_security.persistence.repository;

import com.ms_security.ms_security.persistence.entity.OrderItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IOrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
//    Page<OrderItemEntity> findAll(Pageable pageable);
    Optional<OrderItemEntity> findByProductIdAndCartId(Long productId, Long cartId);
    List<OrderItemEntity> findByOrderId(Long orderId);
    boolean existsByCartIdAndCreateUser(Long cartId, String createUser);
    Page<OrderItemEntity> findByCartId(Long cartId, Pageable pageable);
    List<OrderItemEntity> findByCartId(Long cartId);
}
