package com.ms_security.ms_security.service;

import com.ms_security.ms_security.service.model.dto.StripePaymentIntentDto;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

public interface IPaymentService {
    PaymentIntent paymentIntent(StripePaymentIntentDto paymentIntentDto) throws StripeException;
    PaymentIntent confirm(String id) throws StripeException;
    PaymentIntent cancel(String id) throws StripeException;
}
