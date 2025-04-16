package com.payment.service.services.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.payment.service.dto.request.PaymentRequest;
import com.payment.service.dto.response.RestApiResponse;
import com.payment.service.dto.result.PaymentSaveResult;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

@Service
public interface PaymentService {

    RestApiResponse<PaymentSaveResult> createPayment(PaymentRequest paymentRequest) throws JsonProcessingException, ParseException;
//    RestApiResponse<PaymentEntity> updatePayment(String paymentNumber);
    RestApiResponse<List<PaymentSaveResult>> getUnfinishedPayment(PaymentRequest paymentRequest);
    String updatePaymentStatus(Map<String, Object> payload);

}
