package com.payment.service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.payment.service.dto.request.PaymentRequest;
import com.payment.service.dto.response.RestApiResponse;
import com.payment.service.dto.result.PaymentSaveResult;
import com.payment.service.dto.result.PaymentUpdateResult;
import com.payment.service.entity.PaymentEntity;
import com.payment.service.services.implementation.PaymentServiceImplementation;
import com.payment.service.services.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PaymentServiceImplementation paymentServiceImplementation;

    @PostMapping("/create")
    public RestApiResponse<PaymentSaveResult> createPayment(@RequestBody PaymentRequest paymentRequest) throws JsonProcessingException, ParseException {
        return paymentService.createPayment(paymentRequest);
    }

//    @PutMapping("/update/{paymentNumber}")
//    public RestApiResponse<PaymentEntity> updateTransaction(@PathVariable String paymentNumber) {
//        return paymentService.updatePayment(paymentNumber);
//    }

    @PostMapping("/webhook")
    public String handleMidtransWebhook(@RequestBody Map<String, Object> payload) {
        return paymentServiceImplementation.updatePaymentStatus(payload);
    }
}