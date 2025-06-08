package com.ms_security.ms_security.service.impl;

import com.ms_security.ms_security.persistence.entity.EntriesEntity;
import com.ms_security.ms_security.persistence.entity.InventoryEntity;
import com.ms_security.ms_security.persistence.entity.ParametersEntity;
import com.ms_security.ms_security.service.IEntriesServices;
import com.ms_security.ms_security.service.IInventoryService;
import com.ms_security.ms_security.service.IParametersService;
import com.ms_security.ms_security.service.IServicesService;
import com.ms_security.ms_security.service.impl.consultations.EntriesConsultations;
import com.ms_security.ms_security.service.impl.consultations.InventoryConsultations;
import com.ms_security.ms_security.service.model.dto.EntriesDto;
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
public class EntriesImpl implements IEntriesServices {

    private final EntriesConsultations _entriesConsultations;
    private final IInventoryService _inventoryService;
    private final InventoryConsultations _inventoryConsultations;
    private final IServicesService _servicesService;
    private final IParametersService _iParametersService;
    private final ErrorControlUtilities _errorControlUtilities;

    @Override
    public ResponseEntity<String> findById(String encode) {
        log.info("PROCEED TO SEARCH ENTRY BY ID");
        EncoderUtilities.validateBase64(encode);
        EntriesDto findByIdDto = EncoderUtilities.decodeRequest(encode, EntriesDto.class);
        EncoderUtilities.validator(findByIdDto);
        log.info(EncoderUtilities.formatJson(findByIdDto));
        Optional<EntriesEntity> entryEntity = _entriesConsultations.findById(findByIdDto.getId());
        if (entryEntity.isEmpty()) return _errorControlUtilities.handleSuccess(null, 3L);
        EntriesDto entriesDto = parse(entryEntity.get());
        log.info("SEARCH ENTRY BY ID IS ENDED");
        return _errorControlUtilities.handleSuccess(entriesDto, 1L);
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
        Pageable pageable = PageRequest.of(
                request.getPage() - 1,
                Integer.parseInt(pageSizeParam.get().getParameter()));
        Page<EntriesEntity> pageResult = _entriesConsultations.findAll(pageable);
        List<EntriesDto> entriesDtos = pageResult.stream()
                .map(this::parse)
                .toList();
        PageImpl<EntriesDto> response = new PageImpl<>(entriesDtos, pageable, pageResult.getTotalElements());
        log.info("PAGINATED SEARCH COMPLETED");
        return _errorControlUtilities.handleSuccess(response, 1L);
    }


    @Override
    public ResponseEntity<String> addNew(String encode) {
        EncoderUtilities.validateBase64(encode);
        log.info("INSERT ENTRY BEGINS");
        EntriesDto entriesDto = EncoderUtilities.decodeRequest(encode, EntriesDto.class);
        EncoderUtilities.validator(entriesDto);
        log.info(EncoderUtilities.formatJson(entriesDto));
        log.info("START SEARCH BY INVOICE NUMBER");
        Optional<EntriesEntity> entriesEntity = _entriesConsultations.findByInvoiceNumber(entriesDto.getInvoiceNumber());
        if (entriesEntity.isPresent()) return _errorControlUtilities.handleSuccess(null, 23L);
        log.info("END SEARCH BY INVOICE NUMBER");
        EntriesEntity entity = parseEnt(entriesDto, new EntriesEntity());
        Long nextOrderNumber = _entriesConsultations.findMaxConsecutive() + 1;
        entity.setConsecutive(nextOrderNumber);
        entity.setCreateUser(entriesDto.getCreateUser());
        entity.setDateTimeCreation(new Date().toString());
        EntriesEntity createdEntry = _entriesConsultations.addNew(entity);
        Optional<InventoryEntity> inventory = _inventoryConsultations.findById(entity.getProductId());
        if (inventory.isEmpty()) return _errorControlUtilities.handleSuccess(null, 3L);
        _inventoryService.handleEntry(inventory.get().getProductCode(),entriesDto.getQuantity(), createdEntry.getCost(), entriesDto.getUpdateUser());
        Optional<InventoryEntity> inventoryEntity = _inventoryConsultations.findByProductCode(inventory.get().getProductCode());
        _servicesService.updateServiceSalePrice(inventory.get().getServiceId(), inventoryEntity.get().getSalePrice());
        log.info("serviceId: {}", inventory.get().getServiceId());
        log.info("salePrice: {}", inventoryEntity.get().getSalePrice());
        EntriesDto createdEntriesDto = parse(createdEntry);
        log.info("INSERT ENTRY ENDED");
        return _errorControlUtilities.handleSuccess(createdEntriesDto, 1L);
    }

