package com.ms_security.ms_security.service.impl;

import com.mercadopago.MercadoPago;
import com.mercadopago.resources.Payment;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.datastructures.payment.Payer;
import com.ms_security.ms_security.service.IMercadoPagoService;
import org.springframework.stereotype.Service;

@Service
public class MercadoPagoImpl implements IMercadoPagoService {

    @Override
    public Payment createPayment(Float amount) throws MPException {
        Payment payment = new Payment();
        payment.setTransactionAmount(amount);
        payment.setDescription("Descripción del pago");
        payment.setPaymentMethodId("visa");
        payment.setPayer(new Payer().setEmail("test@test.com"));
        return payment.save();
    }
}
