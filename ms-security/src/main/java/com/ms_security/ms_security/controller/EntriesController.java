package com.ms_security.ms_security.controller;

import com.ms_security.ms_security.service.IEntriesServices;
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

@RestController
@RequestMapping("/entries")
@RequiredArgsConstructor
public class EntriesController {

    private final IEntriesServices entriesService;

    @Operation(description = "LIST ENTRY BY ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @PostMapping(path = "/list/id", produces = {"application/json"})
    public ResponseEntity<String> findById(@RequestBody String encode) {
        return entriesService.findById(encode);
    }

    @Operation(description = "LIST ALL ENTRIES")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @PostMapping(path = "/list/all", produces = {"application/json"})
    public ResponseEntity<String> findAll(@RequestBody String encode) {
        return entriesService.findAll(encode);
    }

    @Operation(description = "INSERT A NEW ENTRY")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @PostMapping(path = "/addRecord", produces = {"application/json"})
    public ResponseEntity<String> addNew(@RequestBody String encode) {
        return entriesService.addNew(encode);
    }

    @Operation(description = "UPDATE AN ENTRY")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @PostMapping(path = "/updateRecord", produces = {"application/json"})
    public ResponseEntity<String> updateData(@RequestBody String encode) {
        return entriesService.updateData(encode);
    }
}
