package com.ms_security.ms_security.controller;

import com.ms_security.ms_security.service.IPaymentService;
import com.ms_security.ms_security.service.model.dto.StripePaymentIntentDto;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stripe")
@RequiredArgsConstructor
public class StripePaymentController {

    private final IPaymentService _iPaymentService;

    @PostMapping("/paymentIntent")
    public ResponseEntity<String> payment(@RequestBody StripePaymentIntentDto paymentIntentDto) {
        try {
            PaymentIntent paymentIntent = _iPaymentService.paymentIntent(paymentIntentDto);
            String paymentStr = paymentIntent.toJson();
            return new ResponseEntity<>(paymentStr, HttpStatus.OK);
        } catch (StripeException e) {
            return new ResponseEntity<>("Error creating payment intent: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/confirm")
    public ResponseEntity<String> confirm(@PathVariable("id") String id) {
        try {
            PaymentIntent paymentIntent = _iPaymentService.confirm(id);
            String paymentStr = paymentIntent.toJson();
            return new ResponseEntity<>(paymentStr, HttpStatus.OK);
        } catch (StripeException e) {
            return new ResponseEntity<>("Error confirming payment intent: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/cancel")
    public ResponseEntity<String> cancel(@PathVariable("id") String id) {
        try {
            PaymentIntent paymentIntent = _iPaymentService.cancel(id);
            String paymentStr = paymentIntent.toJson();
            return new ResponseEntity<>(paymentStr, HttpStatus.OK);
        } catch (StripeException e) {
            return new ResponseEntity<>("Error canceling payment intent: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}

