package com.ms_security.ms_security.service.impl;

import com.ms_security.ms_security.persistence.entity.*;
import com.ms_security.ms_security.service.IInventoryService;
import com.ms_security.ms_security.service.IParametersService;
import com.ms_security.ms_security.service.impl.consultations.InventoryConsultations;
import com.ms_security.ms_security.service.impl.consultations.ServicesConsultations;
import com.ms_security.ms_security.service.model.dto.FindByPageDto;
import com.ms_security.ms_security.service.model.dto.InventoryDto;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class InventoryImpl implements IInventoryService {

    private final InventoryConsultations _inventoryConsultations;
    private final ServicesConsultations _servicesConsultations;
    private final IParametersService _iParametersService;
    private final ErrorControlUtilities _errorControlUtilities;

    @Override
    public ResponseEntity<String> findById(String encode) {
        log.info("PROCEED TO SEARCH BY ID");
        EncoderUtilities.validateBase64(encode);
        InventoryDto findByIdDto = EncoderUtilities.decodeRequest(encode, InventoryDto.class);
        EncoderUtilities.validator(findByIdDto);
        log.info(EncoderUtilities.formatJson(findByIdDto));
        Optional<InventoryEntity> inventoryEntity = _inventoryConsultations.findById(findByIdDto.getId());
        if (inventoryEntity.isEmpty()) return _errorControlUtilities.handleSuccess(null, 3L);
        InventoryDto inventoryDto = parse(inventoryEntity.get());
        log.info("SEARCH BY ID IS ENDED");
        return _errorControlUtilities.handleSuccess(inventoryDto, 1L);
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
        Page<InventoryEntity> pageResult = _inventoryConsultations.findAll(pageable);
        List<InventoryDto> inventoryDto = pageResult.stream()
                .map(this::parse)
                .toList();
        PageImpl<InventoryDto> response = new PageImpl<>(inventoryDto, pageable, pageResult.getTotalElements());
        log.info("PAGINATED SEARCH COMPLETED");
        return _errorControlUtilities.handleSuccess(response, 1L);
    }


    @Override
    public ResponseEntity<String> addNew(String encode) {
        EncoderUtilities.validateBase64(encode);
        log.info("INSERT BEGINS");
        InventoryDto inventoryDto = EncoderUtilities.decodeRequest(encode, InventoryDto.class);
        EncoderUtilities.validator(inventoryDto, InventoryDto.Create.class);
        log.info(EncoderUtilities.formatJson(inventoryDto));
        log.info("START SEARCH BY PRINCIPAL PRODUCT");
        Optional<ServicesEntity> servicesEntity = _servicesConsultations.findById(inventoryDto.getServiceId());
        if (servicesEntity.isEmpty()) return _errorControlUtilities.handleSuccess(null, 24L);
        log.info("END SEARCH BY PRINCIPAL PRODUCT");
        String serviceCode = servicesEntity.get().getCode().toString();
        String derivedCode = serviceCode + "-" + inventoryDto.getProductCode();
        String serviceName = servicesEntity.get().getName();
        String sizeName = serviceName + " " + inventoryDto.getReference();
        log.info("START SEARCH BY DERIVED PRODUCT");
        Optional<InventoryEntity> derived = _inventoryConsultations.findByProductCode(derivedCode);
        if (derived.isPresent()) return _errorControlUtilities.handleSuccess(null, 15L);
        log.info("END SEARCH BY DERIVED PRODUCT");
        InventoryEntity existingEntity = parseEnt(inventoryDto, new InventoryEntity());
        existingEntity.setProductCode(derivedCode);
        existingEntity.setName(sizeName);
        existingEntity.setReference(inventoryDto.getReference());
        existingEntity.setCreateUser(inventoryDto.getCreateUser());
        existingEntity.setDateTimeCreation(new Date().toString());
        InventoryEntity inventoryEntity = _inventoryConsultations.addNew(existingEntity);
        InventoryDto inventoryDtos = parse(inventoryEntity);
        log.info("INSERT ENDED");
        return _errorControlUtilities.handleSuccess(inventoryDtos, 1L);
    }

    @Override
    public ResponseEntity<String> updateData(String encode) {
        EncoderUtilities.validateBase64(encode);
        log.info("UPDATE BEGINS");
        InventoryDto inventoryDto = EncoderUtilities.decodeRequest(encode, InventoryDto.class);
        EncoderUtilities.validator(inventoryDto, InventoryDto.Update.class);
        log.info(EncoderUtilities.formatJson(inventoryDto));
        log.info("START SEARCH BY ID");
        Optional<InventoryEntity> inventory= _inventoryConsultations.findById(inventoryDto.getId());
        if (inventory.isEmpty()) return _errorControlUtilities.handleSuccess(null, 3L);
        log.info("END SEARCH BY ID");
        log.info("START SEARCH BY PRINCIPAL PRODUCT");
        Optional<ServicesEntity> servicesEntity = _servicesConsultations.findById(inventoryDto.getServiceId());
        if (servicesEntity.isEmpty()) return _errorControlUtilities.handleSuccess(null, 24L);
        log.info("END SEARCH BY PRINCIPAL PRODUCT");
        String serviceName = servicesEntity.get().getName();
        String sizeName = serviceName + " " + inventoryDto.getReference();
        InventoryEntity existingEntity = inventory.get();
        if (!existingEntity.getProductCode().equals(inventoryDto.getProductCode())) return _errorControlUtilities.handleSuccess(null, 16L);
        existingEntity.setProductCode(inventory.get().getProductCode());
        existingEntity.setName(sizeName);
        existingEntity.setReference(inventoryDto.getReference());
        existingEntity.setUpdateUser(inventoryDto.getUpdateUser());
        existingEntity.setDateTimeUpdate(new Date().toString());
        InventoryEntity updatedInventory = _inventoryConsultations.updateData(existingEntity);
        InventoryDto inventoryDtos = parse(updatedInventory);
        log.info("UPDATE ENDED");
        return _errorControlUtilities.handleSuccess(inventoryDtos, 1L);
    }

//    @Override
//    public ResponseEntity<String> updateBatch(String encode) {
//        EncoderUtilities.validateBase64(encode);
//        log.info("SE INICIA LA ACTUALIZACIÓN DE NOMBRES POR LOTES");
//        List<InventoryDto> inventoryDtos = EncoderUtilities.decodeRequestList(encode, InventoryDto.class);
//        EncoderUtilities.validator(inventoryDtos, InventoryDto.Update.class);
//        log.info(EncoderUtilities.formatJson(inventoryDtos));
//        List<InventoryEntity> inventoryEntities = inventoryDtos.stream()
//                .map(this::parseInventoryTransform)
//                .toList();
//        log.info("SE INICIA VERIFICACIÓN DE EXISTENCIA");
//        List<InventoryEntity> existingEntities = _inventoryConsultations.findAllByIds(
//                inventoryEntities.stream().map(InventoryEntity::getId).toList());
//        if (existingEntities.size() != inventoryEntities.size()) return _errorControlUtilities.handleSuccess(null, 19L);
//        for (InventoryEntity existingEntity : existingEntities) {
//            Optional<InventoryEntity> newEntity = inventoryEntities.stream()
//                    .filter(ent -> ent.getId().equals(existingEntity.getId()))
//                    .findFirst();
//            newEntity.ifPresent(entity -> existingEntity.setName(entity.getName()));
//        }
//        List<InventoryEntity> updatedEntities = _inventoryConsultations.updateBatch(existingEntities);
//        List<InventoryDto> updatedDtos = updatedEntities.stream()
//                .map(this::parse)
//                .toList();
//        log.info("SE FINALIZA LA ACTUALIZACIÓN DE NOMBRES POR LOTES");
//        return _errorControlUtilities.handleSuccess(updatedDtos, 1L);
//    }


    @Override
    public ResponseEntity<List<EntriesEntity>> getEntries(String encode) {
        log.info("PROCEED TO SEARCH ENTRIES");
        EncoderUtilities.validateBase64(encode);
        InventoryDto inventoryDto = EncoderUtilities.decodeRequest(encode, InventoryDto.class);
        EncoderUtilities.validator(inventoryDto);
        Optional<InventoryEntity> inventoryEntity = _inventoryConsultations.findByProductCode(inventoryDto.getProductCode());
        if (inventoryEntity.isEmpty()) return _errorControlUtilities.handleSuccessList(null, 24L);
        List<EntriesEntity> entries = inventoryEntity.get().getEntries();
        log.info("SEARCH FOR ENTRIES IS ENDED");
        return _errorControlUtilities.handleSuccessList(entries, 1L);
    }

    @Override
    public ResponseEntity<List<ExitsEntity>> getExits(String encode) {
        log.info("PROCEED TO SEARCH EXITS");
        EncoderUtilities.validateBase64(encode);
        InventoryDto inventoryDto = EncoderUtilities.decodeRequest(encode, InventoryDto.class);
        EncoderUtilities.validator(inventoryDto);
        Optional<InventoryEntity> inventoryEntity = _inventoryConsultations.findByProductCode(inventoryDto.getProductCode());
        if (inventoryEntity.isEmpty()) return _errorControlUtilities.handleSuccessList(null, 24L);
        List<ExitsEntity> exits = inventoryEntity.get().getExits();
        log.info("SEARCH FOR EXITS IS ENDED");
        return _errorControlUtilities.handleSuccessList(exits, 1L);
    }

    @Override
    public void handleEntry(String productId, Long quantity, BigDecimal purchasePrice, String updateUser) {
        if (quantity <= 0) {
            _errorControlUtilities.handleSuccess(null, 17L);
            return;
        }
        Optional<InventoryEntity> inventoryOpt = _inventoryConsultations.findByProductCode(productId);
        if (inventoryOpt.isPresent()) {
            InventoryEntity inventory = inventoryOpt.get();
            Long currentStock = inventory.getStock() != null ? inventory.getStock() : 0L;
            Long newStock = currentStock + quantity;
            BigDecimal oldTotalCost = calculateTotalCost(inventory);
            BigDecimal newAverageCost = calculateAverageCost(oldTotalCost, purchasePrice, quantity, newStock);
            inventory.setStock(newStock);
            inventory.setAverageCost(newAverageCost);
            inventory.setTotalCost(oldTotalCost.add(purchasePrice.multiply(new BigDecimal(quantity))));
            inventory.setSalePrice(calculateSalePrice(newAverageCost));
            inventory.setUpdateUser(updateUser);
            inventory.setLastEntryDate(new Date().toString());
            _inventoryConsultations.updateData(inventory);
        } else _errorControlUtilities.handleSuccessList(null, 24L);
    }




    @Override
    public void handleExit(String productId, Long quantity, String updateUser) {
        if (quantity <= 0) {
            _errorControlUtilities.handleSuccess(null, 17L);
            return;
        }
        Optional<InventoryEntity> inventoryOpt = _inventoryConsultations.findByProductCode(productId);
        if (inventoryOpt.isPresent()) {
            InventoryEntity inventory = inventoryOpt.get();
            if (inventory.getPendingStock() < quantity) _errorControlUtilities.handleSuccess(null, 17L);
            inventory.setPendingStock(inventory.getPendingStock() - quantity.intValue());
            inventory.setUpdateUser(updateUser);
            inventory.setLastExitDate(new Date().toString());
            _inventoryConsultations.updateData(inventory);
        } else _errorControlUtilities.handleSuccess(null, 24L);
    }

    @Override
    public void stockReturned(String productId, Long quantity, String updateUser) {
        if (quantity <= 0) {
            _errorControlUtilities.handleSuccess(null, 17L);
            return;
        }
        Optional<InventoryEntity> inventoryOpt = _inventoryConsultations.findByProductCode(productId);
        if (inventoryOpt.isPresent()) {
            InventoryEntity inventory = inventoryOpt.get();
            inventory.setPendingStock(inventory.getPendingStock() - quantity.intValue());
            inventory.setReturnedStock(quantity);
            inventory.setStock(inventory.getStock() + inventory.getReturnedStock());
            inventory.setReturnedStock(inventory.getReturnedStock() - quantity.intValue());
            inventory.setLastEntryDate(new Date().toString());
            inventory.setUpdateUser(updateUser);
            _inventoryConsultations.updateData(inventory);
        } else _errorControlUtilities.handleSuccess(null, 24L);
    }

    @Override
    public void stockExit(String productId, Long quantity, String updateUser) {
        if (quantity <= 0) {
            _errorControlUtilities.handleSuccess(null, 17L);
            return;
        }
        Optional<InventoryEntity> inventoryOpt = _inventoryConsultations.findByProductCode(productId);
        log.info("producID: {}", productId);
        if (inventoryOpt.isPresent()) {
            InventoryEntity inventory = inventoryOpt.get();
            if (inventory.getStock() < quantity) {
                _errorControlUtilities.handleSuccess(null, 17L);
                return;
            }
            inventory.setStock(inventory.getStock() - quantity.intValue());
            inventory.setPendingStock(quantity);
            log.info("quantity: {}", quantity);
            inventory.setLastExitDate(new Date().toString());
            inventory.setUpdateUser(updateUser);
            _inventoryConsultations.updateData(inventory);
        } else {
            _errorControlUtilities.handleSuccess(null, 24L);
        }
    }

    @Override
    public void stockAdjustment(String productId, Long quantity, String updateUser) {
        if (quantity <= 0) {
            _errorControlUtilities.handleSuccess(null, 17L);
            return;
        }
        Optional<InventoryEntity> inventoryOpt = _inventoryConsultations.findByProductCode(productId);
        if (inventoryOpt.isPresent()) {
            InventoryEntity inventory = inventoryOpt.get();
            if (inventory.getStock() < quantity) {
                _errorControlUtilities.handleSuccess(null, 17L);
                return;
            }
            inventory.setStock(inventory.getStock() - quantity.intValue());
            inventory.setAdjustedStock(quantity);
            inventory.setLastExitDate(new Date().toString());
            inventory.setUpdateUser("SYSTEM");
            _inventoryConsultations.updateData(inventory);
        } else {
            _errorControlUtilities.handleSuccess(null, 24L);
        }
    }



    @Override
    @Scheduled(cron = "59 59 23 L * ?")
    public void closeMonthlyInventory() {
        List<InventoryEntity> inventoryList = _inventoryConsultations.getsAll();
        for (InventoryEntity inventory : inventoryList) {
            inventory.setMonthlyClosingDate(LocalDate.now().toString());
            inventory.setClosedQuantity(inventory.getStock());
            _inventoryConsultations.updateData(inventory);
            log.info("CLOSING INVENTORY FOR THE PRODUCT: {}", inventory.getProductCode());
        }
    }


    @Override
    @Scheduled(cron = "0 0 0 1 * ?")
    public void openInitialInventory() {
        List<InventoryEntity> inventoryList = _inventoryConsultations.getsAll();
        for (InventoryEntity inventory : inventoryList) {
            inventory.setInitialStock(inventory.getStock());
            inventory.setPendingStock(inventory.getPendingStock());
            inventory.setCanceledStock(inventory.getCanceledStock());
            inventory.setReturnedStock(inventory.getStock());
            inventory.setAdjustedStock(inventory.getAdjustedStock());
            _inventoryConsultations.updateData(inventory);
            log.info("INITIAL INVENTORY OPENED FOR THE PRODUCT: {}", inventory.getProductCode());
        }
    }

    private BigDecimal calculateSalePrice(BigDecimal averageCost) {
        if (averageCost == null) return BigDecimal.ZERO;
        Optional<ParametersEntity> incrementParam = _iParametersService.findByCodeParameter(2L);
        if (incrementParam.isPresent()) {
            BigDecimal incrementPercentage = new BigDecimal(incrementParam.get().getParameter());
            BigDecimal multiplier = BigDecimal.ONE.add(incrementPercentage.divide(BigDecimal.valueOf(100)));
            return averageCost.multiply(multiplier);
        } else {
            throw new IllegalArgumentException("SALE PRICE INCREMENT PARAMETER NOT FOUND");
        }
    }

    private BigDecimal calculateTotalCost(InventoryEntity inventory) {
        BigDecimal averageCost = inventory.getAverageCost() != null ? inventory.getAverageCost(): BigDecimal.valueOf(0L);
        Long stock = inventory.getStock() != null ? inventory.getStock() : 0L;
        return averageCost.multiply(new BigDecimal(stock));
    }

    private BigDecimal calculateAverageCost(BigDecimal oldTotalCost, BigDecimal purchasePrice, Long quantity, Long stock) {
        stock = (stock != null) ? stock : 0L;
        BigDecimal newTotalCost = oldTotalCost.add(purchasePrice.multiply(new BigDecimal(quantity))); // Costo total antiguo + (Precio de compra * Cantidad)
        return (stock > 0) ? newTotalCost.divide(new BigDecimal(stock), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO; // Costo promedio
    }



    public InventoryDto parse(InventoryEntity entity) {
        if (entity == null) return null;
        return InventoryDto.builder()
                .id(entity.getId())
                .productCode(entity.getProductCode())
                .name(entity.getName())
                .reference(entity.getReference())
                .unitOfMeasure(entity.getUnitOfMeasure())
                .initialStock(entity.getInitialStock())
                .stock(entity.getStock())
                .pendingStock(entity.getPendingStock())
                .canceledStock(entity.getCanceledStock())
                .returnedStock(entity.getReturnedStock())
                .adjustedStock(entity.getAdjustedStock())
                .salePrice(entity.getSalePrice())
                .cost(entity.getCost())
                .averageCost(entity.getAverageCost())
                .totalCost(entity.getTotalCost())
                .status(entity.getStatus())
                .serviceId(entity.getServiceId())
                .createUser(entity.getCreateUser())
                .updateUser(entity.getUpdateUser())
                .lastEntryDate(entity.getLastEntryDate())
                .lastExitDate(entity.getLastExitDate())
                .entries(entity.getEntries())
                .exits(entity.getExits())
                .build();
    }


    public static InventoryEntity parseEnt(InventoryDto dto, InventoryEntity inventory) {
        if (dto == null) return null;

        // Si ya existe una instancia de InventoryEntity, la reutilizamos.
        InventoryEntity entity = inventory != null ? inventory : new InventoryEntity();

        entity.setId(dto.getId());
        entity.setProductCode(dto.getProductCode());
        entity.setName(dto.getName());
        entity.setReference(dto.getReference());
        entity.setUnitOfMeasure(dto.getUnitOfMeasure());
        entity.setInitialStock(dto.getInitialStock());
        entity.setStock(dto.getStock());
        entity.setPendingStock(dto.getPendingStock());
        entity.setCanceledStock(dto.getCanceledStock());
        entity.setReturnedStock(dto.getReturnedStock());
        entity.setAdjustedStock(dto.getAdjustedStock());
        entity.setSalePrice(dto.getSalePrice());
        entity.setCost(dto.getCost());
        entity.setAverageCost(dto.getAverageCost());
        entity.setTotalCost(dto.getTotalCost());
        entity.setStatus(dto.getStatus());
        entity.setServiceId(dto.getServiceId());
        if (inventory != null) {
            entity.setCreateUser(inventory.getCreateUser());
            entity.setUpdateUser(inventory.getUpdateUser());
            entity.setDateTimeCreation(inventory.getDateTimeCreation());
            entity.setDateTimeUpdate(inventory.getDateTimeUpdate());
        } else {
            entity.setCreateUser(dto.getCreateUser());
            entity.setUpdateUser(dto.getUpdateUser());
        }
        entity.setLastEntryDate(inventory.getLastEntryDate());
        entity.setLastExitDate(inventory.getLastExitDate());
        entity.setEntries(dto.getEntries());
        entity.setExits(dto.getExits());
        return entity;
    }


    private InventoryEntity parseInventoryTransform(InventoryDto inventoryDto) {
        InventoryEntity inventoryEntity = new InventoryEntity();
        inventoryEntity.setId(inventoryDto.getId());
        inventoryEntity.setProductCode(inventoryDto.getProductCode());
        inventoryEntity.setReference(inventoryDto.getReference());
        inventoryEntity.setName(inventoryDto.getName());
        inventoryEntity.setServiceId(inventoryDto.getServiceId());
        inventoryEntity.setCreateUser(inventoryDto.getCreateUser());
        inventoryEntity.setUpdateUser(inventoryDto.getUpdateUser());
        inventoryEntity.setDateTimeUpdate(new Date().toString());
        return inventoryEntity;
    }

}
