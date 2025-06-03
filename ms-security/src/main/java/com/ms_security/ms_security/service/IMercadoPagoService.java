package com.ms_security.ms_security.service;


import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.Payment;
import org.springframework.stereotype.Service;

public interface IMercadoPagoService {
    Payment createPayment(Float amount) throws MPException;
}
