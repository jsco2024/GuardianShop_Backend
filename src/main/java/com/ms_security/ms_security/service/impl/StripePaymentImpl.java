package com.ms_security.ms_security.service.impl;

import com.ms_security.ms_security.service.IPaymentService;
import com.ms_security.ms_security.service.model.dto.StripePaymentIntentDto;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Service
@RequiredArgsConstructor
public class StripePaymentImpl implements IPaymentService {
    @Value("${stripe.key.secret}")
    private String secretKey;

    @Override
    public PaymentIntent paymentIntent(StripePaymentIntentDto paymentIntentDto) throws StripeException {
        Stripe.apiKey = secretKey;
        Map<String, Object> params = new HashMap<>();
        params.put("amount", paymentIntentDto.getAmount());
        params.put("description", paymentIntentDto.getDescription());
        params.put("currency", paymentIntentDto.getCurrency().toString());
        ArrayList<String> payment_Method_Types = new ArrayList<>();
        payment_Method_Types.add("card");
        params.put("payment_method_types", payment_Method_Types);
        return PaymentIntent.create(params);
    }

    @Override
    public PaymentIntent confirm(String id) throws StripeException {
        Stripe.apiKey = secretKey;
        PaymentIntent paymentIntent = PaymentIntent.retrieve(id);
        Map<String, Object> params = new HashMap<>();
        params.put("payment_method", "pm_card_visa");
        return paymentIntent.confirm(params);
    }

    @Override
    public PaymentIntent cancel(String id) throws StripeException {
        Stripe.apiKey = secretKey;
        PaymentIntent paymentIntent = PaymentIntent.retrieve(id);
        paymentIntent.cancel();
        return paymentIntent;
    }
}
