package com.payment.service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.payment.service.dto.request.PaymentRequest;
import com.payment.service.dto.response.RestApiResponse;
import com.payment.service.dto.result.PaymentSaveResult;
import com.payment.service.services.implementation.PaymentServiceImplementation;
import static com.payment.service.utility.RestApiPath.*;
import com.payment.service.services.services.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(PAYMENT_API_PATH)
public class PaymentController {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PaymentServiceImplementation paymentServiceImplementation;

    @PostMapping(PAYMENT_CREATE_API_PATH)
    public RestApiResponse<PaymentSaveResult> createPayment(HttpServletRequest request, @RequestBody PaymentRequest paymentRequest) throws JsonProcessingException, ParseException {
        return paymentService.createPayment((String) request.getAttribute("userRole"), (String) request.getAttribute("userEmail"), paymentRequest);
    }

    @PostMapping(PAYMENT_WEBHOOK_API_PATH)
    public String handleMidtransWebhook(HttpServletRequest request, @RequestBody Map<String, Object> payload) {
        return paymentServiceImplementation.updatePaymentStatus(payload);
    }

    @GetMapping("")
    public RestApiResponse<List<PaymentSaveResult>> getUnpaidPayment(HttpServletRequest request, @RequestParam String status){
        return paymentServiceImplementation.getUnfinishedPayment((String) request.getAttribute("userRole"), (String) request.getAttribute("userEmail"), status);
    }

    @GetMapping("/finished")
    public RestApiResponse<List<PaymentSaveResult>> getFinishedPayment(HttpServletRequest request){
        return paymentServiceImplementation.getFinishedPayment((String) request.getAttribute("userRole"), (String) request.getAttribute("userEmail"));
    }
}