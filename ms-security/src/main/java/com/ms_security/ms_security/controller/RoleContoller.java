package com.ms_security.ms_security.controller;

import com.ms_security.ms_security.service.IRoleService;
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
 * Controller class for handling role-related operations.
 * This class provides endpoints for managing roles such as
 * listing, adding, and updating roles.
 */
@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleContoller {

    private final IRoleService _iRoleService;

    /**
     * Lists a role by ID.
     *
     * @param entity the ID of the role to be listed
     * @return a ResponseEntity containing the role details
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
        return _iRoleService.findById(entity);
    }

    /**
     * Lists all roles.
     *
     * @param entity request body containing pagination or filter criteria
     * @return a ResponseEntity containing the list of all roles
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
        return _iRoleService.findAll(entity);
    }

    /**
     * Inserts a new role.
     *
     * @param entity request body containing the new role details
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
        return _iRoleService.addNew(entity);
    }

    /**
     * Updates an existing role.
     *
     * @param entity request body containing the updated role details
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
        return _iRoleService.updateData(entity);
    }

    /**
     * Gets a role along with its associated permissions.
     *
     * @param entity the ID of the role
     * @return a ResponseEntity containing the role details along with permissions
     */
    @Operation(
            summary = "GET ROLE WITH PERMISSIONS",
            description = "FETCHES A ROLE ALONG WITH ITS ASSOCIATED PERMISSIONS BY ROLE ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful Operation", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "400", description = "Invalid ID Supplied", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "404", description = "Role Not Found", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @PostMapping("/list/permissions")
    public ResponseEntity<String> getRoleWhitPermission(@RequestBody String entity) {
        return _iRoleService.findRoleWithPermissionById(entity);
    }

    /**
          * Fetches a user along with their associated roles by user ID.
          *
          * @param entity the ID of the user whose roles are to be fetched
          * @return a ResponseEntity containing the user and their roles
          */
    @Operation(
            summary = "GET USER WITH ROLES",
            description = "FETCHES A USER ALONG WITH THEIR ASSOCIATED ROLES BY USER ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful Operation", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "400", description = "Invalid ID Supplied", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "404", description = "User Not Found", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @PostMapping("/list/roles")
    public ResponseEntity<String> getUserWithRoles(@RequestBody String entity) {
            return _iRoleService.findRoleWithPermissionById(entity);
    }
}
