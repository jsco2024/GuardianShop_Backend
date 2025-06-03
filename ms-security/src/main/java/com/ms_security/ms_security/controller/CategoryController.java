package com.ms_security.ms_security.controller;

import com.ms_security.ms_security.service.ICategoryService;
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
 * Controller class for handling category-related operations.
 * This class provides endpoints for managing categories such as
 * listing, adding, and updating categories.
 */
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final ICategoryService _iCategoryService;

    /**
     * Lists a category by ID.
     *
     * @param entity the ID of the category to be listed
     * @return a ResponseEntity containing the category details
     */
    @Operation(
            description = "LIST CATEGORIES BY ID"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
                    @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
            }
    )
    @PostMapping(path = "/list/id", produces = {"application/json"})
    public ResponseEntity<String> findById(@RequestBody String entity){
        return _iCategoryService.findById(entity);
    }

    /**
     * Lists all categories.
     *
     * @param entity request body containing pagination or filter criteria
     * @return a ResponseEntity containing the list of all categories
     */
    @Operation(
            description = "LIST ALL CATEGORIES"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
                    @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
            }
    )
    @PostMapping(path = "/list/all", produces = {"application/json"})
    public ResponseEntity<String> findAll(@RequestBody String entity){
        return _iCategoryService.findAll(entity);
    }

    /**
     * Inserts a new category.
     *
     * @param entity request body containing the new category details
     * @return a ResponseEntity indicating the result of the insert operation
     */
    @Operation(
            description = "INSERT A NEW CATEGORY"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
                    @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
            }
    )
    @PostMapping(path = "/addRecord", produces = {"application/json"})
    public ResponseEntity<String> addNew(@RequestBody String entity){
        return _iCategoryService.addNew(entity);
    }

    /**
     * Updates an existing category.
     *
     * @param entity request body containing the updated category details
     * @return a ResponseEntity indicating the result of the update operation
     */
    @Operation(
            description = "UPDATE A CATEGORY"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
                    @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
            }
    )
    @PostMapping(path = "/updateRecord", produces = {"application/json"})
    public ResponseEntity<String> updateData(@RequestBody String entity){
        return _iCategoryService.updateData(entity);
    }
}
