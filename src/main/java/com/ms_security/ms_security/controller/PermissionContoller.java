package com.ms_security.ms_security.controller;

import com.ms_security.ms_security.service.IPermissionService;
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

/**
 * Controller class for handling permission-related operations.
 * This class provides endpoints for managing permissions such as
 * listing, adding, and updating permissions.
 */
@RestController
@RequestMapping("/permission")
@RequiredArgsConstructor
public class PermissionContoller {

    private final IPermissionService _iPermissionService;

    /**
     * Lists a permission by ID.
     *
     * @param entity the ID of the permission to be listed
     * @return a ResponseEntity containing the permission details
     */
    @Operation(
            description = "LIST PERMISSION BY ID"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
                    @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
            }
    )
    @PostMapping(path = "/list/id",produces = {"application/json"})
    public ResponseEntity<String> findById(@RequestBody String entity){
        return _iPermissionService.findById(entity);
    }

    /**
     * Lists all permissions.
     *
     * @param entity request body containing pagination or filter criteria
     * @return a ResponseEntity containing the list of all permissions
     */
    @Operation(
            description = "LIST ALL PERMISSION"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
                    @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
            }
    )
    @PostMapping(path = "/list/all",produces = {"application/json"})
    public ResponseEntity<String> findByAll(@RequestBody String entity){
        return _iPermissionService.findAll(entity);
    }

    /**
     * Inserts a new permission.
     *
     * @param entity request body containing the new permission details
     * @return a ResponseEntity indicating the result of the insert operation
     */
    @Operation(
            description = "INSERT A NEW PERMISSION "
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
                    @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
            }
    )
    @PostMapping(path = "/addRecord",produces = {"application/json"})
    public ResponseEntity<String>  addNew(@RequestBody String entity){
        return _iPermissionService.addNew(entity);
    }

    /**
     * Updates an existing permission.
     *
     * @param entity request body containing the updated permission details
     * @return a ResponseEntity indicating the result of the update operation
     */
    @Operation(
            description = "UPDATE A PERMISSION "
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
                    @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
            }
    )
    @PostMapping(path = "/updateRecord",produces = {"application/json"})
    public ResponseEntity<String>  updateData(@RequestBody String entity){
        return _iPermissionService.updateData(entity);
    }

    /**
     * Lists permissions by role ID.
     *
     * @param encodedRoleId the encoded role ID for which to list permissions
     * @return a ResponseEntity containing the list of permissions for the role
     */
    @Operation(
            description = "LIST PERMISSIONS BY ROLE ID"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
                    @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
            }
    )
    @PostMapping(path = "/list/role", produces = {"application/json"})
    public ResponseEntity<String> findByRoleId(@RequestBody String encodedRoleId) {
        return _iPermissionService.findByRoleId(encodedRoleId);
    }
}

