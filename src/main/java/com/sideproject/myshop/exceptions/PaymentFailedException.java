package com.sideproject.myshop.exceptions;

import com.stripe.exception.StripeException;

public class PaymentFailedException extends RuntimeException {
    public PaymentFailedException(String s){
        super(s);
    }
    public PaymentFailedException(String s, Throwable cause) {
        super(s, cause);
    }
}
