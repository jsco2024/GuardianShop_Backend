package com.ms_security.ms_security.service.impl.consultations;

import com.ms_security.ms_security.persistence.entity.OrderItemEntity;
import com.ms_security.ms_security.persistence.repository.IOrderItemRepository;
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
public class OrderItemConsultations {

    private final IOrderItemRepository _orderItemRepository;

    @Cacheable(value = "OrderItemFindById", key = "#id")
    @Transactional(readOnly = true)
    public Optional<OrderItemEntity> findById(Long id) {
        return _orderItemRepository.findById(id);
    }

    @Cacheable(value = "OrderItemFindAll")
    @Transactional(readOnly = true)
    public Page<OrderItemEntity> findAll(Pageable pageable) {
        return _orderItemRepository.findAll(pageable);
    }

    @CacheEvict(value = {"OrderItemFindById", "OrderItemFindAll", "OrderItemFindByProductAndCart", "OrderItemFindByCartId"}, allEntries = true)
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public OrderItemEntity addNew(OrderItemEntity entity) {
        return _orderItemRepository.save(entity);
    }

    @CacheEvict(value = {"OrderItemFindById", "OrderItemFindAll", "OrderItemFindByProductAndCart", "OrderItemFindByCartId"}, allEntries = true)
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public OrderItemEntity updateData(OrderItemEntity entity) {
        return _orderItemRepository.save(entity);
    }

    @CacheEvict(value = {"OrderItemFindById", "OrderItemFindAll", "OrderItemFindByProductAndCart", "OrderItemFindByCartId"}, allEntries = true)
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void deleteById(Long id) {
        _orderItemRepository.deleteById(id);
    }

    @Cacheable(value = "OrderItemFindByProductAndCart", key = "#productId + '-' + #cartId")
    @Transactional(readOnly = true)
    public Optional<OrderItemEntity> findByProductIdAndCartId(Long productId, Long cartId) {
        log.info("Searching for OrderItem with Product ID: {} and Cart ID: {}", productId, cartId);
        return _orderItemRepository.findByProductIdAndCartId(productId, cartId);
    }

    @Cacheable(value = "OrderItemFindByOrderId", key = "#orderId")
    @Transactional(readOnly = true)
    public List<OrderItemEntity> findByOrderId(Long orderId) {
        log.info("Searching for OrderItems with Order ID: {}", orderId);
        return _orderItemRepository.findByOrderId(orderId);
    }

    @Cacheable(value = "OrderItemFindByCartAndUser", key = "#cartId + '-' + #createUser")
    @Transactional(readOnly = true)
    public boolean existsByCartIdAndCreateUser(Long cartId, String createUser) {
        log.info("Searching for OrderItems with Cart ID: {} and Username: {}", cartId, createUser);
        return _orderItemRepository.existsByCartIdAndCreateUser(cartId, createUser);
    }

    @Cacheable(value = "OrderItemFindByCartIdPage", key = "#cartId")
    @Transactional(readOnly = true)
    public Page<OrderItemEntity> findByCartIdPage(Long cartId, Pageable pageable) {
        return _orderItemRepository.findByCartId(cartId, pageable);
    }

    @Cacheable(value = "OrderItemFindByCartId", key = "#cartId")
    @Transactional(readOnly = true)
    public List<OrderItemEntity> findByCartId(Long cartId) {
        return _orderItemRepository.findByCartId(cartId);
    }

}
