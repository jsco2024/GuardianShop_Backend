package com.ms_security.ms_security.controller;

import com.ms_security.ms_security.service.IServicesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for handling service-related operations.
 * This class provides endpoints for managing services such as
 * listing, adding, and updating services.
 */
@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
public class ServicesContoller {
    
    private final IServicesService _iServicesService;

    /**
     * Lists a service by ID.
     *
     * @param entity the ID of the service to be listed
     * @return a ResponseEntity containing the service details
     */
    @Operation(
            description = "LIST SERVISES BY ID"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
                    @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
            }
    )
    @PostMapping(path = "/list/id",produces = {"application/json"})
    public ResponseEntity<String> findById(@RequestBody String entity){
        return _iServicesService.findById(entity);
    }

    /**
     * Lists all services.
     *
     * @param entity request body containing pagination or filter criteria
     * @return a ResponseEntity containing the list of all services
     */
    @Operation(
            description = "LIST ALL SERVISES"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
                    @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
            }
    )
    @PostMapping(path = "/list/all",produces = {"application/json"})
    public ResponseEntity<String> findByAll(@RequestBody String entity){
        return _iServicesService.findAll(entity);
    }

    /**
     * Inserts a new service.
     *
     * @param entity request body containing the new service details
     * @return a ResponseEntity indicating the result of the insert operation
     */
    @Operation(
            description = "INSERT A NEW SERVISES "
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
                    @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
            }
    )
    @PostMapping(path = "/addRecord",produces = {"application/json"})
    public ResponseEntity<String>  addNew(@RequestBody String entity){
        return _iServicesService.addNew(entity);
    }

    /**
     * Updates an existing service.
     *
     * @param entity request body containing the updated service details
     * @return a ResponseEntity indicating the result of the update operation
     */
    @Operation(
            description = "UPDATE A SERVISES "
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
                    @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
            }
    )
    @PostMapping(path = "/updateRecord",produces = {"application/json"})
    public ResponseEntity<String>  updateData(@RequestBody String entity){
        return _iServicesService.updateData(entity);
    }

    @Operation(summary = "GET SERVICE WITH INVENTORY DETAILS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SERVICE AND INVENTORY DETAILS FOUND"),
            @ApiResponse(responseCode = "404", description = "SERVICE NOT FOUND"),
            @ApiResponse(responseCode = "400", description = "INVALID INPUT")
    })
    @PostMapping("/inventory")
    public ResponseEntity<String> getServiceWithInventory(@RequestBody String encode) {
        return _iServicesService.findServiceWithInventory(encode);
    }

}
