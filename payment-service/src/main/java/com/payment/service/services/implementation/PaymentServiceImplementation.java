package com.payment.service.services.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.service.dto.request.AuditTrailsRequest;
import com.payment.service.dto.request.PaymentRequest;
import com.payment.service.dto.response.RestApiResponse;
import com.payment.service.dto.result.PaymentSaveResult;
import com.payment.service.entity.cart.CartEntity;
import com.payment.service.entity.payment.PaymentEntity;
import com.payment.service.entity.product.Product;
import com.payment.service.exception.CustomIllegalArgumentException;
import com.payment.service.repository.cart.CartRepository;
import com.payment.service.repository.payment.PaymentRepository;
import com.payment.service.repository.product.ProductRepository;
import com.payment.service.services.services.AuditTrailsService;
import com.payment.service.services.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.payment.service.constant.GeneralConstant.*;

@Service
public class PaymentServiceImplementation implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AuditTrailsService auditTrailsService;

    @Value("${midtrans.server-key}")
    private String midtransKey;

    @Value("${midtrans.charge.api-url}")
    private String midtransCharge;

    @Value("${midtrans.status.api-url}")
    private String midtransStatus;


    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public RestApiResponse<PaymentSaveResult> createPayment(PaymentRequest paymentRequest) throws JsonProcessingException, ParseException {
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("CREATE");
        try{
            List<CartEntity> cartEntity = cartRepository.findByUserEmail(paymentRequest.getUserEmail(), true, false, false);
            if(cartEntity.isEmpty()){
               throw new CustomIllegalArgumentException("Validation Error", Collections.singletonList("Payment Dengan User Email: " + paymentRequest.getUserEmail()));
            }
            System.out.println(cartEntity);

            Integer totalPrice = 0;
            for (CartEntity cartData : cartEntity) {
                totalPrice += cartData.getCartTotalPrice();
            }

            PaymentEntity createPayment = insertPayment(paymentRequest, cartEntity);
            System.out.println("createPayment" + createPayment);
            ResponseEntity<Map> response = postMidtrans(paymentRequest.getPaymentType(), createPayment.getPaymentNumber(), totalPrice);
            System.out.println("response"+response);

            String midtransUrl = String.format(midtransStatus, createPayment.getPaymentNumber());
            ResponseEntity<Map> responseGet = getMidtrans(midtransUrl);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (responseGet.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = responseGet.getBody();
                if (responseBody != null) {
                    createPayment.setPaymentStartDate(sdf.parse(responseBody.get("transaction_time").toString()));
                    createPayment.setPaymentEndDate(sdf.parse(responseBody.get("expiry_time").toString()));
                }
            }
            createPayment.setPaymentStatus(response.getBody().get("transaction_status").toString());
            createPayment.setPaymentThirdParty(response.toString());
            PaymentEntity savePayment = paymentRepository.save(createPayment);

            PaymentSaveResult responsePayment = paymentSaveResult(createPayment, response);

            insertAuditTrails(paymentRequest, responsePayment, response);

            return RestApiResponse.<PaymentSaveResult>builder()
                    .code(HttpStatus.CREATED.toString())
                    .message(AUDIT_CREATE_DESC_ACTION_SUCCESS)
                    .data(responsePayment)
                    .build();

        } catch (ArrayIndexOutOfBoundsException e){
            insertAuditTrails(paymentRequest, null, null);

            throw e;
        }

    }

    @Override
    public String updatePaymentStatus(Map<String, Object> payload) {
        String orderId = (String) payload.get("order_id");
        String transactionStatus = (String) payload.get("transaction_status");

        Optional<PaymentEntity> existingPayment = paymentRepository.findByPaymentNumber(orderId);

        if (existingPayment.isPresent()) {
            PaymentEntity paymentEntity = existingPayment.get();

            if (transactionStatus.equals("settlement")){
                List<CartEntity> cartEntities = cartRepository.findByUserEmail(existingPayment.get().getUserEmail(), true, false, false);
                for (CartEntity updateCart : cartEntities){
                    updateCart.setIsPayed(true);
                    updateCart.setCartUpdatedDate(new Date());
                    cartRepository.save(updateCart);
                }
                paymentEntity.setPaymentStatus(transactionStatus);
                paymentEntity.setPaymentUpdatedDate(new Date());
                paymentRepository.save(paymentEntity);
            }

            if (transactionStatus.equals("expire")){
                List<CartEntity> cartEntities = cartRepository.findByUserEmail(existingPayment.get().getUserEmail(), true, false, false);
                for (CartEntity updateCart : cartEntities){
                    updateCart.setIsFailed(true);
                    updateCart.setCartUpdatedDate(new Date());
                    cartRepository.save(updateCart);

                    List<Product> product = productRepository.findManyProductByCode(updateCart.getProductCode());
                    for (Product updateProduct : product){
                        Integer currentStocks = updateProduct.getProductStock();
                        Integer updateStocks = currentStocks + updateCart.getCartQuantity();
                        updateProduct.setProductStock(updateStocks);
                        updateProduct.setUpdatedDate(new Date());
                        productRepository.save(updateProduct);
                    }
                }
                paymentEntity.setPaymentStatus(transactionStatus);
                paymentEntity.setPaymentUpdatedDate(new Date());
                paymentRepository.save(paymentEntity);
            }

        }
        return null;
    }

    private String encodeServerKey() {
        return java.util.Base64.getEncoder().encodeToString((midtransKey + ":").getBytes());
    }

    private PaymentEntity insertPayment (PaymentRequest paymentRequest, List<CartEntity> cartEntity){
        return PaymentEntity.builder()
                .userId(cartEntity.get(0).getUserId())
                .userEmail(cartEntity.get(0).getUserEmail())
                .paymentNumber(UUID.randomUUID().toString())
                .paymentType(paymentRequest.getPaymentType())
                .paymentCreatedDate(new Date())
                .build();
    }

    private ResponseEntity<Map> postMidtrans(String type, String orderId, Integer totalPrice){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encodeServerKey());
        headers.set("Content-Type", "application/json");

        System.out.println("type" + type + " oi" + orderId + " tp" + totalPrice);
        Map<String, Object> body = new HashMap<>();
        body.put("payment_type", type);
        body.put("transaction_details", Map.of(
                "order_id", orderId,
                "gross_amount", totalPrice
        ));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.exchange(midtransCharge, HttpMethod.POST, entity, Map.class);
        return response;
    }

    private ResponseEntity<Map> getMidtrans(String url){
        HttpHeaders headersGet = new HttpHeaders();
        String encodedAuth = Base64.getEncoder().encodeToString((midtransKey + ":").getBytes());
        headersGet.set("Authorization", "Basic " + encodedAuth);
        headersGet.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entityGet = new HttpEntity<>(headersGet);
        ResponseEntity<Map> responseGet = restTemplate.exchange(url, HttpMethod.GET, entityGet, Map.class);

        return responseGet;
    }

    private PaymentSaveResult paymentSaveResult(PaymentEntity createPayment, ResponseEntity<Map> response){
        return PaymentSaveResult.builder()
                .paymentNumber(createPayment.getPaymentNumber())
                .paymentStatus(createPayment.getPaymentStatus())
                .paymentType(createPayment.getPaymentType())
                .cartTotalPrice(createPayment.getPaymentPrice())
                .paymentStartDate(createPayment.getPaymentStartDate())
                .paymentEndDate(createPayment.getPaymentEndDate())
                .paymentThirdParty(response)
                .paymentCreatedDate(createPayment.getPaymentCreatedDate())
                .build();
    }

    private void
    insertAuditTrails(PaymentRequest paymentRequest, PaymentSaveResult paymentSaveResult, ResponseEntity<Map> response) throws JsonProcessingException {
        System.out.println("psr" + paymentRequest + "response" + response);
        ObjectMapper mapper = new ObjectMapper();
        auditTrailsService.insertAuditTrails(AuditTrailsRequest.builder()
                .auditTrailsAction(AUDIT_CREATE_ACTION)
                .auditTrailsDescription(AUDIT_CREATE_DESC_ACTION_SUCCESS)
                .auditTrailsDate(new Date())
                .auditTrailsRequest(mapper.writeValueAsString(paymentRequest))
                .auditTrailsResponse(mapper.writeValueAsString(paymentSaveResult))
                .auditTrailsThirdPartyResponse(response.toString())
                .build());
    }
}
