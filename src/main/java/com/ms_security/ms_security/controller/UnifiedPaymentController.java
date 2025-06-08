package com.ms_security.ms_security.controller;

import com.mercadopago.exceptions.MPException;
import com.ms_security.ms_security.service.IMercadoPagoService;
import com.ms_security.ms_security.service.IPaypalService;
import com.ms_security.ms_security.service.IPaymentService;
import com.ms_security.ms_security.service.model.dto.ResponsePaymentDto;
import com.ms_security.ms_security.service.model.dto.StripePaymentIntentDto;
import com.ms_security.ms_security.service.model.dto.UnifiedPaymentDto;
import com.ms_security.ms_security.utilities.ErrorControlUtilities;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class UnifiedPaymentController {

    private final IMercadoPagoService mercadoPagoService;
    private final IPaypalService paypalService;
    private final IPaymentService stripePaymentService;
    private final ErrorControlUtilities _errorControlUtilities;

    @PostMapping("/pay")
    public ResponseEntity<?> processPayment(@RequestBody UnifiedPaymentDto paymentDto) {
        try {
            String paymentMethod = paymentDto.getPaymentMethod();
            String currency = paymentDto.getCurrency();
            String description = paymentDto.getDescription();
            String approvalUrl; // Variable para almacenar la URL de aprobación

            switch (paymentMethod.toLowerCase()) {
                case "mercadopago":
                    // Lógica para Mercado Pago
                    com.mercadopago.resources.Payment mpPayment = mercadoPagoService.createPayment(paymentDto.getAmount());
                    approvalUrl = String.valueOf(mpPayment.getPointOfInteraction());
                    return ResponseEntity.status(302).location(URI.create(approvalUrl)).build();

                case "paypal":
                    // Lógica para PayPal
                    Payment paypalPayment = paypalService.createPayment(paymentDto.getAmount().doubleValue(), currency, "paypal", "sale", description);
                    approvalUrl = paypalPayment.getLinks().stream()
                            .filter(link -> link.getRel().equalsIgnoreCase("approval_url"))
                            .findFirst()
                            .map(link -> link.getHref())
                            .orElse(null);
                    return ResponseEntity.status(302).location(URI.create(approvalUrl)).build();

                case "stripe":
                    // Lógica para Stripe
                    StripePaymentIntentDto stripePaymentDto = new StripePaymentIntentDto();
                    stripePaymentDto.setAmount(paymentDto.getAmount().intValue() * 100); // Convertir a centavos
                    stripePaymentDto.setCurrency(StripePaymentIntentDto.Currency.valueOf(currency));
                    stripePaymentDto.setDescription(description);
                    PaymentIntent stripePaymentIntent = stripePaymentService.paymentIntent(stripePaymentDto);

                    // Devuelve el client_secret para Stripe
                    return ResponseEntity.ok(new ResponsePaymentDto(stripePaymentIntent.getId(), stripePaymentIntent.getClientSecret()));

                default:
                    return ResponseEntity.badRequest().body("Método de pago no soportado.");
            }
        } catch (PayPalRESTException e) {
            return ResponseEntity.status(500).body("Error al procesar el pago con PayPal: " + e.getMessage());
        } catch (MPException e) {
            return ResponseEntity.status(500).body("Error al procesar el pago con Mercado Pago: " + e.getMessage());
        } catch (StripeException e) {
            return ResponseEntity.status(500).body("Error al procesar el pago con Stripe: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error inesperado: " + e.getMessage());
        }
    }

}

