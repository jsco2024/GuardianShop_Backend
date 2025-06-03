package com.ms_security.ms_security.controller;

import com.ms_security.ms_security.persistence.entity.EntriesEntity;
import com.ms_security.ms_security.persistence.entity.ExitsEntity;
import com.ms_security.ms_security.service.IInventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller class for handling inventory-related operations.
 * This class provides endpoints for managing inventory items such as
 * listing, adding, and updating inventory details.
 */
@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final IInventoryService _iInventoryService;

    /**
     * Lists an inventory item by ID.
     *
     * @param entity the ID of the inventory item to be listed
     * @return a ResponseEntity containing the inventory item details
     */
    @Operation(
            description = "LIST INVENTORY BY ID"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
                    @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
            }
    )
    @PostMapping(path = "/list/id", produces = {"application/json"})
    public ResponseEntity<String> findById(@RequestBody String entity){
        return _iInventoryService.findById(entity);
    }

    /**
     * Lists all inventory items.
     *
     * @param entity request body containing pagination or filter criteria
     * @return a ResponseEntity containing the list of all inventory items
     */
    @Operation(
            description = "LIST ALL INVENTORY ITEMS"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
                    @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
            }
    )
    @PostMapping(path = "/list/all", produces = {"application/json"})
    public ResponseEntity<String> findAll(@RequestBody String entity){
        return _iInventoryService.findAll(entity);
    }

    /**
     * Inserts a new inventory item.
     *
     * @param entity request body containing the new inventory item details
     * @return a ResponseEntity indicating the result of the insert operation
     */
    @Operation(
            description = "INSERT A NEW INVENTORY ITEM"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
                    @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
            }
    )
    @PostMapping(path = "/addRecord", produces = {"application/json"})
    public ResponseEntity<String> addNew(@RequestBody String entity){
        return _iInventoryService.addNew(entity);
    }

    /**
     * Updates an existing inventory item.
     *
     * @param entity request body containing the updated inventory item details
     * @return a ResponseEntity indicating the result of the update operation
     */
    @Operation(
            description = "UPDATE AN INVENTORY ITEM"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
                    @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
            }
    )
    @PostMapping(path = "/updateRecord", produces = {"application/json"})
    public ResponseEntity<String> updateData(@RequestBody String entity){
        return _iInventoryService.updateData(entity);
    }

    @Operation(
            description = "LIST ALL ENTRIES FOR A PRODUCT"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
                    @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
            }
    )
    @PostMapping(path = "/listAllEntries", produces = {"application/json"})
    public ResponseEntity<List<EntriesEntity>>entries(@RequestBody String entity){
        return _iInventoryService.getEntries(entity);
    }

    @Operation(
            description = "LIST ALL OUTPUTS OF A PRODUCT"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
                    @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
            }
    )
    @PostMapping(path = "/listAllExits", produces = {"application/json"})
    public ResponseEntity<List<ExitsEntity>> exits(@RequestBody String entity){
        return _iInventoryService.getExits(entity);
    }

//    /**
//     * Updates a batch of inventory items by decoding a Base64 string containing item details,
//     * validating the data, and updating the names of the corresponding inventory items.
//     *
//     * @param entity Base64 encoded string containing the inventory data to update
//     * @return a ResponseEntity indicating the result of the batch update operation
//     */
//    @Operation(
//            description = "UPDATE NAMES OF INVENTORY ITEMS IN BATCH"
//    )
//    @ApiResponses(
//            value = {
//                    @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
//                    @ApiResponse(responseCode = "400", description = "INVALID REQUEST OR DATA", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
//            }
//    )
//    @PostMapping(path = "/updateBatch", produces = {"application/json"})
//    public ResponseEntity<String> updateBatch(@RequestBody String entity) {
//       return _iInventoryService.updateBatch(entity);
//    }
}
