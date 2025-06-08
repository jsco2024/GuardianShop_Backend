package com.ms_security.ms_security.service.impl;

import com.ms_security.ms_security.persistence.entity.CartEntity;
import com.ms_security.ms_security.persistence.entity.InventoryEntity;
import com.ms_security.ms_security.persistence.entity.OrderItemEntity;
import com.ms_security.ms_security.persistence.entity.ParametersEntity;
import com.ms_security.ms_security.service.ICartService;
import com.ms_security.ms_security.service.IParametersService;
import com.ms_security.ms_security.service.impl.consultations.CartConsultations;
import com.ms_security.ms_security.service.impl.consultations.InventoryConsultations;
import com.ms_security.ms_security.service.impl.consultations.OrderItemConsultations;
import com.ms_security.ms_security.service.model.dto.CartDto;
import com.ms_security.ms_security.service.model.dto.CartRequestDto;
import com.ms_security.ms_security.service.model.dto.FindByPageDto;
import com.ms_security.ms_security.service.model.dto.OrderItemDto;
import com.ms_security.ms_security.utilities.EncoderUtilities;
import com.ms_security.ms_security.utilities.ErrorControlUtilities;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class CartImpl implements ICartService {
    private final CartConsultations _cartConsultations;
    private final InventoryConsultations _inventoryConsultations;
    private final OrderItemConsultations _orderItemConsultations;
    private final IParametersService _iParametersService;
    private final ErrorControlUtilities _errorControlUtilities;

    @Override
    public ResponseEntity<String> findById(String encode) {
        log.info("PROCEED TO SEARCH CART BY ID");
        EncoderUtilities.validateBase64(encode);
        CartDto findByIdDto = EncoderUtilities.decodeRequest(encode, CartDto.class);
        EncoderUtilities.validator(findByIdDto);
        log.info(EncoderUtilities.formatJson(findByIdDto));
        Optional<CartEntity> cartEntity = _cartConsultations.findById(findByIdDto.getId());
        if (cartEntity.isEmpty()) return _errorControlUtilities.handleSuccess(null, 3L);
        log.info("SEARCH BY ID IS ENDED");
        return _errorControlUtilities.handleSuccess(parse(cartEntity.get()), 1L);
    }

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
        Pageable pageable = PageRequest.of(request.getPage() - 1,
                Integer.parseInt(pageSizeParam.get().getParameter()));
        Page<CartEntity> pageResult = _cartConsultations.findAll(pageable);
        List<CartDto> cartDtoList = pageResult.stream()
                .map(this::parse)
                .toList();
        PageImpl<CartDto> response = new PageImpl<>(cartDtoList, pageable, pageResult.getTotalElements());
        log.info("PAGINATED SEARCH COMPLETED");
        return _errorControlUtilities.handleSuccess(response, 1L);
    }


    @Override
    public ResponseEntity<String> addNew(String encode) {
        EncoderUtilities.validateBase64(encode);
        log.info("ADDING NEW CART BEGINS");
        CartDto cartDto = EncoderUtilities.decodeRequest(encode, CartDto.class);
        EncoderUtilities.validator(cartDto);
        log.info(EncoderUtilities.formatJson(cartDto));
        if (cartDto.getStatus() == null || cartDto.getStatus().isEmpty()) cartDto.setStatus("PENDING");
        CartEntity cartEntity = parseEnt(cartDto, new CartEntity());
        cartEntity.setCreateUser(cartDto.getCreateUser());
        cartEntity.setDateTimeCreation(new Date().toString());
        CartEntity savedCart = _cartConsultations.addNew(cartEntity);
        log.info("ADDING NEW CART ENDED");
        return _errorControlUtilities.handleSuccess(parse(savedCart), 1L);
    }

    @Override
    public ResponseEntity<String> updateData(String encode) {
        EncoderUtilities.validateBase64(encode);
        log.info("UPDATING CART BEGINS");
        CartDto cartDto = EncoderUtilities.decodeRequest(encode, CartDto.class);
        EncoderUtilities.validator(cartDto);
        log.info(EncoderUtilities.formatJson(cartDto));
        log.info("START SEARCH BY ID");
        Optional<CartEntity> cartEntity = _cartConsultations.findById(cartDto.getId());
        if (cartEntity.isEmpty()) return _errorControlUtilities.handleSuccess(null, 3L);
        log.info("END SEARCH BY NAME");
        CartEntity existingCart = parseEnt(cartDto, cartEntity.get());
        if (cartDto.getStatus() == null || cartDto.getStatus().isEmpty()) existingCart.setStatus("PENDING");
        existingCart.setUpdateUser(cartDto.getUpdateUser());
        existingCart.setDateTimeUpdate(new Date().toString());
        CartEntity updatedCart = _cartConsultations.updateData(existingCart);
        log.info("UPDATING CART ENDED");
        return _errorControlUtilities.handleSuccess(parse(updatedCart), 1L);
    }

    @Override
    public ResponseEntity<String> addToCart(String encode) {
        EncoderUtilities.validateBase64(encode);
        CartRequestDto cartRequest = EncoderUtilities.decodeRequest(encode, CartRequestDto.class);
        log.info("inventoryId: {}", cartRequest.getInventoryId());
        if (cartRequest.getQuantity() < 0) return _errorControlUtilities.handleSuccess(null, 47L);
        log.info("START SEARCH FOR INVENTORY STOCK");
        Optional<InventoryEntity> inventoryEntity = _inventoryConsultations.findById(cartRequest.getInventoryId());
        if (inventoryEntity.isEmpty() || inventoryEntity.get().getStock() < cartRequest.getQuantity()) return _errorControlUtilities.handleSuccess(null, 24L);
        InventoryEntity inventory = inventoryEntity.get();
        log.info("START SEARCH FOR CART");
        Optional<CartEntity> optionalCartEntity = _cartConsultations.findById(cartRequest.getCartId());
        CartEntity cartEntity;
        if (optionalCartEntity.isPresent() && "PENDING".equals(optionalCartEntity.get().getStatus())) {
            cartEntity = optionalCartEntity.get();
            log.info("SEARCHING FOR EXISTING ORDER ITEM WITH INVENTORYID={} AND CARTID={}", cartRequest.getInventoryId(), cartEntity.getId());
            Optional<OrderItemEntity> existingOrderItem = _orderItemConsultations.findByProductIdAndCartId(cartRequest.getInventoryId(), cartEntity.getId());
            log.info("RESULT OF ORDER ITEM SEARCH: {}", existingOrderItem.isPresent() ? "FOUND" : "NOT FOUND");
            if (existingOrderItem.isPresent()) {
                log.info("FOUND EXISTING ITEM IN CART, UPDATING QUANTITY TO {}", cartRequest.getQuantity());
                OrderItemEntity itemToUpdate = existingOrderItem.get();
                itemToUpdate.setQuantity(cartRequest.getQuantity());
                itemToUpdate.setUpdateUser(cartRequest.getCreateUser());
                log.info("ITEM QUANTITY SET TO {}", itemToUpdate.getQuantity());
                _orderItemConsultations.updateData(itemToUpdate);
                log.info("ITEM UPDATED IN DATABASE.");
            } else {
                log.info("NO EXISTING ITEM FOUND, ADDING NEW ITEM.");
                OrderItemEntity newItem = new OrderItemEntity();
                newItem.setName(inventory.getName());
                newItem.setCartId(cartEntity.getId());
                newItem.setProductId(cartRequest.getInventoryId());
                newItem.setQuantity(cartRequest.getQuantity());
                newItem.setPrice(inventory.getSalePrice());
                newItem.setCreateUser(cartRequest.getCreateUser());
                cartEntity.getItems().add(newItem);
                _orderItemConsultations.addNew(newItem);
            }
        } else {
            log.info("CREATING A NEW CART AS THE EXISTING CART IS EITHER NOT FOUND OR NOT PENDING.");
            cartEntity = new CartEntity();
            cartEntity.setStatus("PENDING");
            cartEntity.setUserId(cartRequest.getUserId());
            cartEntity.setCreateUser(cartRequest.getCreateUser());
            cartEntity.setItems(new ArrayList<>());
            CartEntity savedCart = _cartConsultations.addNew(cartEntity);
            log.info("CART CREATED SUCCESSFULLY {}", savedCart);
            OrderItemEntity newItem = new OrderItemEntity();
            newItem.setName(inventory.getName());
            newItem.setCartId(savedCart.getId());
            newItem.setProduct(inventory);
            newItem.setProductId(cartRequest.getInventoryId());
            newItem.setQuantity(cartRequest.getQuantity());
            newItem.setPrice(inventory.getSalePrice());
            newItem.setCreateUser(cartRequest.getCreateUser());
            savedCart.getItems().add(newItem);
            _orderItemConsultations.addNew(newItem);
            log.info("ITEM ADDED SUCCESSFULLY {}", newItem);
            _cartConsultations.updateData(savedCart);
        }
        log.info("ITEM ADDED SUCCESSFULLY");
        return _errorControlUtilities.handleSuccess(null, 1L);
    }



    @Override
    public ResponseEntity<String> removeItemFromCart(String encode) {
        EncoderUtilities.validateBase64(encode);
        log.info("REMOVING ITEM FROM CART BEGINS");
        CartDto cartDto = EncoderUtilities.decodeRequest(encode, CartDto.class);
        Optional<CartEntity> optionalCartEntity = _cartConsultations.findById(cartDto.getId());
        if (optionalCartEntity.isEmpty()) return _errorControlUtilities.handleSuccess(null, 18L);
        CartEntity cartEntity = optionalCartEntity.get();
        Long itemIdToRemove = cartDto.getItems().get(0).getId();
        boolean itemExists = cartEntity.getItems().stream()
                .anyMatch(item -> item.getId().equals(itemIdToRemove));
        if (!itemExists) return _errorControlUtilities.handleSuccess(null, 19L);
        cartEntity.getItems().removeIf(item -> item.getId().equals(itemIdToRemove));
        log.info("ITEM REMOVED FROM CART");
        cartEntity.setUpdateUser(cartDto.getUpdateUser());
        cartEntity.setDateTimeUpdate(new Date().toString());
        _cartConsultations.updateData(cartEntity);
        _orderItemConsultations.deleteById(itemIdToRemove);
        return _errorControlUtilities.handleSuccess(null, 1L);
    }

    @Override
    public ResponseEntity<String> deleteCart(String encode) {
        EncoderUtilities.validateBase64(encode);
        log.info("REMOVING ITEM FROM CART BEGINS");
        CartDto cartDto = EncoderUtilities.decodeRequest(encode, CartDto.class);
        log.info("START SEARCH CART BY ID");
        Optional<CartEntity> cartEntity = _cartConsultations.findById(cartDto.getId());
        log.info("END SEARCH CART BY ID");
        if (cartEntity.isEmpty()) return _errorControlUtilities.handleSuccess(null, 3L);
        CartEntity cart = cartEntity.get();
        log.info("CHANGING CART STATUS TO INACTIVE");
        cart.setStatus("INACTIVE");
        cart.setUpdateUser(cartDto.getUpdateUser());
        cart.setDateTimeUpdate(new Date().toString());
        _cartConsultations.updateData(cart);
        log.info("ITEM STATUS CHANGED TO INACTIVE, ITEMS REMAIN IN CART");
        return _errorControlUtilities.handleSuccess(null, 1L);
    }



    @Override
    @Scheduled(cron = "0 0 0 * * ?")
    public ResponseEntity<String> deleteAllPendingCarts() {
        log.info("DELETING ALL PENDING CARTS BEGINS");
        List<CartEntity> pendingCarts = _cartConsultations.findAllByStatus("PENDING");
        if (pendingCarts.isEmpty()) return _errorControlUtilities.handleSuccess(null, 19L);
        log.info("START UPDATING PENDING CART STATUS");
        for (CartEntity cart : pendingCarts) {
            log.info("PROCESSING CART ID: " + cart.getId());
            cart.setStatus("INACTIVE");
            cart.setUpdateUser("SYSTEM");
            cart.setDateTimeUpdate(new Date().toString());
            _cartConsultations.updateData(cart);
            log.info("CART STATUS CHANGED TO INACTIVE FOR CART ID: " + cart.getId());
        }
        log.info("ALL PENDING CARTS UPDATED TO INACTIVE SUCCESSFULLY");
        return _errorControlUtilities.handleSuccess(null, 1L);
    }

    @Override
    public ResponseEntity<String> checkIfUserHasCart(String encode) {
        log.info("PROCEED TO CHECK IF USER HAS AN EXISTING CART");
        EncoderUtilities.validateBase64(encode);
        CartDto userDto = EncoderUtilities.decodeRequest(encode, CartDto.class);
        EncoderUtilities.validator(userDto);
        log.info(EncoderUtilities.formatJson(userDto));
        List<CartEntity> userCarts = _cartConsultations.findCartsByUserId(userDto.getUserId());
        Optional<CartEntity> pendingCart = userCarts.stream()
                .filter(cart -> "PENDING".equals(cart.getStatus()))
                .findFirst();
        String responseMessage;
        if (pendingCart.isPresent()) {
            Long cartId = pendingCart.get().getId();
            log.info("USER WITH ID {} HAS AN EXISTING PENDING CART WITH ID {}", userDto.getUserId(), cartId);
            responseMessage = String.format("{\"message\": \"User has an existing pending cart\", \"cartId\": %d}", cartId);
        } else {
            log.info("USER WITH ID {} DOES NOT HAVE AN EXISTING PENDING CART", userDto.getUserId());
            responseMessage = "{\"message\": \"User does not have an existing pending cart\", \"cartId\": 0}";
        }
        return _errorControlUtilities.handleSuccess(responseMessage, 1L);
    }


    private CartDto parse(CartEntity entity) {
        CartDto cartDto = new CartDto();
        cartDto.setId(entity.getId());
        cartDto.setUserId(entity.getUserId());
        cartDto.setStatus(entity.getStatus());
        cartDto.setCreateUser(entity.getCreateUser());
        cartDto.setUpdateUser(entity.getUpdateUser());
        log.info("CONVERT ITEMS IF THEY EXIST");
        if (entity.getItems() == null) {
            cartDto.setItems(new ArrayList<>());
        } else if (!entity.getItems().isEmpty()) {
            List<OrderItemDto> itemDtos = entity.getItems().stream().map(itemEntity -> {
                OrderItemDto itemDto = new OrderItemDto();
                itemDto.setId(itemEntity.getId());
                itemDto.setProductId(itemEntity.getProduct().getId());
                itemDto.setQuantity(itemEntity.getQuantity());
                return itemDto;
            }).toList();
            cartDto.setItems(itemDtos);
        }
        return cartDto;
    }

    private CartEntity parseEnt(CartDto dto, CartEntity entity) {
        entity.setId(dto.getId());
        entity.setStatus(dto.getStatus());
        entity.setUserId(dto.getUserId());
        entity.setCreateUser(dto.getCreateUser());
        entity.setUpdateUser(dto.getUpdateUser());
        log.info("CONVERT ITEMS IF THEY EXIST");
        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            List<OrderItemEntity> itemEntities = dto.getItems().stream().map(itemDto -> {
                OrderItemEntity itemEntity = new OrderItemEntity();
                itemEntity.setId(itemDto.getId());
                InventoryEntity inventory = new InventoryEntity();
                inventory.setId(itemDto.getProductId());
                itemEntity.setProduct(inventory);
                itemEntity.setQuantity(itemDto.getQuantity());
                return itemEntity;
            }).toList();
            entity.setItems(itemEntities);
        }
        return entity;
    }
}