    @Override
    public ResponseEntity<String> updateData(String encode) {
        EncoderUtilities.validateBase64(encode);
        log.info("UPDATE ENTRY BEGINS");
        EntriesDto entriesDto = EncoderUtilities.decodeRequest(encode, EntriesDto.class);
        EncoderUtilities.validator(entriesDto);
        log.info(EncoderUtilities.formatJson(entriesDto));
        log.info("START SEARCH BY ID");
        Optional<EntriesEntity> existingEntriesEntity = _entriesConsultations.findById(entriesDto.getId());
        if (existingEntriesEntity.isEmpty()) return _errorControlUtilities.handleSuccess(null, 3L);
        log.info("END SEARCH BY ID");
        EntriesEntity existingEntity = existingEntriesEntity.get();
        if (!Objects.equals(existingEntity.getProductId(), entriesDto.getProductId())) return _errorControlUtilities.handleSuccess(null,31L);
        Optional<InventoryEntity> inventory = _inventoryConsultations.findById(existingEntity.getProductId());
        if (inventory.isEmpty()) return _errorControlUtilities.handleSuccess(null, 3L);
        _inventoryService.stockAdjustment(inventory.get().getProductCode(), existingEntity.getQuantity(), entriesDto.getUpdateUser());
        EntriesEntity entity = parseEnt(entriesDto, existingEntity);
        entity.setInvoiceNumber(existingEntity.getInvoiceNumber());
        entity.setConsecutive(existingEntity.getConsecutive());
        entity.setUpdateUser(entriesDto.getCreateUser());
        entity.setDateTimeUpdate(new Date().toString());
        EntriesEntity updatedEntry = _entriesConsultations.updateData(entity);
        _inventoryService.handleEntry(inventory.get().getProductCode(),entriesDto.getQuantity(), updatedEntry.getCost(), entriesDto.getUpdateUser());
        Optional<InventoryEntity> inventoryEntity = _inventoryConsultations.findByProductCode(inventory.get().getProductCode());
        _servicesService.updateServiceSalePrice(inventory.get().getServiceId(), inventoryEntity.get().getSalePrice());
        log.info("serviceId: {}", inventory.get().getServiceId());
        log.info("salePrice: {}", inventoryEntity.get().getSalePrice());
        EntriesDto updatedEntriesDto = parse(updatedEntry);
        log.info("UPDATE ENTRY ENDED");
        return _errorControlUtilities.handleSuccess(updatedEntriesDto, 1L);
    }

    @Override
    public void returnToStock(Long productId, Long quantity, String updateUser) {
        Optional<InventoryEntity> inventoryEntity = _inventoryConsultations.findById(productId);
        if (inventoryEntity.isPresent()) {
            _inventoryService.stockReturned(inventoryEntity.get().getProductCode(), quantity, updateUser);
        }else _errorControlUtilities.handleSuccess(null, 3L);
    }

    private EntriesDto parse(EntriesEntity entity) {
        EntriesDto entriesDto = new EntriesDto();
        entriesDto.setId(entity.getId());
        entriesDto.setInvoiceNumber(entity.getInvoiceNumber());
        entriesDto.setQuantity(entity.getQuantity());
        entriesDto.setCost(entity.getCost());
        entriesDto.setProductId(entity.getProductId());
        return entriesDto;
    }

    private EntriesEntity parseEnt(EntriesDto dto, EntriesEntity entries) {
        EntriesEntity entity = new EntriesEntity();
        entity.setId(dto.getId());
        entity.setInvoiceNumber(dto.getInvoiceNumber());
        entity.setQuantity(dto.getQuantity());
        entity.setCost(dto.getCost());
        entity.setProductId(dto.getProductId());
        entity.setCreateUser(entries.getCreateUser());
        entity.setUpdateUser(entries.getUpdateUser());
        return entity;
    }
}
