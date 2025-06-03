package com.ms_security.ms_security.service.impl.consultations;

import com.ms_security.ms_security.persistence.entity.OrderEntity;
import com.ms_security.ms_security.persistence.repository.IOrderRepository;
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
public class OrderConsultations {

    private final IOrderRepository _orderRepository;

    @Cacheable(value = "OrderFindById", key = "#id")
    @Transactional(readOnly = true)
    public Optional<OrderEntity> findById(Long id) {
        return _orderRepository.findById(id);
    }

    @Cacheable(value = "OrderFindAll")
    @Transactional(readOnly = true)
    public Page<OrderEntity> findAll(Pageable pageable) {
        return _orderRepository.findAll(pageable);
    }

    @CacheEvict(value = {"OrderFindById", "OrderFindAll", "OrderMaxNumber", "OrderFindByOrderNumber", "OrderFindAllByStatus"}, allEntries = true)
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public OrderEntity addNew(OrderEntity entity) {
        return _orderRepository.save(entity);
    }

    @CacheEvict(value = {"OrderFindById", "OrderFindAll", "OrderMaxNumber", "OrderFindByOrderNumber", "OrderFindAllByStatus"}, allEntries = true)
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public OrderEntity updateData(OrderEntity entity) {
        return _orderRepository.save(entity);
    }

    @Cacheable(value = "OrderMaxNumber")
    @Transactional(readOnly = true)
    public Long findMaxOrderNumber() {
        return _orderRepository.findMaxOrderNumber();
    }

    @Cacheable(value = "OrderFindByOrderNumber", key = "#orderNumber")
    @Transactional(readOnly = true)
    public Optional<OrderEntity> findByOrderNumber(Long orderNumber) {
        return _orderRepository.findByOrderNumber(orderNumber);
    }

    @Cacheable(value = "OrderFindAllByStatus", key = "#orderNumber")
    @Transactional(readOnly = true)
    public List<OrderEntity> findAllByStatus(String status) {
        return _orderRepository.findAllByStatus(status);
    }

    @Cacheable(value = "OrderFindByOrderNumberWithItems", key = "#orderNumber")
    @Transactional(readOnly = true)
    public Optional<OrderEntity> findByOrderNumberWithItems(Long orderNumber) {
        return _orderRepository.findByOrderNumberWithItems(orderNumber);
    }

}
