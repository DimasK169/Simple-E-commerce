package com.payment.service.services.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.service.dto.request.AuditTrailsRequest;
import com.payment.service.dto.request.PaymentRequest;
import com.payment.service.dto.response.RestApiResponse;
import com.payment.service.dto.result.PaymentProductSaved;
import com.payment.service.dto.result.PaymentSaveResult;
import com.payment.service.entity.cart.CartEntity;
import com.payment.service.entity.payment.PaymentEntity;
import com.payment.service.entity.product.Product;
import com.payment.service.exception.CustomIllegalArgumentException;
import com.payment.service.exception.UnauthorizedException;
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
import java.util.stream.Collectors;

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
    public RestApiResponse<PaymentSaveResult> createPayment(String userRole, String userEmail, PaymentRequest paymentRequest) throws JsonProcessingException, ParseException {

        try{

            if (!userRole.equals("Customer")) throw new UnauthorizedException(PAYMENT_ROLE_WRONG);

            List<CartEntity> cartEntity = cartRepository.findByUserEmail(userEmail, "N/A", false, false);
            if(cartEntity.isEmpty()) throw new CustomIllegalArgumentException("Validation Error", Collections.singletonList(CART_NOT_FOUND));

            Integer totalPrice = 0;

            PaymentEntity createNewPayment = insertPayment(paymentRequest, cartEntity);

            for (CartEntity cartData : cartEntity) {
                totalPrice += cartData.getCartTotalPrice();
                cartData.setPaymentNumber(createNewPayment.getPaymentNumber());
                cartRepository.save(cartData);
            }
            createNewPayment.setPaymentPrice(totalPrice);

            ResponseEntity<Map> response = postMidtrans(paymentRequest.getPaymentType(), createNewPayment.getPaymentNumber(), totalPrice);
            String midtransUrl = String.format(midtransStatus, createNewPayment.getPaymentNumber());
            ResponseEntity<Map> responseGet = getMidtrans(midtransUrl);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (responseGet.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = responseGet.getBody();
                if (responseBody != null) {
                    createNewPayment.setPaymentStartDate(sdf.parse(responseBody.get("transaction_time").toString()));
                    createNewPayment.setPaymentEndDate(sdf.parse(responseBody.get("expiry_time").toString()));
                }
            }
            createNewPayment.setPaymentStatus(response.getBody().get("transaction_status").toString());
            createNewPayment.setPaymentThirdParty(response.toString());
            PaymentEntity savePayment = paymentRepository.save(createNewPayment);

            PaymentSaveResult responsePayment = paymentSaveResult(createNewPayment, response);

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
        if (existingPayment.isEmpty()) throw new CustomIllegalArgumentException("Validation Error", Collections.singletonList(PAYMENT_NOT_FOUND));

        if (existingPayment.isPresent()) {
            PaymentEntity paymentEntity = existingPayment.get();

            if (transactionStatus.equals("settlement")){
                List<CartEntity> cartEntities = cartRepository.findByUserEmailAndPaymentNumber(existingPayment.get().getUserEmail(), existingPayment.get().getPaymentNumber(), false, false);
                if (cartEntities.isEmpty()) throw new CustomIllegalArgumentException("Validation Error", Collections.singletonList(CART_NOT_FOUND));
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
                List<CartEntity> cartEntities = cartRepository.findByUserEmailAndPaymentNumber(existingPayment.get().getUserEmail(), existingPayment.get().getPaymentNumber(),false, false);
                if (cartEntities.isEmpty()) throw new CustomIllegalArgumentException("Validation Error", Collections.singletonList(CART_NOT_FOUND));
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

    @Override
    public RestApiResponse<List<PaymentSaveResult>> getPayment(String userRole, String userEmail) {

        try {
            if (!userRole.equals("Customer")) throw new UnauthorizedException(PAYMENT_ROLE_WRONG);

            List<PaymentEntity> allPayments = paymentRepository.findPaymentByEmail(userEmail);
            if (allPayments.isEmpty()) throw new CustomIllegalArgumentException("Validation Error", Collections.singletonList(PAYMENT_NOT_FOUND));

            List<PaymentEntity> paymentEntities = allPayments.stream()
                    .filter(p -> p.getPaymentStatus().equalsIgnoreCase("settlement") ||
                            p.getPaymentStatus().equalsIgnoreCase("expire") || p.getPaymentStatus().equalsIgnoreCase("pending"))
                    .collect(Collectors.toList());

            List<PaymentSaveResult> results = new ArrayList<>();


            for (PaymentEntity getPayment : paymentEntities) {
                System.out.println(getPayment.getPaymentStatus() + " " + getPayment.getPaymentNumber());

                List<CartEntity> cartEntities;
                if (getPayment.getPaymentStatus().equalsIgnoreCase("settlement")) {
                    cartEntities = cartRepository.findByUserEmailAndPaymentNumber(userEmail, getPayment.getPaymentNumber(), true, false);
                    if (cartEntities.isEmpty()) throw new CustomIllegalArgumentException("Validation Error", Collections.singletonList(CART_NOT_FOUND));
                } else if (getPayment.getPaymentStatus().equalsIgnoreCase("expire")){
                    cartEntities = cartRepository.findByUserEmailAndPaymentNumber(userEmail, getPayment.getPaymentNumber(), false, true);
                    if (cartEntities.isEmpty()) throw new CustomIllegalArgumentException("Validation Error", Collections.singletonList(CART_NOT_FOUND));
                } else {
                    cartEntities = cartRepository.findByUserEmailAndPaymentNumber(userEmail, getPayment.getPaymentNumber(), false, false);
                    if (cartEntities.isEmpty()) throw new CustomIllegalArgumentException("Validation Error", Collections.singletonList(CART_NOT_FOUND));
                }

                if (cartEntities.isEmpty()) throw new CustomIllegalArgumentException("Validation Error", Collections.singletonList(CART_NOT_FOUND));

                Integer totalPrice = 0;
                List<PaymentProductSaved> savedProduct = new ArrayList<>();
                for (CartEntity getCart : cartEntities) {
                    PaymentProductSaved products = new PaymentProductSaved();
                    products.setProductQuantity(getCart.getCartQuantity().toString());
                    products.setProductName(getCart.getProductName());
                    products.setCartTotalPricePerItem(getCart.getCartTotalPrice().toString());
                    totalPrice += getCart.getCartTotalPrice();
                    savedProduct.add(products);
                }

                PaymentSaveResult result = getPayment(getPayment, totalPrice);
                result.setProduct(savedProduct);
                results.add(result);
            }

            return RestApiResponse.<List<PaymentSaveResult>>builder()
                    .code(AUDIT_GET_ACTION)
                    .message(AUDIT_GET_DESC_ACTION_SUCCESS)
                    .data(results)
                    .build();

        } catch (CustomIllegalArgumentException e) {
            throw e;
        }
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

    private PaymentSaveResult getPayment(PaymentEntity payment, Integer totalPrice){
        return PaymentSaveResult.builder()
                .paymentNumber(payment.getPaymentNumber())
                .paymentStatus(payment.getPaymentStatus())
                .paymentType(payment.getPaymentType())
                .cartTotalPrice(totalPrice)
                .paymentStartDate(payment.getPaymentStartDate())
                .paymentEndDate(payment.getPaymentEndDate())
                .paymentThirdParty(payment.getPaymentThirdParty())
                .paymentCreatedDate(payment.getPaymentCreatedDate())
                .build();
    }

    private void insertAuditTrails(PaymentRequest paymentRequest, PaymentSaveResult paymentSaveResult, ResponseEntity<Map> response) throws JsonProcessingException {
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
