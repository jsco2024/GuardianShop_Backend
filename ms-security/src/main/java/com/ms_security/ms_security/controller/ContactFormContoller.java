package com.ms_security.ms_security.controller;


import com.ms_security.ms_security.service.IContactFormService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/form")
@RequiredArgsConstructor
public class ContactFormContoller {

    /**
     * Retrieves a contact form by its ID.
     *
     * @param entity the JSON string containing the ID of the contact form to retrieve
     * @return the ResponseEntity with the retrieved contact form data or an error message
     */
    private final IContactFormService _iContactFormService;
    @Operation(
            description = "LIST FORM BY ID"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
                    @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
            }
    )
    @PostMapping(path = "/list/id",produces = {"application/json"})
    public ResponseEntity<String> findById(@RequestBody String entity){
        return _iContactFormService.findById(entity);
    }

    /**
     * Retrieves all contact forms with pagination.
     *
     * @param entity the JSON string containing pagination and filter criteria
     * @return the ResponseEntity with the list of contact forms or an error message
     */
    @Operation(description = "LIST ALL FORM WITH PAGINATION")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @PostMapping(path = "/list/all", produces = {"application/json"})
    public ResponseEntity<String> findByAll(@RequestBody String entity){
        return _iContactFormService.findAll(entity);
    }

    /**
     * Adds a new contact form.
     *
     * @param entity the JSON string containing the new contact form data
     * @return the ResponseEntity indicating the result of the operation
     */
    @Operation(
            description = "INSERT A NEW FORM "
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
                    @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
            }
    )
    @PostMapping(path = "/addRecord",produces = {"application/json"})
    public ResponseEntity<String>  addNew(@RequestBody String entity){
        return _iContactFormService.addNew(entity);
    }

    /**
     * Updates an existing contact form.
     *
     * @param entity the JSON string containing the updated contact form data
     * @return the ResponseEntity indicating the result of the operation
     */
    @Operation(
            description = "UPDATE A FORM "
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
                    @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
            }
    )
    @PostMapping(path = "/updateRecord",produces = {"application/json"})
    public ResponseEntity<String>  updateData(@RequestBody String entity){
        return _iContactFormService.updateData(entity);
    }


}
