package com.ms_security.ms_security.controller;

import com.ms_security.ms_security.service.IOrderItemService;
import com.ms_security.ms_security.service.model.dto.CartPaginationRequestDto;
import com.ms_security.ms_security.service.model.dto.CartValidationRequestDto;
import com.ms_security.ms_security.service.model.dto.FindByIdDto;
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
@RequestMapping("/order-items")
@RequiredArgsConstructor
public class OrderItemController {

    private final IOrderItemService orderItemService;

    @Operation(description = "LIST ORDER ITEM BY ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @PostMapping(path = "/list/id", produces = {"application/json"})
    public ResponseEntity<String> findById(@RequestBody String id) {
        return orderItemService.findById(id);
    }

    @Operation(description = "LIST ALL ORDER ITEMS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @PostMapping(path = "/list/all", produces = {"application/json"})
    public ResponseEntity<String> findAll(@RequestBody String entity) {
        return orderItemService.findAll(entity);
    }

//    @Operation(description = "INSERT A NEW ORDER ITEM")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
//            @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
//    })
//    @PostMapping(path = "/addRecord", produces = {"application/json"})
//    public ResponseEntity<String> addNew(@RequestBody String entity) {
//        return orderItemService.addNew(entity);
//    }
//
    @Operation(description = "UPDATE AN ORDER ITEM")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @PostMapping(path = "/updateRecord", produces = {"application/json"})
    public ResponseEntity<String> updateData(@RequestBody String entity) {
        return orderItemService.updateData(entity);
    }

    @Operation(description = "DELETE AN ORDER ITEM BY ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @PostMapping(path = "/delete", produces = {"application/json"})
    public ResponseEntity<String> deleteById(@RequestBody String id) {
        return orderItemService.deleteById(id);
    }

    @Operation(description = "VALIDATE IF CART EXISTS FOR USER")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @PostMapping(path = "/validate/cart", produces = {"application/json"})
    public ResponseEntity<String> findByCartIdAndCreateUser(@RequestBody CartValidationRequestDto requestDto) {
        return orderItemService.findByCartIdAndCreateUser(requestDto.getCreateUser(), requestDto.getCartId());
    }

    @Operation(description = "LIST ITEMS BY CART ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @PostMapping(path = "/list/cart", produces = {"application/json"})
    public ResponseEntity<String> findByCartId(@RequestBody CartPaginationRequestDto request) {
        return orderItemService.findByCartId(request.getCartId(), request.getPage(), request.getSize());
    }
}
