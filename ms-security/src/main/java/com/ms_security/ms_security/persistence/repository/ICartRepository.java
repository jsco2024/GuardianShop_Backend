package com.ms_security.ms_security.persistence.repository;

import com.ms_security.ms_security.persistence.entity.CartEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ICartRepository extends JpaRepository<CartEntity, Long> {
    Page<CartEntity> findAll(Pageable pageable);
    Optional<CartEntity> findByUserName(String userName);
    List<CartEntity> findAllByStatus(String status);

    @Modifying
    @Transactional
    @Query("UPDATE CartEntity c SET c.status = :status WHERE c.id = :cartId")
    void updateCartStatus(Long cartId, String status);

    @Modifying
    @Transactional
    @Query("DELETE FROM OrderItemEntity o WHERE o.cart.id = :cartId")
    void deleteItemsByCartId(@Param("cartId") Long cartId);

    Optional<CartEntity> findByUserIdAndStatus(Long userId, String status);

    List<CartEntity> findByUserId(Long userId);

}
