package com.payment.service.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RestApiPath {

    public static final String PAYMENT_API_PATH = "/payment";
    public static final String PAYMENT_CREATE_API_PATH = "/create";
    public static final String PAYMENT_WEBHOOK_API_PATH = "/webhook";

}
