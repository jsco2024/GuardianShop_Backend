package com.ms_security.ms_security.controller;

import com.ms_security.ms_security.service.IUserService;
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
 * Controller class for handling user-related operations.
 * This class provides endpoints for managing users such as
 * listing, adding, updating users, and fetching users with roles.
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService _iUserService;

    /**
     * Lists a user by ID.
     *
     * @param entity the ID of the user to be listed
     * @return a ResponseEntity containing the user details
     */
    @Operation(
            description = "LIST USER BY ID"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
                    @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
            }
    )
    @PostMapping(path = "/list/id", produces = {"application/json"})
    public ResponseEntity<String> findById(@RequestBody String entity) {
        return _iUserService.findById(entity);
    }

    /**
     * Lists all users.
     *
     * @param entity request body containing pagination or filter criteria
     * @return a ResponseEntity containing the list of all users
     */
    @Operation(
            description = "LIST ALL USERS"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
                    @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
            }
    )
    @PostMapping(path = "/list/all", produces = {"application/json"})
    public ResponseEntity<String> findByAll(@RequestBody String entity) {
        return _iUserService.findAll(entity);
    }

    /**
     * Inserts a new user.
     *
     * @param entity request body containing the new user details
     * @return a ResponseEntity indicating the result of the insert operation
     */
    @Operation(
            description = "INSERT A NEW USER"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
                    @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
            }
    )
    @PostMapping(path = "/addRecord", produces = {"application/json"})
    public ResponseEntity<String> addNew(@RequestBody String entity) {
        return _iUserService.addNew(entity);
    }

    /**
     * Updates an existing user.
     *
     * @param entity request body containing the updated user details
     * @return a ResponseEntity indicating the result of the update operation
     */
    @Operation(
            description = "UPDATE A USER"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
                    @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
            }
    )
    @PostMapping(path = "/updateRecord", produces = {"application/json"})
    public ResponseEntity<String> updateData(@RequestBody String entity) {
        return _iUserService.updateData(entity);
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
            return _iUserService.findUserWithRolesById(entity);
    }
}
