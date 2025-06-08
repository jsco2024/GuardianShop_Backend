package com.ms_security.ms_security.controller;

import com.ms_security.ms_security.service.IExitsServices;
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
@RequestMapping("/exits")
@RequiredArgsConstructor
public class ExitsController {

    private final IExitsServices exitsService;

    @Operation(description = "LIST EXIT BY ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @PostMapping(path = "/list/id", produces = {"application/json"})
    public ResponseEntity<String> findById(@RequestBody String encode) {
        return exitsService.findById(encode);
    }

    @Operation(description = "LIST ALL EXITS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @PostMapping(path = "/list/all", produces = {"application/json"})
    public ResponseEntity<String> findAll(@RequestBody String encode) {
        return exitsService.findAll(encode);
    }

//    @Operation(description = "INSERT A NEW EXIT")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
//            @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
//    })
//    @PostMapping(path = "/addRecord", produces = {"application/json"})
//    public ResponseEntity<String> addNew(@RequestBody String encode) {
//        return exitsService.addNew(encode);
//    }
//
//    @Operation(description = "UPDATE AN EXIT")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
//            @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
//    })
//    @PostMapping(path = "/updateRecord", produces = {"application/json"})
//    public ResponseEntity<String> updateData(@RequestBody String encode) {
//        return exitsService.updateData(encode);
//    }
}
