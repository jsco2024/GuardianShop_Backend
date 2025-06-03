package com.ms_security.ms_security.controller;

import com.ms_security.ms_security.service.IPaypalService;
import com.ms_security.ms_security.service.model.dto.PaypalPaymentDto;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/paypal")
@RequiredArgsConstructor
public class PayPalController {

    private final IPaypalService _payPalService;

    @PostMapping("/pay")
    public ResponseEntity<String> pay(@RequestBody PaypalPaymentDto paymentDto) {
        try {
            Payment payment = _payPalService.createPayment(paymentDto.getAmount(), "USD", "paypal", "sale", "Payment description");
            return ResponseEntity.ok(payment.toJSON());
        } catch (PayPalRESTException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping("/success")
    public ResponseEntity<String> success(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) {
        try {
            Payment payment = _payPalService.executePayment(paymentId, payerId);
            return ResponseEntity.ok(payment.toJSON());
        } catch (PayPalRESTException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping("/cancel")
    public ResponseEntity<String> cancel() {
        return ResponseEntity.ok("Payment canceled");
    }
}

