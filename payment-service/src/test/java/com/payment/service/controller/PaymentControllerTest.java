package com.payment.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.service.dto.request.PaymentRequest;
import com.payment.service.dto.response.RestApiResponse;
import com.payment.service.dto.result.PaymentSaveResult;
import com.payment.service.entity.cart.CartEntity;
import com.payment.service.entity.payment.PaymentEntity;
import com.payment.service.entity.product.Product;
import com.payment.service.services.implementation.PaymentServiceImplementation;
import com.payment.service.services.services.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    private ObjectMapper objectMapper = new ObjectMapper();

    private PaymentRequest paymentRequest;
    private PaymentEntity payment;
    private PaymentSaveResult paymentSaveResult;
    private RestApiResponse<PaymentSaveResult> saveResultRestApiResponse;
    private RestApiResponse<List<PaymentSaveResult>> getResultRestApiResponse;
    private CartEntity cart;
    private Product product;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();

        List<PaymentSaveResult> results = new ArrayList<>();

        paymentRequest = PaymentRequest.builder()
                .userEmail("user@example.com")
                .paymentType("gopay")
                .build();

        cart = CartEntity.builder()
                .cartId(1l)
                .userId(1l)
                .userEmail("user@example.com")
                .productId(1l)
                .productName("Laptop")
                .productCode("P001")
                .cartQuantity(1)
                .paymentNumber("PN001")
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

        paymentSaveResult = PaymentSaveResult.builder()
                .paymentNumber("PN001")
                .paymentStatus("pending")
                .paymentType("gopay")
                .paymentStartDate(new Date())
                .paymentEndDate(new Date())
                .paymentThirdParty("Third Party")
                .paymentCreatedDate(new Date())
                .build();

        results.add(paymentSaveResult);

        saveResultRestApiResponse = RestApiResponse.<PaymentSaveResult>builder()
                .code("201 CREATED")
                .message("Cart Baru Berhasil Dibuat")
                .data(paymentSaveResult)
                .build();

        getResultRestApiResponse = RestApiResponse.<List<PaymentSaveResult>>builder()
                .code("201 CREATED")
                .message("Cart Baru Berhasil Dibuat")
                .data(results)
                .build();
    }

    @Test
    @DisplayName("Controller createPayment Success")
    void createPayment_whenValid() throws Exception {
        given(paymentService.createPayment(eq("Customer"), eq(paymentRequest.getUserEmail()), eq(paymentRequest)))
                .willReturn(saveResultRestApiResponse);

        mockMvc.perform(post("/payment/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest))
                        .requestAttr("userRole", "Customer")
                        .requestAttr("userEmail", "user@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("201 CREATED"))
                .andExpect(jsonPath("$.message").value("Cart Baru Berhasil Dibuat"))
                .andExpect(jsonPath("$.data.Payment_Type").value("gopay"))
                .andExpect(jsonPath("$.data.Payment_Status").value("pending"));
    }

    @Test
    @DisplayName("Controller createPayment Success")
    void testHandleMidtransWebhook_success() throws Exception {
        // Mock payload yang dikirim Midtrans
        Map<String, Object> payload = new HashMap<>();
        payload.put("transaction_status", "settlement");
        payload.put("order_id", "PMT123456");

        String payloadJson = new ObjectMapper().writeValueAsString(payload);

        when(paymentService.updatePaymentStatus(anyMap())).thenReturn("OK");

        mockMvc.perform(post("/payment/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadJson))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }

    @Test
    @DisplayName("Controller getPayment Success")
    void getPayment_whenValid() throws Exception {
        given(paymentService.getPayment(eq("Customer"), eq("user@example.com")))
                .willReturn(getResultRestApiResponse);

        mockMvc.perform(get("/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest))
                        .requestAttr("userRole", "Customer")
                        .requestAttr("userEmail", "user@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("201 CREATED"))
                .andExpect(jsonPath("$.message").value("Cart Baru Berhasil Dibuat"))
                .andExpect(jsonPath("$.data[0].Payment_Type").value("gopay"))
                .andExpect(jsonPath("$.data[0].Payment_Status").value("pending"));
    }

}
