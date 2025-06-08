package com.ms_security.ms_security.controller;



import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.Payment;
import com.ms_security.ms_security.service.IMercadoPagoService;
import com.ms_security.ms_security.service.model.dto.MercadoPagoPaymentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mercadopago")
@RequiredArgsConstructor
public class MercadoPagoController {

    private final IMercadoPagoService mercadoPagoService;

    @PostMapping("/pay")
    public ResponseEntity<String> pay(@RequestBody MercadoPagoPaymentDto mercadoPagoPaymentDto) {
        try {
            Payment payment = mercadoPagoService.createPayment(mercadoPagoPaymentDto.getAmount());
            return ResponseEntity.ok(payment.toString());
        } catch (MPException e) {
            throw new RuntimeException(e);
        }
    }
}
