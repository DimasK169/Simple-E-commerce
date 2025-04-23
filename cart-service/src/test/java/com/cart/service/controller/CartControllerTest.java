package com.cart.service.controller;

import com.cart.service.dto.request.CartRequest;
import com.cart.service.dto.response.RestApiResponse;
import com.cart.service.dto.result.CartGetResult;
import com.cart.service.dto.result.CartSaveResult;
import com.cart.service.entity.cart.CartEntity;
import com.cart.service.entity.product.Product;
import com.cart.service.services.services.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartController cartController;

    private ObjectMapper objectMapper = new ObjectMapper();


    private CartRequest cartRequest;
    private CartEntity cart;
    private Product product;
    private CartSaveResult cartSaveResult;
    private CartGetResult cartGetResult;
    private RestApiResponse<CartSaveResult> saveResultRestApiResponse;
    private RestApiResponse<CartSaveResult> updateResultRestApiResponse;
    private RestApiResponse<List<CartGetResult>> getResultRestApiResponse;
    private RestApiResponse deleteRestApiResponse;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();

        List<CartGetResult> results = new ArrayList<>();

        cartRequest = CartRequest.builder()
                .userEmail("user@example.com")
                .productCode("P001")
                .cartQuantity(1)
                .fsCode(null)
                .build();

        cart = CartEntity.builder()
                .cartId(1l)
                .userId(1l)
                .userEmail("user@example.com")
                .productId(1l)
                .productName("Laptop")
                .productCode("P001")
                .cartQuantity(1)
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

        cartSaveResult = CartSaveResult.builder()
                .userEmail(cart.getUserEmail())
                .productName(cart.getProductName())
                .fsCode(cart.getFsCode())
                .cartQuantity(cart.getCartQuantity())
                .cartTotalPrice(cart.getCartTotalPrice())
                .isPayed(cart.getIsPayed())
                .isFailed(cart.getIsFailed())
                .cartCreatedDate(cart.getCartCreatedDate())
                .build();

        cartGetResult = CartGetResult.builder()
                .productName(cart.getProductName())
                .productDesc(product.getProductDescription())
                .productImage(product.getProductImage())
                .productPrice(product.getProductPrice())
                .cartTotalPricePerItem(cart.getCartTotalPrice())
                .cartQuantity(cart.getCartQuantity())
                .fsCode(cart.getFsCode())
                .productCode(cart.getProductCode())
                .createdDate(new Date())
                .build();

        results.add(cartGetResult);

        saveResultRestApiResponse = RestApiResponse.<CartSaveResult>builder()
                .code("201 CREATED")
                .message("Cart Baru Berhasil Dibuat")
                .data(cartSaveResult)
                .build();

        updateResultRestApiResponse = RestApiResponse.<CartSaveResult>builder()
                .code("200 OK")
                .message("Cart Berhasil Di-Update")
                .data(cartSaveResult)
                .build();

        getResultRestApiResponse = RestApiResponse.<List<CartGetResult>>builder()
                .code("200 OK")
                .message("Cart Berhasil Di-Fetch")
                .data(results)
                .build();

        deleteRestApiResponse = RestApiResponse.builder()
                .code(HttpStatus.OK.toString())
                .message("Cart Berhasil Di-Delete")
                .build();
    }

    @Test
    @DisplayName("Controller addToCart Success")
    void addToCart_whenValid() throws Exception {
        given(cartService.addToCart(eq("Customer"), eq(cartRequest.getUserEmail()), Mockito.any(CartRequest.class)))
                .willReturn(saveResultRestApiResponse);

        mockMvc.perform(post("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartRequest))
                        .requestAttr("userRole", "Customer")
                        .requestAttr("userEmail", "user@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("201 CREATED"))
                .andExpect(jsonPath("$.message").value("Cart Baru Berhasil Dibuat"))
                .andExpect(jsonPath("$.data.Product_Name").value("Laptop"))
                .andExpect(jsonPath("$.data.Cart_Quantity").value(1));
    }

    @Test
    @DisplayName("Controller updateQuantityCart Success")
    void updateQuantityCart_whenValid() throws Exception {

        given(cartService.updateQuantityCart(eq("Customer"), eq(cartRequest.getUserEmail()), Mockito.any(CartRequest.class)))
                .willReturn(updateResultRestApiResponse);

        mockMvc.perform(put("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartRequest))
                        .requestAttr("userRole", "Customer")
                        .requestAttr("userEmail", "user@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200 OK"))
                .andExpect(jsonPath("$.message").value("Cart Berhasil Di-Update"))
                .andExpect(jsonPath("$.data.Product_Name").value("Laptop"))
                .andExpect(jsonPath("$.data.Cart_Quantity").value(1));
    }

    @Test
    @DisplayName("Controller deleteCart Success")
    void deleteCart_whenValid() throws Exception {
        given(cartService.deleteCart(eq("Customer"), eq(cartRequest.getUserEmail()), Mockito.any(CartRequest.class)))
                .willReturn(deleteRestApiResponse);

        mockMvc.perform(delete("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartRequest))
                        .requestAttr("userRole", "Customer")
                        .requestAttr("userEmail", "user@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200 OK"))
                .andExpect(jsonPath("$.message").value("Cart Berhasil Di-Delete"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("Controller getAllCart Success")
    void getAllCart_whenValid() throws Exception {
        given(cartService.getCartByUserEmailAndProductCode(eq("Customer"), eq(cartRequest.getUserEmail())))
                .willReturn(getResultRestApiResponse);

        mockMvc.perform(get("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartRequest))
                        .requestAttr("userRole", "Customer")
                        .requestAttr("userEmail", "user@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200 OK"))
                .andExpect(jsonPath("$.message").value("Cart Berhasil Di-Fetch"))
                .andExpect(jsonPath("$.data[0].Product_Name").value("Laptop"))
                .andExpect(jsonPath("$.data[0].Cart_Quantity").value(1));
    }
}
