package com.ms_security.ms_security.controller;

import com.ms_security.ms_security.service.ICartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {

    private final ICartService cartService;

    @Operation(description = "LIST CART BY ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @PostMapping(path = "/list/id", produces = {"application/json"})
    public ResponseEntity<String> findById(@RequestBody String id) {
        return cartService.findById(id);
    }

    @Operation(description = "LIST ALL CARTS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @PostMapping(path = "/list/all", produces = {"application/json"})
    public ResponseEntity<String> findAll(@RequestBody String entity) {
        return cartService.findAll(entity);
    }

    @Operation(description = "INSERT A NEW CART")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @PostMapping(path = "/addRecord", produces = {"application/json"})
    public ResponseEntity<String> addNew(@RequestBody String entity) {
        return cartService.addNew(entity);
    }

    @Operation(description = "UPDATE A CART")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @PostMapping(path = "/updateRecord", produces = {"application/json"})
    public ResponseEntity<String> updateData(@RequestBody String entity) {
        return cartService.updateData(entity);
    }

    @Operation(description = "ADD ITEM TO CART")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @PostMapping(path = "/addToCart", produces = {"application/json"})
    public ResponseEntity<String> addToCart(@RequestBody String entity) {
        return cartService.addToCart(entity);
    }

    @Operation(description = "REMOVE ITEM FROM CART")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @PostMapping(path = "/removeItem", produces = {"application/json"})
    public ResponseEntity<String> removeItemFromCart(@RequestBody String entity) {
        return cartService.removeItemFromCart(entity);
    }

    @Operation(description = "DELETE A CART")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @PostMapping(path = "/deleteCart", produces = {"application/json"})
    public ResponseEntity<String> deleteCart(@RequestBody String entity) {
        return cartService.deleteCart(entity);
    }

    @Operation(description = "CHECK IF USER HAS AN EXISTING CART")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @PostMapping(path = "/findCartByUser", produces = {"application/json"})
    public ResponseEntity<String> checkIfUserHasCart(@RequestBody String encodedUserId) {
        return cartService.checkIfUserHasCart(encodedUserId);
    }

}
