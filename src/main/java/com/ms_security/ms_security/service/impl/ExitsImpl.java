package com.ms_security.ms_security.service.impl;

import com.ms_security.ms_security.persistence.entity.*;
import com.ms_security.ms_security.service.IExitsServices;
import com.ms_security.ms_security.service.IInventoryService;
import com.ms_security.ms_security.service.IParametersService;
import com.ms_security.ms_security.service.impl.consultations.ExitsConsultations;
import com.ms_security.ms_security.service.impl.consultations.InventoryConsultations;
import com.ms_security.ms_security.service.impl.consultations.OrderConsultations;
import com.ms_security.ms_security.service.model.dto.ExitsDto;
import com.ms_security.ms_security.service.model.dto.FindByPageDto;
import com.ms_security.ms_security.utilities.EncoderUtilities;
import com.ms_security.ms_security.utilities.ErrorControlUtilities;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class ExitsImpl implements IExitsServices {

    private final ExitsConsultations _exitsConsultations;
    private final OrderConsultations _orderConsultations;
    private final IInventoryService _inventoryService;
    private final IParametersService _iParametersService;
    private final InventoryConsultations _inventoryConsultations;
    private final ErrorControlUtilities _errorControlUtilities;

    /**
     * Method responsible for searching for an exit record by its ID.
     *
     * @param encode ID of the exit item to be retrieved.
     * @return A ResponseEntity object containing the requested data or an error message.
     */
    @Override
    public ResponseEntity<String> findById(String encode) {
        log.info("PROCEED TO SEARCH EXIT ITEM BY ID");
        EncoderUtilities.validateBase64(encode);
        ExitsDto findByIdDto = EncoderUtilities.decodeRequest(encode, ExitsDto.class);
        EncoderUtilities.validator(findByIdDto);
        log.info(EncoderUtilities.formatJson(findByIdDto));
        Optional<ExitsEntity> exitsEntity = _exitsConsultations.findById(findByIdDto.getId());
        if (exitsEntity.isEmpty()) return _errorControlUtilities.handleSuccess(null, 3L);
        ExitsDto exitsDto = parse(exitsEntity.get());
        log.info("SEARCH EXIT ITEM BY ID IS ENDED");
        return _errorControlUtilities.handleSuccess(exitsDto, 1L);
    }

    /**
     * Retrieves all exit records.
     *
     * @return A ResponseEntity object containing a list of all exit items or an error message.
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
        Page<ExitsEntity> pageResult = _exitsConsultations.findAll(pageable);
        List<ExitsDto> exitsDtos = pageResult.stream()
                .map(this::parse)
                .toList();
        PageImpl<ExitsDto> response = new PageImpl<>(exitsDtos, pageable, pageResult.getTotalElements());
        log.info("PAGINATED SEARCH COMPLETED");
        return _errorControlUtilities.handleSuccess(response, 1L);
    }


    /**
     * Responsible for creating a new exit item.
     *
     * @param encode Base64 encoded request containing the details of the exit item to be created.
     * @return A ResponseEntity object containing the created exit item or an error message.
     */
    @Override
    public ResponseEntity<String> addNew(String encode) {
        EncoderUtilities.validateBase64(encode);
        log.info("INSERT EXIT ITEM BEGINS");
        ExitsDto exitsDto = EncoderUtilities.decodeRequest(encode, ExitsDto.class);
        EncoderUtilities.validator(exitsDto);
        log.info("DECODED EXITS DTO: {}", EncoderUtilities.formatJson(exitsDto));
        Optional<OrderEntity> orderEntity = _orderConsultations.findByOrderNumber(exitsDto.getOrderNumber());
        log.warn("ORDER ALREADY EXISTS FOR ORDER NUMBER: {}", exitsDto.getOrderNumber());
        if (orderEntity.isPresent()) return _errorControlUtilities.handleSuccess(null, 20L);
        ExitsEntity entity = parseEnt(exitsDto, new ExitsEntity());
        Long nextOrderNumber = _exitsConsultations.findMaxConsecutive() + 1;
        log.info("NEXT ORDER NUMBER FOR EXIT ITEM: {}", nextOrderNumber);
        entity.setConsecutive(nextOrderNumber);
        entity.setCreateUser(exitsDto.getCreateUser());
        entity.setDateTimeCreation(new Date().toString());
        ExitsEntity createdExit = _exitsConsultations.addNew(entity);
        log.info("CREATED EXIT ENTITY: {}", EncoderUtilities.formatJson(createdExit));
        Optional<InventoryEntity> inventory = _inventoryConsultations.findById(entity.getProductId());
        log.warn("INVENTORY NOT FOUND FOR PRODUCT ID: {}", entity.getProductId());
        if (inventory.isEmpty()) return _errorControlUtilities.handleSuccess(null, 3L);
        _inventoryService.handleExit(inventory.get().getProductCode(), exitsDto.getQuantity(), exitsDto.getUpdateUser());
        log.info("HANDLED EXIT FOR PRODUCT: {}", inventory.get().getProductCode());
        ExitsDto createdExitsDto = parse(createdExit);
        log.info("INSERT EXIT ITEM ENDED");
        return _errorControlUtilities.handleSuccess(createdExitsDto, 1L);
    }


    /**
     * Responsible for updating an existing exit item.
     *
     * @param encode Base64 encoded request containing the updated details.
     * @return A ResponseEntity object containing the updated exit item or an error message.
     */
    @Override
    public ResponseEntity<String> updateData(String encode) {
        EncoderUtilities.validateBase64(encode);
        log.info("UPDATE EXIT BEGINS");
        ExitsDto exitsDto = EncoderUtilities.decodeRequest(encode, ExitsDto.class);
        EncoderUtilities.validator(exitsDto);
        log.info("DECODED EXITS DTO FOR UPDATE: {}", EncoderUtilities.formatJson(exitsDto));
        log.warn("EXITS ENTITY NOT FOUND FOR ID: {}", exitsDto.getId());
        Optional<ExitsEntity> existingExitsEntityOpt = _exitsConsultations.findById(exitsDto.getId());
        if (existingExitsEntityOpt.isEmpty())  _errorControlUtilities.handleSuccess(null, 3L);
        ExitsEntity existingEntity = existingExitsEntityOpt.get();
        log.info("FOUND EXISTING EXITS ENTITY: {}", EncoderUtilities.formatJson(existingEntity));
        log.warn("PRODUCT ID MISMATCH: EXISTING={} NEW={}", existingEntity.getProductId(), exitsDto.getProductId());
        if (!Objects.equals(existingEntity.getProductId(), exitsDto.getProductId())) return _errorControlUtilities.handleSuccess(null, 31L);
        Optional<InventoryEntity> inventory = _inventoryConsultations.findById(existingEntity.getProductId());
        log.warn("INVENTORY NOT FOUND FOR PRODUCT ID: {}", existingEntity.getProductId());
        if (inventory.isEmpty()) return _errorControlUtilities.handleSuccess(null, 3L);
        _inventoryService.handleEntry(inventory.get().getProductCode(), existingEntity.getQuantity(), existingEntity.getCost(), exitsDto.getUpdateUser());
        log.info("HANDLED ENTRY FOR PRODUCT: {}", inventory.get().getProductCode());
        Optional<OrderEntity> orderEntity = _orderConsultations.findByOrderNumber(exitsDto.getOrderNumber());
        log.warn("ORDER ALREADY EXISTS FOR ORDER NUMBER: {}", exitsDto.getOrderNumber());
        if (orderEntity.isPresent()) return _errorControlUtilities.handleSuccess(null, 20L);
        ExitsEntity exitEntity = parseEnt(exitsDto, existingExitsEntityOpt.get());
        exitEntity.setUpdateUser(exitsDto.getUpdateUser());
        exitEntity.setDateTimeUpdate(new Date().toString());
        ExitsEntity updatedExit = _exitsConsultations.updateData(exitEntity);
        log.info("UPDATED EXIT ENTITY: {}", EncoderUtilities.formatJson(updatedExit));
        _inventoryService.handleExit(inventory.get().getProductCode(), exitsDto.getQuantity(), exitsDto.getUpdateUser());
        ExitsDto updatedExitsDto = parse(updatedExit);
        log.info("UPDATE EXIT ENDED");
        return _errorControlUtilities.handleSuccess(updatedExitsDto, 1L);
    }


    /**
     * Method to handle the exit generation when an order is paid.
     *
     * @param encode Base64 encoded request containing the order number details.
     * @return A ResponseEntity object indicating success or failure.
     */
    @Override
    public ResponseEntity<String> exitOnPayment(String encode) {
        EncoderUtilities.validateBase64(encode);
        log.info("GENERATING EXIT ON PAYMENT BEGINS");
        ExitsDto exitsDto = EncoderUtilities.decodeRequest(encode, ExitsDto.class);
        EncoderUtilities.validator(exitsDto);
        log.info("DECODED EXITS DTO: {}", EncoderUtilities.formatJson(exitsDto));
        Optional<OrderEntity> orderEntityOpt = _orderConsultations.findByOrderNumber(exitsDto.getOrderNumber());
        log.warn("ORDER NOT FOUND FOR ORDER NUMBER: {}", exitsDto.getOrderNumber());
        if (orderEntityOpt.isEmpty()) return _errorControlUtilities.handleSuccess(null, 3L);
        OrderEntity orderEntity = orderEntityOpt.get();
        log.info("FOUND ORDER ENTITY: {}", EncoderUtilities.formatJson(orderEntity));
        log.warn("ORDER STATUS IS NOT PAID: {}", orderEntity.getStatus());
        if (!"PAID".equalsIgnoreCase(orderEntity.getStatus())) return _errorControlUtilities.handleSuccess(null, 25L);
        List<OrderItemEntity> orderItems = orderEntity.getItems();
        log.info("PROCESSING {} ORDER ITEMS.", orderItems.size());
        for (OrderItemEntity item : orderItems) {
            log.info("PROCESSING ITEM: {}", EncoderUtilities.formatJson(item));
            log.warn("INVENTORY NOT FOUND FOR PRODUCT ID: {}", item.getProduct().getId());
            Optional<InventoryEntity> inventory = _inventoryConsultations.findById(item.getProduct().getId());
            if (inventory.isEmpty()) return _errorControlUtilities.handleSuccess(null, 3L);
            _inventoryService.handleExit(inventory.get().getProductCode(), item.getQuantity(), exitsDto.getUpdateUser());
            log.info("HANDLED EXIT FOR PRODUCT: {}", inventory.get().getProductCode());
            ExitsEntity entity = new ExitsEntity();
            Long nextOrderNumber = _exitsConsultations.findMaxConsecutive() + 1;
            log.info("NEXT ORDER NUMBER FOR EXITS: {}", nextOrderNumber);
            entity.setConsecutive(nextOrderNumber);
            entity.setOrderNumber(orderEntity.getOrderNumber());
            entity.setProductId(item.getProduct().getId());
            entity.setQuantity(item.getQuantity());
            entity.setCost(item.getPrice());
            entity.setCreateUser(exitsDto.getCreateUser());
            entity.setDateTimeCreation(new Date().toString());
            String encodeEntity = EncoderUtilities.encodeResponse(entity);
            log.info("ENCODING ENTITY FOR INSERTION: {}", encodeEntity);
            ResponseEntity<String> response = addNew(encodeEntity);
            log.info("ADD NEW RESPONSE: {}", response.getBody());
        }
        log.info("GENERATING EXIT ON PAYMENT ENDED FOR ORDER NUMBER: {}", orderEntity.getOrderNumber());
        return _errorControlUtilities.handleSuccess(null, 1L);
    }


    /**
     * Converts an ExitsEntity object to an ExitsDto object.
     *
     * @param entity The ExitsEntity object to be converted.
     * @return The corresponding ExitsDto object.
     */
    private ExitsDto parse(ExitsEntity entity) {
        ExitsDto exitsDto = new ExitsDto();
        exitsDto.setId(entity.getId());
        exitsDto.setOrderNumber(entity.getOrderNumber());
        exitsDto.setQuantity(entity.getQuantity());
        exitsDto.setCost(entity.getCost());
        exitsDto.setProductId(entity.getProductId());
        exitsDto.setCreateUser(entity.getCreateUser());
        exitsDto.setUpdateUser(entity.getUpdateUser());
        return exitsDto;
    }

    /**
     * Converts an ExitsDto object to an ExitsEntity object.
     *
     * @param dto The ExitsDto object to be converted.
     * @param entities The ExitsEntity object to be updated.
     * @return The updated ExitsEntity object.
     */
    private ExitsEntity parseEnt(ExitsDto dto, ExitsEntity entities) {
        ExitsEntity entity = new ExitsEntity();
        entity.setId(dto.getId());
        entity.setOrderNumber(dto.getOrderNumber());
        entity.setQuantity(dto.getQuantity());
        entity.setCost(dto.getCost());
        entity.setProductId(dto.getProductId());
        entity.setCreateUser(entities.getCreateUser());
        entity.setUpdateUser(entities.getUpdateUser());
        entity.setDateTimeCreation(entities.getDateTimeCreation());
        entity.setDateTimeUpdate(entities.getDateTimeUpdate());
        return entity;
    }
}
