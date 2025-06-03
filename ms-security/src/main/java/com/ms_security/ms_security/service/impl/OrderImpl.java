package com.ms_security.ms_security.service.impl;

import com.ms_security.ms_security.controller.UnifiedPaymentController;
import com.ms_security.ms_security.persistence.entity.*;
import com.ms_security.ms_security.service.*;
import com.ms_security.ms_security.service.impl.consultations.CartConsultations;
import com.ms_security.ms_security.service.impl.consultations.InventoryConsultations;
import com.ms_security.ms_security.service.impl.consultations.OrderConsultations;
import com.ms_security.ms_security.service.impl.consultations.OrderItemConsultations;
import com.ms_security.ms_security.service.model.dto.*;
import com.ms_security.ms_security.utilities.EncoderUtilities;
import com.ms_security.ms_security.utilities.ErrorControlUtilities;
import com.ms_security.ms_security.utilities.PdfGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class OrderImpl implements IOrderService {

    private final OrderConsultations _orderConsultations;
    private final OrderItemConsultations _orderItemConsultations;
    private final IExitsServices _exitsServices;
    private final IEntriesServices _entriesServices;
    private final CartConsultations _cartConsultations;
    private final InventoryConsultations _inventoryConsultations;
    private final UnifiedPaymentController _unifiedPaymentController;
    private final IParametersService _iParametersService;
    private final IInventoryService _iInventoryService;
    private final ErrorControlUtilities _errorControlUtilities;


    /**
     * Method responsible for searching for a record by its ID.
     *
     * @param encode ID of the order item to be retrieved.
     * @return A ResponseEntity object containing the requested data or an error message.
     */
    @Override
    public ResponseEntity<String> findById(String encode) {
        log.info("PROCEED TO SEARCH ORDER ITEM BY ID");
        EncoderUtilities.validateBase64(encode);
        OrderItemDto findByIdDto = EncoderUtilities.decodeRequest(encode, OrderItemDto.class);
        EncoderUtilities.validator(findByIdDto);
        log.info(EncoderUtilities.formatJson(findByIdDto));
        Optional<OrderEntity> orderEntity = _orderConsultations.findById(findByIdDto.getId());
        if (orderEntity.isEmpty()) return _errorControlUtilities.handleSuccess(null, 3L);
        OrderDto orderDto = parse(orderEntity.get());
        log.info("SEARCH ORDER ITEM BY ID IS ENDED");
        return _errorControlUtilities.handleSuccess(orderDto, 1L);
    }

    /**
     * Retrieves all records.
     *
     * @return A ResponseEntity object containing a list of all order items or an error message.
     */
    @Override
    public ResponseEntity<String> findAll(String encode) {
        EncoderUtilities.validateBase64(encode);
        log.info("INITIATING PAGINATED SEARCH");
        FindByPageDto request = EncoderUtilities.decodeRequest(encode, FindByPageDto.class);
        EncoderUtilities.validator(request);
        log.info(EncoderUtilities.formatJson(request));
        log.info("INITIATING PARAMETER QUERY");
        Optional<ParametersEntity> pageSizeParam = _iParametersService.findByCodeParameter(1L);
        log.info("PARAMETER QUERY COMPLETED");
        Pageable pageable = PageRequest.of(
                request.getPage() - 1,
                Integer.parseInt(pageSizeParam.get().getParameter()));
        Page<OrderEntity> pageResult = _orderConsultations.findAll(pageable);
        List<OrderDto> orderDtos = pageResult.stream()
                .map(this::parse)
                .toList();
        PageImpl<OrderDto> response = new PageImpl<>(orderDtos, pageable, pageResult.getTotalElements());
        log.info("PAGINATED SEARCH COMPLETED");
        return _errorControlUtilities.handleSuccess(response, 1L);
    }


    /**
     * Responsible for creating a new order item.
     *
     * @param encode Base64 encoded request containing the details of the order item to be created.
     * @return A ResponseEntity object containing the created order item or an error message.
     */
    @Transactional
    @Override
    public ResponseEntity<String> addNew(String encode) {
        EncoderUtilities.validateBase64(encode);
        log.info("INSERT ORDER ITEM BEGINS");
        OrderDto orderDto = EncoderUtilities.decodeRequest(encode, OrderDto.class);
        EncoderUtilities.validator(orderDto);
        log.info("DECODED ORDER DTO: " + EncoderUtilities.formatJson(orderDto));
        Long userId = orderDto.getUserId();
        log.info("FETCHING CART FOR USER ID AND STATUS: " + userId);
        Optional<CartEntity> cartEntityOptional = _cartConsultations.findByUserIdAndStatus(userId, "PENDING");
        if (cartEntityOptional.isPresent()) {
            CartEntity cartEntity = cartEntityOptional.get();
            Long cartId = cartEntity.getId();
            List<OrderItemEntity> cartItems = _orderItemConsultations.findByCartId(cartId);
            log.info("NUMBER OF ITEMS IN CART: " + (cartItems != null ? cartItems.size() : 0));
            if (cartItems == null || cartItems.isEmpty()) return _errorControlUtilities.handleSuccess(null, 27L);
            OrderEntity orderEntity = parseEnt(orderDto, new OrderEntity());
            Long nextOrderNumber = _orderConsultations.findMaxOrderNumber() + 1;
            log.info("GENERATING NEXT ORDER NUMBER: " + nextOrderNumber);
            orderEntity.setOrderNumber(nextOrderNumber);
            orderEntity.setUserId(userId);
            orderEntity.setCreateUser(orderDto.getCreateUser());
            orderEntity.setDateTimeCreation(new Date().toString());
            orderEntity.setDateTimeOrder(new Date().toString());
            orderEntity.setStatus("PENDING");
            BigDecimal totalOrderPrice = BigDecimal.ZERO;
            for (OrderItemEntity item : cartItems) {
                log.info("PROCESSING ITEM: " + item.getName());
                item.setOrderId(orderEntity.getId());
                BigDecimal itemTotalPrice = item.getPrice().multiply(new BigDecimal(item.getQuantity()));
                totalOrderPrice = totalOrderPrice.add(itemTotalPrice);
                log.info("ITEM TOTAL PRICE: " + itemTotalPrice);
            }
            orderEntity.setUnitPrice(cartItems.stream().findFirst().map(OrderItemEntity::getPrice).orElse(BigDecimal.ZERO));
            orderEntity.setProductName(cartItems.stream().findFirst().map(OrderItemEntity::getName).orElse(""));
            orderEntity.setQuantity(cartItems.stream().map(OrderItemEntity::getQuantity).reduce(0L, Long::sum));
            orderEntity.setItems(cartItems);
            orderEntity.setTotalPrice(totalOrderPrice);
            log.info("TOTAL ORDER PRICE: " + totalOrderPrice);
            OrderEntity createdOrder = _orderConsultations.addNew(orderEntity);
            log.info("ORDER CREATED SUCCESSFULLY WITH ORDER NUMBER: " + createdOrder.getOrderNumber());
            OrderDto createdOrderDto = parse(createdOrder);
            log.info("UPDATING ORDER NUMBER IN ITEMS");
            for (OrderItemEntity orderItem : cartItems) {
                orderItem.setOrderId(createdOrder.getId());
                _orderItemConsultations.updateData(orderItem);
            }
            cartEntity.setStatus("FINISHED");
            _cartConsultations.updateData(cartEntity);
            log.info("GENERATE PDF FOR THE UPDATED ORDER");
            generateOrderPdf(createdOrder, null, cartItems);
            log.info("PDF GENERATED SUCCESSFULLY");
            log.info("INSERT ORDER ITEM ENDED");
            log.info("STARTING INVENTORY OUTPUT ");
            for (OrderItemEntity orderItem : cartItems) {
                Optional<InventoryEntity> optionalInventory = _inventoryConsultations.findById(orderItem.getProductId());
                if (optionalInventory.isPresent()) {
                    InventoryEntity inventory = optionalInventory.get();
                    _iInventoryService.stockExit(inventory.getProductCode(), orderItem.getQuantity(), orderItem.getCreateUser());
                    log.info("STOCK UPDATED SUCCESSFULLY FOR PRODUCT: " + inventory.getProductCode());
                } else _errorControlUtilities.handleSuccess(null, 24L);
                log.info("ENDING INVENTORY OUTPUT ");
            }
            return _errorControlUtilities.handleSuccess(createdOrderDto, 1L);
        } else return _errorControlUtilities.handleSuccess(null, 28L);
    }

    /**
     * Responsible for updating order items based on the items in the user's cart.
     *
     * @param encode Base64 encoded request containing the order details.
     * @return A ResponseEntity object containing the updated order or an error message.
     */
    @Override
    public ResponseEntity<String> updateData(String encode) {
        EncoderUtilities.validateBase64(encode);
        log.info("INSERT OR UPDATE ORDER ITEM BEGINS");
        OrderDto orderDto = EncoderUtilities.decodeRequest(encode, OrderDto.class);
        EncoderUtilities.validator(orderDto);
        log.info("DECODED ORDER DTO: " + EncoderUtilities.formatJson(orderDto));
        Long userId = orderDto.getUserId();
        log.info("SEARCH ORDER FOR ORDER NUMBER: " + orderDto.getOrderNumber());
        Optional<OrderEntity> entity = _orderConsultations.findByOrderNumber(orderDto.getOrderNumber());
        if (entity.isEmpty()) return _errorControlUtilities.handleSuccess(null, 21L);
        log.info("FETCHING CART FOR USER ID: " + userId);
        Optional<CartEntity> cartEntityOptional = _cartConsultations.findByUserIdAndStatus(userId, "PENDING");
        if (cartEntityOptional.isPresent()) {
            CartEntity cartEntity = cartEntityOptional.get();
            Long cartId = cartEntity.getId();
            List<OrderItemEntity> cartItems = _orderItemConsultations.findByCartId(cartId);
            log.info("NUMBER OF ITEMS IN CART: " + (cartItems != null ? cartItems.size() : 0));
            if (cartItems == null || cartItems.isEmpty()) return _errorControlUtilities.handleSuccess(null, 27L);
            OrderEntity orderEntity = parseEnt(orderDto, new OrderEntity());
            orderEntity.setOrderNumber(entity.get().getOrderNumber());
            orderEntity.setUpdateUser(orderDto.getUpdateUser());
            orderEntity.setDateTimeUpdate(new Date().toString());
            orderEntity.setDateTimeOrder(entity.get().getDateTimeOrder());
            orderEntity.setStatus("PENDING");

            List<OrderItemEntity> orderItems = new ArrayList<>();
            BigDecimal totalOrderPrice = BigDecimal.ZERO;
            for (OrderItemEntity itemDto : cartItems) {
                log.info("PROCESSING ITEM: " + itemDto.getName());
                Optional<OrderItemEntity> existingItemOptional = _orderItemConsultations.findByProductIdAndCartId(itemDto.getProductId(), cartEntity.getId());
                OrderItemEntity orderItemEntity;
                if (existingItemOptional.isPresent()) {
                    orderItemEntity = existingItemOptional.get();
                    orderItemEntity.setCartId(itemDto.getCartId());
                    orderItemEntity.setName(itemDto.getName());
                    orderItemEntity.setProductId(itemDto.getProductId());
                    orderItemEntity.setQuantity(itemDto.getQuantity());
                    orderItemEntity.setPrice(itemDto.getPrice());
                    orderItemEntity.setDateTimeUpdate(new Date().toString());
                    log.info("UPDATED ITEM: " + orderItemEntity.getName());
                } else {
                    orderItemEntity = new OrderItemEntity();
                    orderItemEntity.setName(itemDto.getName());
                    orderItemEntity.setQuantity(itemDto.getQuantity());
                    orderItemEntity.setPrice(itemDto.getPrice());
                    orderItemEntity.setProduct(itemDto.getProduct());
                    orderItemEntity.setCartId(itemDto.getCartId());
                    orderItemEntity.setUpdateUser(itemDto.getUpdateUser());
                    log.info("CREATED NEW ITEM: " + orderItemEntity.getName());
                }
                BigDecimal itemTotalPrice = orderItemEntity.getPrice().multiply(new BigDecimal(orderItemEntity.getQuantity()));
                totalOrderPrice = totalOrderPrice.add(itemTotalPrice);
                log.info("ITEM TOTAL PRICE: " + itemTotalPrice);
                orderItemEntity.setOrder(orderEntity);
                orderItems.add(orderItemEntity);
            }
            orderEntity.setUnitPrice(orderItems.stream().findFirst().map(OrderItemEntity::getPrice).orElse(BigDecimal.ZERO));
            orderEntity.setProductName(orderItems.stream().findFirst().map(OrderItemEntity::getName).orElse(""));
            orderEntity.setQuantity(orderItems.stream().map(OrderItemEntity::getQuantity).reduce(0L, Long::sum));
            orderEntity.setItems(orderItems);
            orderEntity.setTotalPrice(totalOrderPrice);
            log.info("TOTAL ORDER PRICE: " + totalOrderPrice);
            for (OrderItemEntity orderItem : orderItems) {
                log.info("STARTING INVENTORY OUTPUT ");
                Optional<InventoryEntity> optionalInventory = _inventoryConsultations.findById(orderItem.getProductId());
                if (optionalInventory.isPresent()) {
                    InventoryEntity inventory = optionalInventory.get();
                    _iInventoryService.stockReturned(inventory.getProductCode(), inventory.getPendingStock(), orderItem.getUpdateUser());
                    log.info("STOCK UPDATED SUCCESSFULLY FOR PRODUCT: " + inventory.getProductCode());
                } else _errorControlUtilities.handleSuccess(null, 24L);
                log.info("ENDING INVENTORY OUTPUT ");
            }
            OrderEntity updatedOrder = _orderConsultations.updateData(orderEntity);
            log.info("ORDER UPDATED SUCCESSFULLY WITH ORDER NUMBER: " + updatedOrder.getOrderNumber());
            OrderDto updatedOrderDto = parse(updatedOrder);

            log.info("GENERATE PDF FOR THE UPDATED ORDER");
            generateOrderPdf(updatedOrder, null, orderItems);
            log.info("PDF GENERATED SUCCESSFULLY");
            log.info("UPDATE ORDER ITEM ENDED");
            for (OrderItemEntity orderItem : orderItems) {
                log.info("STARTING INVENTORY OUTPUT ");
                Optional<InventoryEntity> optionalInventory = _inventoryConsultations.findById(orderItem.getProductId());
                if (optionalInventory.isPresent()) {
                    InventoryEntity inventory = optionalInventory.get();
                    _iInventoryService.stockExit(inventory.getProductCode(), orderItem.getQuantity(), orderItem.getUpdateUser());
                    log.info("STOCK UPDATED SUCCESSFULLY FOR PRODUCT: " + inventory.getProductCode());
                } else _errorControlUtilities.handleSuccess(null, 24L);
                log.info("ENDING INVENTORY OUTPUT ");
            }
            return _errorControlUtilities.handleSuccess(updatedOrderDto, 1L);
        } else return _errorControlUtilities.handleSuccess(null, 28L);
    }





    /**
     * Cancels an existing order by changing its status to INACTIVE.
     *
     * @param encode Base64 encoded request containing the ID of the order to be canceled.
     * @return A ResponseEntity object indicating the success or failure of the operation.
     */
    @Override
    public ResponseEntity<String> cancelOrder(String encode) {
        EncoderUtilities.validateBase64(encode);
        log.info("CANCEL ORDER PROCESS BEGINS");
        OrderDto orderDto = EncoderUtilities.decodeRequest(encode, OrderDto.class);
        EncoderUtilities.validator(orderDto);
        log.info(EncoderUtilities.formatJson(orderDto));
        Optional<OrderEntity> orderEntityOptional = _orderConsultations.findByOrderNumber(orderDto.getOrderNumber());
        if (orderEntityOptional.isEmpty()) return _errorControlUtilities.handleSuccess(null, 21L);
        OrderEntity orderEntity = orderEntityOptional.get();
        orderEntity.setStatus("CANCELLED");
        orderEntity.setUpdateUser(orderDto.getUpdateUser());
        orderEntity.setDateTimeUpdate(new Date().toString());
        for (OrderItemEntity item : orderEntity.getItems()) {
            Optional<InventoryEntity> inventory = _inventoryConsultations.findById(item.getProduct().getId());
            if (inventory.isEmpty()) return _errorControlUtilities.handleSuccess(null, 3L);
            _entriesServices.returnToStock(item.getProduct().getId(), item.getQuantity(), orderDto.getUpdateUser());
//            Optional<InventoryEntity> inventoryEntityOpt = _inventoryConsultations.findById(item.getProduct().getId());
//            if (inventoryEntityOpt.isPresent()) {
//                InventoryEntity inventories = inventoryEntityOpt.get();
//                inventory.get().setPendingStock(inventory.get().getPendingStock() - item.getQuantity());
//                _inventoryConsultations.updateData(inventories);
//            }
        }
        OrderEntity updatedOrder = _orderConsultations.updateData(orderEntity);
        OrderDto updatedOrderDto = parse(updatedOrder);
        log.info("CANCEL ORDER PROCESS ENDED");
        return _errorControlUtilities.handleSuccess(updatedOrderDto, 1L);
    }



    @Override
    public ResponseEntity<String> checkout(String encode) {
        EncoderUtilities.validateBase64(encode);
        log.info("CHECKOUT PROCESS BEGINS");
        OrderDto orderDto = EncoderUtilities.decodeRequest(encode, OrderDto.class);
        EncoderUtilities.validator(orderDto);
        log.info(EncoderUtilities.formatJson(orderDto));
        log.info("START SEARCH ORDER BY NUMBER");
        Optional<OrderEntity> orderEntity = _orderConsultations.findByOrderNumber(orderDto.getOrderNumber());
        if (orderEntity.isEmpty()) return _errorControlUtilities.handleSuccess(null, 21L);
        log.info("END SEARCH ORDER BY NUMBER");
        OrderEntity order = orderEntity.get();
        if (!"PENDING".equals(order.getStatus())) return _errorControlUtilities.handleSuccess(null, 30L);
        log.info("START PAYMENT PROCESS");
        ResponseEntity<String> paymentResponse = processPayment(orderDto.getUnifiedPaymentDto());
        if (!paymentResponse.getStatusCode().is2xxSuccessful()) {
            log.info("PAYMENT FAILED");
            return _errorControlUtilities.handleSuccess(null, 26L);
        }
        log.info("PAYMENT SUCCESSFUL, UPDATING ORDER STATUS");
        order.setStatus("PAID");
        order.setUpdateUser(orderDto.getUpdateUser());
        order.setDateTimeUpdate(new Date().toString());
        OrderEntity checkedOutOrder = _orderConsultations.updateData(order);
        OrderDto updatedOrderDto = parse(checkedOutOrder);
        List<OrderItemEntity> orderItems = _orderItemConsultations.findByOrderId(checkedOutOrder.getId());
        generateOrderPdf(checkedOutOrder, orderDto.getUnifiedPaymentDto(), orderItems);
        log.info("UPDATING CART STATUS TO COMPLETED");
        if (!orderItems.isEmpty()) {
            for (OrderItemEntity item : orderItems) {
                log.info("ORDER ITEM: " + EncoderUtilities.formatJson(item));
            }
            Long cartId = orderItems.stream().map(OrderItemEntity::getCartId).findFirst().orElse(null);
            _cartConsultations.updateCartStatus(cartId, "COMPLETED");
            log.info("CART STATUS UPDATED TO COMPLETED FOR CART ID: " + cartId);
        } else {
            log.warn("NO ORDER ITEMS FOUND FOR ORDER ID: " + checkedOutOrder.getId());
        }
        log.info("CHECKOUT PROCESS ENDED");
        log.info("TRIGGERING INVENTORY EXIT");
        if ("PAID".equals(updatedOrderDto.getStatus())) {
            String encodedOrderDto = EncoderUtilities.encodeResponse(updatedOrderDto);
            _exitsServices.exitOnPayment(String.valueOf(encodedOrderDto));
        }
        log.info("EXIT ON PAYMENT TRIGGERED");
        return _errorControlUtilities.handleSuccess(updatedOrderDto, 1L);
    }


    private void generateOrderPdf(OrderEntity order, UnifiedPaymentDto paymentDto, List<OrderItemEntity> orderItems) {
        OrderPdfDto orderPdfDto = new OrderPdfDto();
        String itemName = orderItems.stream()
                .findFirst()
                .map(OrderItemEntity::getName)
                .orElse("Nombre no disponible");
        orderPdfDto.setName(itemName);
        orderPdfDto.setUnitPrice(order.getTotalPrice());
        orderPdfDto.setOrderNumber(order.getOrderNumber());
        orderPdfDto.setQuantity(order.getQuantity());
        orderPdfDto.setTotalPrice(order.getTotalPrice());
        PdfGenerator pdfGenerator = new PdfGenerator();
        byte[] pdfBytes = pdfGenerator.generateOrderPdf(orderPdfDto);
        try (FileOutputStream fos = new FileOutputStream("receipt.pdf")) {
            fos.write(pdfBytes);
            log.info("PDF receipt generated and saved successfully.");
        } catch (IOException e) {
            log.error("Error saving PDF receipt: " + e.getMessage());
        }
    }

    @Override
    @Scheduled(cron = "0 0 0 * * ?")
    public ResponseEntity<String> deactivatePendingOrdersOlder() {
        log.info("DEACTIVATING PENDING ORDERS OLDER THAN 1 HOUR BEGINS");
        Date now = new Date();
        Date limitDate = new Date(now.getTime() - (60 * 60 * 1000));
        List<OrderEntity> pendingOrders = _orderConsultations.findAllByStatus("PENDING");
        log.info("NO PENDING ORDERS FOUND");
        if (pendingOrders.isEmpty()) return _errorControlUtilities.handleSuccess(null, 29L);
        log.info("START DEACTIVATING PENDING ORDERS");
        for (OrderEntity order : pendingOrders) {
            if (order.getDateTimeCreation() != null && new Date(order.getDateTimeCreation()).before(limitDate)) {
                log.info("CANCELING ORDER ID: " + order.getId());
                order.setStatus("CANCELLED");
                order.setUpdateUser("SYSTEM");
                List<OrderItemEntity> orderItems = order.getItems();
                for (OrderItemEntity item : orderItems) {
                    _entriesServices.returnToStock(item.getProduct().getId(), item.getQuantity(), "SYSTEM");
//                    Optional<InventoryEntity> inventoryEntityOpt = _inventoryConsultations.findById(item.getProductId());
////                    if (inventoryEntityOpt.isPresent()) {
////                        InventoryEntity inventory = inventoryEntityOpt.get();
////                        inventory.setPendingStock(inventory.getPendingStock() - item.getQuantity());
////                        _inventoryConsultations.updateData(inventory);
////                    }
                }
                log.info("ORDER CANCELED" + order.getId());
                _orderConsultations.updateData(order);
            }
        }
        log.info("ALL PENDING ORDERS OLDER THAN 48 HOURS CANCELLED SUCCESSFULLY");
        return _errorControlUtilities.handleSuccess(null, 1L);
    }

    public ResponseEntity<String> processPayment(UnifiedPaymentDto paymentDto) {
        try {
            ResponseEntity<?> response = _unifiedPaymentController.processPayment(paymentDto);
            return (ResponseEntity<String>) response;
        } catch (Exception e) {
            return _errorControlUtilities.handleSuccess(null, 26L);
        }
    }





    /**
     * Converts an OrderEntity object to an OrderDto object.
     *
     * @param entity The OrderEntity object to be converted.
     * @return The corresponding OrderDto object.
     */
    private OrderDto parse(OrderEntity entity) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(entity.getId());
        orderDto.setOrderNumber(entity.getOrderNumber());
        orderDto.setUnitPrice(entity.getUnitPrice());
        orderDto.setTotalPrice(entity.getTotalPrice());
        orderDto.setStatus(entity.getStatus());
        orderDto.setCreateUser(entity.getCreateUser());
        orderDto.setUpdateUser(entity.getUpdateUser());
        if (entity.getItems() != null && !entity.getItems().isEmpty()) {
            List<OrderItemDto> itemDtos = entity.getItems().stream().map(itemEntity -> {
                OrderItemDto itemDto = new OrderItemDto();
                itemDto.setId(itemEntity.getId());
                itemDto.setProductId(itemEntity.getProduct().getId());
                itemDto.setQuantity(itemEntity.getQuantity());
                itemDto.setPrice(itemEntity.getPrice());
                return itemDto;
            }).toList();
            orderDto.setItems(itemDtos);
        }
        return orderDto;
    }


    /**
     * Converts an OrderDto object to an OrderEntity object.
     *
     * @param dto The OrderDto object to be converted.
     * @param entities The OrderEntity object to be updated.
     * @return The updated OrderEntity object.
     */
    private OrderEntity parseEnt(OrderDto dto, OrderEntity entities) {
        OrderEntity entity = new OrderEntity();
        entity.setId(dto.getId());
        entity.setOrderNumber(dto.getOrderNumber());
        entity.setUnitPrice(dto.getUnitPrice());
        entity.setTotalPrice(dto.getTotalPrice());
        entity.setStatus(dto.getStatus());
        entity.setCreateUser(entities.getCreateUser());
        entity.setUpdateUser(entities.getUpdateUser());
        entity.setDateTimeOrder(entities.getDateTimeOrder());
        entity.setDateTimeCreation(entities.getDateTimeCreation());
        entity.setDateTimeUpdate(entities.getDateTimeUpdate());
        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            List<OrderItemEntity> itemEntities = dto.getItems().stream().map(itemDto -> {
                OrderItemEntity itemEntity = new OrderItemEntity();
                itemEntity.setId(itemDto.getId());
                itemEntity.setProduct(itemDto.getProduct());
                itemEntity.setQuantity(itemDto.getQuantity());
                itemEntity.setPrice(itemDto.getPrice());
                return itemEntity;
            }).toList();
            entity.setItems(itemEntities);
        }

        return entity;
    }

}
