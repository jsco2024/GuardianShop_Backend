package com.ms_security.ms_security.controller;

import com.ms_security.ms_security.service.IOrderService;
import com.ms_security.ms_security.service.model.dto.OrderDto;
import com.ms_security.ms_security.service.model.dto.UnifiedPaymentDto;
import com.ms_security.ms_security.utilities.EncoderUtilities;
import com.ms_security.ms_security.utilities.ErrorControlUtilities;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Log4j2
public class OrderController {

    private final IOrderService orderService;
    private final RestTemplate restTemplate;
    private final ErrorControlUtilities _errorControlUtilities;

    @Operation(description = "LIST ORDER BY ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @PostMapping(path = "/list/id", produces = {"application/json"})
    public ResponseEntity<String> findById(@RequestBody String id) {
        return orderService.findById(id);
    }

    @Operation(description = "LIST ALL ORDERS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @PostMapping(path = "/list/all", produces = {"application/json"})
    public ResponseEntity<String> findAll(@RequestBody String entity) {
        return orderService.findAll(entity);
    }

    @Operation(description = "INSERT A NEW ORDER")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @PostMapping(path = "/addRecord", produces = {"application/json"})
    public ResponseEntity<String> addNew(@RequestBody String entity) {
        return orderService.addNew(entity);
    }

    @Operation(description = "UPDATE AN ORDER")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @PostMapping(path = "/updateRecord", produces = {"application/json"})
    public ResponseEntity<String> updateData(@RequestBody String entity) {
        return orderService.updateData(entity);
    }

    @Operation(description = "CHECKOUT ORDER")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESSFUL OPERATION", content = @Content(schema = @Schema(implementation = ResponseEntity.class))),
            @ApiResponse(responseCode = "400", description = "GENERAL ERROR", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    @PostMapping(path = "/checkout", produces = {"application/json"})
    public ResponseEntity<String> checkout(@RequestBody String cartIdBase64) {

        // Validar y decodificar el cartId desde base64
        EncoderUtilities.validateBase64(cartIdBase64);
        log.info("Received cartIdBase64: " + cartIdBase64);

        // Realizar el proceso de checkout llamando al servicio
        ResponseEntity<String> checkoutResponse = orderService.checkout(cartIdBase64);

        // Verificar si el proceso de checkout fue exitoso
        if (checkoutResponse.getStatusCode().is2xxSuccessful()) {
            log.info("Checkout successful. Decoding response into OrderDto.");

            // Decodificar la respuesta del servicio a un OrderDto
            OrderDto orderDto = EncoderUtilities.decodeRequest(checkoutResponse.getBody(), OrderDto.class);

            if (orderDto == null) {
                log.error("Failed to decode OrderDto from checkout response.");
                return _errorControlUtilities.handleSuccess(null, 22L); // Error si no hay detalles de la orden
            }

            // URL del controlador de pago
            String paymentUrl = "http://localhost:8082/payment/pay";

            // Preparar el DTO para el pago
            UnifiedPaymentDto paymentDto = orderDto.getUnifiedPaymentDto();

            // Hacer la llamada al controlador de pago usando RestTemplate
            ResponseEntity<String> paymentResponse = restTemplate.postForEntity(paymentUrl, paymentDto, String.class);

            // Verificar la respuesta del servicio de pago
            if (!paymentResponse.getStatusCode().is2xxSuccessful()) {
                log.info("PAYMENT FAILED");
                return _errorControlUtilities.handleSuccess(null, 26L); // Manejar error de pago
            }

            log.info("PAYMENT SUCCESSFUL, PROCEEDING WITH ORDER FINALIZATION");

            // Actualizar el estado de la orden a "PAID"
            orderDto.setStatus("PAID");
            return _errorControlUtilities.handleSuccess(orderDto, 1L); // Retornar la respuesta exitosa
        } else {
            return checkoutResponse; // Manejar el error en el proceso de checkout
        }
    }

}