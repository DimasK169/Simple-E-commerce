package com.payment.service.services.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.service.dto.request.AuditTrailsRequest;
import com.payment.service.dto.request.PaymentRequest;
import com.payment.service.dto.response.RestApiResponse;
import com.payment.service.dto.result.PaymentSaveResult;
import com.payment.service.dto.result.PaymentUpdateResult;
import com.payment.service.entity.PaymentEntity;
import com.payment.service.repository.PaymentRepository;
import com.payment.service.services.services.AuditTrailsService;
import com.payment.service.services.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PaymentServiceImplementation implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private AuditTrailsService auditTrailsService;

    private static final String MIDTRANS_SERVER_KEY = "SB-Mid-server-oQ9buJM9VzkIKUJxEv7_7wmJ";
    private static final String MIDTRANS_API_URL = "https://api.sandbox.midtrans.com/v2/charge";

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public RestApiResponse<PaymentSaveResult> createPayment(PaymentRequest paymentRequest) throws JsonProcessingException, ParseException {
        PaymentEntity paymentEntity = PaymentEntity.builder()
                .paymentNumber(UUID.randomUUID().toString())
                .paymentStatus("Pending")
                .paymentType(paymentRequest.getPaymentType())
                .paymentPrice(paymentRequest.getPaymentPrice())
                .paymentCreatedDate(new Date())
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encodeServerKey());
        headers.set("Content-Type", "application/json");

        Map<String, Object> body = new HashMap<>();

        body.put("payment_type", paymentRequest.getPaymentType());
        body.put("transaction_details", Map.of(
                "order_id", paymentEntity.getPaymentNumber(),
                "gross_amount", paymentRequest.getPaymentPrice()
        ));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.exchange(MIDTRANS_API_URL, HttpMethod.POST, entity, Map.class);

        String midtransUrl = "https://api.sandbox.midtrans.com/v2/" + paymentEntity.getPaymentNumber() + "/status";
        HttpHeaders headersGet = new HttpHeaders();
        String serverKey = MIDTRANS_SERVER_KEY;
        String encodedAuth = Base64.getEncoder().encodeToString((serverKey + ":").getBytes());
        headersGet.set("Authorization", "Basic " + encodedAuth);
        headersGet.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entityGet = new HttpEntity<>(headersGet);
        ResponseEntity<Map> responseGet = restTemplate.exchange(midtransUrl, HttpMethod.GET, entityGet, Map.class);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (responseGet.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = responseGet.getBody();
            if (responseBody != null) {
                paymentEntity.setPaymentStartDate(sdf.parse(responseBody.get("transaction_time").toString()));
                paymentEntity.setPaymentEndDate(sdf.parse(responseBody.get("expiry_time").toString()));
            }
        }

        paymentEntity.setPaymentThirdParty(response.toString());
        PaymentEntity createPayment = paymentRepository.save(paymentEntity);

        PaymentSaveResult paymentSaveResult = PaymentSaveResult.builder()
                .paymentNumber(createPayment.getPaymentNumber())
                .paymentStatus(createPayment.getPaymentStatus())
                .paymentType(createPayment.getPaymentType())
                .paymentPrice(createPayment.getPaymentPrice())
                .paymentStartDate(createPayment.getPaymentStartDate())
                .paymentEndDate(createPayment.getPaymentEndDate())
                .paymentThirdParty(createPayment.getPaymentThirdParty())
                .paymentCreatedDate(createPayment.getPaymentCreatedDate())
                .build();

        ObjectMapper mapper = new ObjectMapper();

        auditTrailsService.insertAuditTrails(AuditTrailsRequest.builder()
                .auditTrailsAction("Create")
                        .auditTrailsDescription("Creating Audit Trails For New Payment")
                        .auditTrailsDate(new Date())
                        .auditTrailsRequest(mapper.writeValueAsString(paymentRequest))
                        .auditTrailsResponse(mapper.writeValueAsString(paymentSaveResult))
                        .auditTrailsThirdPartyResponse(response.toString())
                .build());

        RestApiResponse restApiResponseCreate = RestApiResponse.builder()
                .code(HttpStatus.CREATED.toString())
                .message("Payment Baru Berhasil Dibuat")
                .data(paymentSaveResult)
                .build();

        return restApiResponseCreate;
    }

//    @Override
//    public RestApiResponse<PaymentEntity> updatePayment(String paymentNumber) {
//        String midtransUrl = "https://api.sandbox.midtrans.com/v2/" + paymentNumber + "/status";
//
//        HttpHeaders headers = new HttpHeaders();
//        String serverKey = MIDTRANS_SERVER_KEY;
//        String encodedAuth = Base64.getEncoder().encodeToString((serverKey + ":").getBytes());
//        headers.set("Authorization", "Basic " + encodedAuth);
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        HttpEntity<String> entity = new HttpEntity<>(headers);
//        ResponseEntity<Map> response = restTemplate.exchange(midtransUrl, HttpMethod.GET, entity, Map.class);
//
//        if (response.getStatusCode() == HttpStatus.OK) {
//            Map<String, Object> responseBody = response.getBody();
//            if (responseBody != null) {
//                String paymentStatus = (String) responseBody.get("transaction_status");
//                String paymentType = (String) responseBody.get("payment_type");
//                String paymentPrice = (String) responseBody.get("gross_amount");
//
//                Optional<PaymentEntity> existingTransaction = paymentRepository.findByPaymentNumber(paymentNumber);
//                if (existingTransaction.isPresent()) {
//                    PaymentEntity paymentEntity = existingTransaction.get();
//                    paymentEntity.setPaymentStatus(paymentStatus);
//                    paymentEntity.setPaymentType(paymentType);
//                    paymentEntity.setPaymentUpdatedDate(new Date());
//                    PaymentEntity updatePayment = paymentRepository.save(paymentEntity);
//
//                    PaymentUpdateResult updateResponse = PaymentUpdateResult.builder()
//                            .paymentNumber(paymentNumber)
//                            .paymentType(paymentType)
//                            .paymentStatus(paymentStatus)
//                            .paymentPrice(paymentPrice)
//                            .paymentUpdatedDate(updatePayment.getPaymentUpdatedDate())
//                            .build();
//
//                    RestApiResponse restApiResponse = RestApiResponse.builder()
//                            .code(HttpStatus.OK.toString())
//                            .message("Pembayaran Berhasil")
//                            .data(paymentEntity)
//                            .error(null)
//                            .build();
//
//                    return restApiResponse;
//                }
//            }
//        }
//        throw new RuntimeException("Failed to update transaction from Midtrans.");
//    }

    @Override
    public String updatePaymentStatus(Map<String, Object> payload) {
        String orderId = (String) payload.get("order_id");
        String transactionStatus = (String) payload.get("transaction_status");

        Optional<PaymentEntity> existingPayment = paymentRepository.findByPaymentNumber(orderId);

        if (existingPayment.isPresent()) {
            PaymentEntity paymentEntity = existingPayment.get();
            paymentEntity.setPaymentStatus(transactionStatus);
            paymentEntity.setPaymentUpdatedDate(new Date());
            paymentRepository.save(paymentEntity);
        }
        return null;
    }

    private String encodeServerKey() {
        return java.util.Base64.getEncoder().encodeToString((MIDTRANS_SERVER_KEY + ":").getBytes());
    }
}
