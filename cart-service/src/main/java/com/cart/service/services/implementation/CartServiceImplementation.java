package com.cart.service.services.implementation;

import com.cart.service.entity.product.Product;
import com.cart.service.entity.user.Users;
import com.cart.service.repository.product.ProductRepository;
import com.cart.service.repository.user.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.cart.service.dto.request.AuditTrailsRequest;
import com.cart.service.dto.request.CartRequest;
import com.cart.service.dto.response.RestApiResponse;
import com.cart.service.dto.result.CartSaveResult;
import com.cart.service.entity.cart.CartEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.cart.service.repository.cart.CartRepository;
import com.cart.service.services.services.AuditTrailsService;
import com.cart.service.services.services.CartService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartServiceImplementation implements CartService {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuditTrailsService auditTrailsService;

    @Override
    @Transactional("cartTransactionManager")
    public RestApiResponse<CartSaveResult> addToCart(CartRequest cartRequest) throws JsonProcessingException {
        Product product = productRepository.findById(cartRequest.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Users user = userRepository.findById(cartRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        CartEntity cartEntity = CartEntity.builder()
                .userId(user.getUserId())
                .userEmail(user.getUserEmail())
                .productId(product.getProductId())
                .productName(product.getProductName())
                .cartNumber(UUID.randomUUID().toString())
                .cartQuantity(cartRequest.getCartQuantity())
                .cartTotalPrice(cartRequest.getCartTotalPrice())
                .cartCreatedDate(new Date())
                .build();
        CartEntity cartSave = cartRepository.save(cartEntity);

        CartSaveResult cartSaveResult = CartSaveResult.builder()
                .productId(cartSave.getProductId())
                .userId(cartSave.getUserId())
                .userEmail(cartSave.getUserEmail())
                .productName(cartSave.getProductName())
                .cartNumber(cartSave.getCartNumber())
                .cartQuantity(cartSave.getCartQuantity())
                .cartTotalPrice(cartSave.getCartTotalPrice())
                .cartCreatedDate(cartSave.getCartCreatedDate())
                .build();

        RestApiResponse response = RestApiResponse.builder()
                .code(HttpStatus.CREATED.toString())
                .message("Cart Baru Berhasil Dibuat")
                .data(cartSaveResult)
                .error(null)
                .build();

        ObjectMapper mapper = new ObjectMapper();

        auditTrailsService.insertAuditTrails(AuditTrailsRequest.builder()
                .auditTrailsAction("Create")
                .auditTrailsDescription("Creating Audit Trails For New Cart")
                .auditTrailsDate(new Date())
                .auditTrailsRequest(mapper.writeValueAsString(cartRequest))
                .auditTrailsResponse(mapper.writeValueAsString(cartSaveResult))
                .build());

        return response;
    }

}
