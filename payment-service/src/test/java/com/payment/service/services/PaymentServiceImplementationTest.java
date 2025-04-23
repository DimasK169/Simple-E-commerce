package com.payment.service.services;

import com.payment.service.dto.request.PaymentRequest;
import com.payment.service.dto.response.RestApiResponse;
import com.payment.service.dto.result.PaymentSaveResult;
import com.payment.service.entity.cart.CartEntity;
import com.payment.service.entity.payment.PaymentEntity;
import com.payment.service.entity.product.Product;
import com.payment.service.repository.cart.CartRepository;
import com.payment.service.repository.payment.PaymentRepository;
import com.payment.service.repository.product.ProductRepository;
import com.payment.service.services.implementation.PaymentServiceImplementation;
import com.payment.service.services.services.AuditTrailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceImplementationTest {

    @Spy
    @InjectMocks
    private PaymentServiceImplementation paymentServiceImplementation;

    @Mock private PaymentRepository paymentRepository;
    @Mock private CartRepository cartRepository;
    @Mock private ProductRepository productRepository;
    @Mock private AuditTrailsService auditTrailsService;

    private PaymentRequest paymentRequest;
    private PaymentEntity payment;
    private CartEntity cart;
    private Product product;

    private final String midtransCharge = "https://api.sandbox.midtrans.com/v2/charge";
    private final String midtransStatus = "https://api.sandbox.midtrans.com/v2/%s/status";

    @BeforeEach
    void setup() {

        paymentRequest = PaymentRequest.builder()
                .userEmail("user@example.com")
                .paymentType("gopay")
                .build();

        payment = PaymentEntity.builder()
                .userId(1l)
                .userEmail("user@example.com")
                .paymentNumber("PN001")
                .paymentType("gopay")
                .paymentPrice(1000)
                .paymentCreatedDate(new Date())
                .build();

        cart = CartEntity.builder()
                .cartId(1l)
                .userId(1l)
                .userEmail("user@example.com")
                .productId(1l)
                .productName("Laptop")
                .productCode("P001")
                .cartQuantity(10)
                .cartTotalPrice(1000)
                .paymentNumber("N/A")
                .isPayed(false)
                .isFailed(false)
                .cartCreatedDate(new Date())
                .build();

        product = Product.builder()
                .productId(1l)
                .productCode("P001")
                .productName("Laptop")
                .productDescription("A good laptop")
                .productCategory("Electronics")
                .productStock(10)
                .productPrice(1500)
                .productImage("stored-laptop.png")
                .productIsAvailable(true)
                .productIsDelete(false)
                .createdBy("admin")
                .createdDate(new Date())
                .build();

        ReflectionTestUtils.setField(paymentServiceImplementation, "midtransCharge", midtransCharge);
        ReflectionTestUtils.setField(paymentServiceImplementation, "midtransStatus", midtransStatus);
        ReflectionTestUtils.setField(paymentServiceImplementation, "midtransKey", "SB-Mid-server-oQ9buJM9VzkIKUJxEv7_7wmJ");

    }

    @Test
    @DisplayName("Success Create Payment")
    void createPayment_whenValidPaymentType() throws Exception {

        // Setup response dari Midtrans POST (charge)
        Map<String, Object> postBody = new HashMap<>();
        postBody.put("transaction_status", "pending");

        ResponseEntity<Map> postResponse = new ResponseEntity<>(postBody, HttpStatus.OK);

        // Setup response dari Midtrans GET (status)
        Map<String, Object> getBody = new HashMap<>();
        getBody.put("transaction_time", "2025-04-23 14:00:00");
        getBody.put("expiry_time", "2025-04-23 15:00:00");

        ResponseEntity<Map> getResponse = new ResponseEntity<>(getBody, HttpStatus.OK);

        when(cartRepository.findByUserEmail("user@example.com", "N/A", false, false))
                .thenReturn(List.of(cart));

        when(paymentRepository.save(any(PaymentEntity.class)))
                .thenReturn(payment);

        RestApiResponse<PaymentSaveResult> response = paymentServiceImplementation.createPayment(
                "Customer", "user@example.com", paymentRequest
        );

        assertEquals(HttpStatus.CREATED.toString(), response.getCode());
        assertNotNull(response.getData());

        verify(paymentRepository).save(any(PaymentEntity.class));

    }

    @Test
    @DisplayName("Success Update Payment Settlement")
    void testUpdatePaymentStatus_settlement() {
        // Arrange
        String orderId = "PN001";
        String transactionStatus = "settlement";

        Map<String, Object> payload = new HashMap<>();
        payload.put("order_id", orderId);
        payload.put("transaction_status", transactionStatus);

        when(paymentRepository.findByPaymentNumber(orderId)).thenReturn(Optional.of(payment));
        when(cartRepository.findByUserEmailAndPaymentNumber("user@example.com", orderId, false, false))
                .thenReturn(List.of(cart));

        // Act
        paymentServiceImplementation.updatePaymentStatus(payload);

        // Assert
        assertTrue(cart.getIsPayed());
        assertEquals(transactionStatus, payment.getPaymentStatus());
        verify(cartRepository).findByUserEmailAndPaymentNumber("user@example.com", orderId, false, false);
        verify(paymentRepository).findByPaymentNumber(orderId);
    }

    @Test
    @DisplayName("Success Update Payment Expire")
    void testUpdatePaymentStatus_expire() {
        // Arrange
        String orderId = "PN001";
        String transactionStatus = "expire";

        Map<String, Object> payload = new HashMap<>();
        payload.put("order_id", orderId);
        payload.put("transaction_status", transactionStatus);

        when(paymentRepository.findByPaymentNumber(orderId)).thenReturn(Optional.of(payment));
        when(cartRepository.findByUserEmailAndPaymentNumber("user@example.com", orderId, false, false))
                .thenReturn(List.of(cart));
        when(productRepository.findManyProductByCode("P001")).thenReturn(List.of(product));

        // Act
        paymentServiceImplementation.updatePaymentStatus(payload);

        // Assert
        assertTrue(cart.getIsFailed());
        assertEquals(20, product.getProductStock());
        assertEquals(transactionStatus, payment.getPaymentStatus());
        verify(cartRepository).findByUserEmailAndPaymentNumber("user@example.com", orderId, false, false);
        verify(productRepository).findManyProductByCode("P001");
        verify(paymentRepository).findByPaymentNumber(orderId);
    }

    @Test
    @DisplayName("Success Get Payment Settlement")
    void testGetPayment_withSettlementStatus_shouldReturnPaymentSaveResult() {
        PaymentEntity payment = new PaymentEntity();
        payment.setPaymentNumber("PN001");
        payment.setPaymentStatus("settlement");

        when(paymentRepository.findPaymentByEmail("user@example.com")).thenReturn(List.of(payment));
        when(cartRepository.findByUserEmailAndPaymentNumber("user@example.com", "PN001", true, false))
                .thenReturn(List.of(cart));

        RestApiResponse<List<PaymentSaveResult>> response = paymentServiceImplementation.getPayment("Customer", "user@example.com");

        assertEquals("Get", response.getCode());
        assertEquals(1, response.getData().size());

        PaymentSaveResult result = response.getData().get(0);
        System.out.println(result);
        assertEquals(1, result.getProduct().size());
        assertEquals(1000, result.getCartTotalPrice());

        verify(cartRepository).findByUserEmailAndPaymentNumber("user@example.com", "PN001", true, false);
        verify(paymentRepository).findPaymentByEmail("user@example.com");
    }

    @Test
    @DisplayName("Success Get Payment Expire")
    void testGetPayment_withExpireStatus_shouldReturnPaymentSaveResult() {
        payment.setPaymentStatus("expire");

        when(paymentRepository.findPaymentByEmail("user@example.com")).thenReturn(List.of(payment));
        when(cartRepository.findByUserEmailAndPaymentNumber("user@example.com", "PN001", false, true))
                .thenReturn(List.of(cart));

        RestApiResponse<List<PaymentSaveResult>> response = paymentServiceImplementation.getPayment("Customer", "user@example.com");

        assertEquals("Get", response.getCode());
        assertEquals(1, response.getData().size());

        PaymentSaveResult result = response.getData().get(0);
        System.out.println(result);
        assertEquals(1, result.getProduct().size());
        assertEquals(1000, result.getCartTotalPrice());

        verify(cartRepository).findByUserEmailAndPaymentNumber("user@example.com", "PN001", false, true);
        verify(paymentRepository).findPaymentByEmail("user@example.com");
    }

    @Test
    @DisplayName("Success Get Payment Pending")
    void testGetPayment_withPendingStatus_shouldReturnPaymentSaveResult() {
        payment.setPaymentStatus("pending");

        when(paymentRepository.findPaymentByEmail("user@example.com")).thenReturn(List.of(payment));
        when(cartRepository.findByUserEmailAndPaymentNumber("user@example.com", "PN001", false, false))
                .thenReturn(List.of(cart));

        RestApiResponse<List<PaymentSaveResult>> response = paymentServiceImplementation.getPayment("Customer", "user@example.com");

        assertEquals("Get", response.getCode());
        assertEquals(1, response.getData().size());

        PaymentSaveResult result = response.getData().get(0);
        System.out.println(result);
        assertEquals(1, result.getProduct().size());
        assertEquals(1000, result.getCartTotalPrice());

        verify(cartRepository).findByUserEmailAndPaymentNumber("user@example.com", "PN001", false, false);
        verify(paymentRepository).findPaymentByEmail("user@example.com");
    }

}
