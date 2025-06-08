package com.ms_security.ms_security.service;

import com.ms_security.ms_security.persistence.entity.EntriesEntity;
import com.ms_security.ms_security.persistence.entity.ExitsEntity;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

public interface IInventoryService {

    /**
     * Retrieves a service by its ID.
     *
     * @param encode the encoded ID of the service
     * @return a ResponseEntity containing the service details or an error message
     */
    ResponseEntity<String> findById(String encode);

    /**
     * Retrieves all services.
     *
     * @param encode the encoded parameter for retrieval
     * @return a ResponseEntity containing a list of all services or an error message
     */
    ResponseEntity<String> findAll(String encode);

    /**
     * Adds a new service.
     *
     * @param encode the encoded details of the service to be added
     * @return a ResponseEntity indicating the success or failure of the addition
     */
    ResponseEntity<String> addNew(String encode);

    /**
     * Updates an existing service.
     *
     * @param encode the encoded details of the service to be updated
     * @return a ResponseEntity indicating the success or failure of the update
     */
    ResponseEntity<String> updateData(String encode);

    ResponseEntity<List<EntriesEntity>> getEntries(String encode);
    ResponseEntity<List<ExitsEntity>> getExits(String encode);

    void handleEntry(String productId, Long quantity, BigDecimal purchasePrice, String updateUser);
    void handleExit(String productId, Long quantity, String updateUser);

    void closeMonthlyInventory();
    void openInitialInventory();
    void stockReturned(String productId, Long quantity, String updateUser);
    void stockExit(String productId, Long quantity, String updateUser);

    void stockAdjustment(String productId, Long quantity, String updateUser);

//    public ResponseEntity<String> updateBatch(String encode);
}
