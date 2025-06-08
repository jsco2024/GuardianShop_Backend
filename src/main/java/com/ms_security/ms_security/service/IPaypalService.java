package com.ms_security.ms_security.service;

import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.stereotype.Service;

public interface IPaypalService {

    Payment createPayment(Double total, String currency, String method, String intent, String description) throws PayPalRESTException;


    Payment executePayment(String paymentId, String payerId) throws PayPalRESTException;

}
