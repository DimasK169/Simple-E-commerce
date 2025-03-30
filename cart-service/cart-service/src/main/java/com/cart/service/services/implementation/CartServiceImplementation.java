package com.cart.service.services.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.cart.service.dto.request.AuditTrailsRequest;
import com.cart.service.dto.request.CartRequest;
import com.cart.service.dto.response.RestApiResponse;
import com.cart.service.dto.result.CartSaveResult;
import com.cart.service.entity.CartEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.cart.service.repository.CartRepository;
import com.cart.service.services.services.AuditTrailsService;
import com.cart.service.services.services.CartService;

import java.util.Date;
import java.util.UUID;

@Service
public class CartServiceImplementation implements CartService {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    AuditTrailsService auditTrailsService;

    @Override
    public RestApiResponse<CartSaveResult> createNewCart(CartRequest cartRequest) throws JsonProcessingException {

        CartEntity cartEntity = CartEntity.builder()
                .cartNumber(UUID.randomUUID().toString())
                .cartQuantity(cartRequest.getCartQuantity())
                .cartTotalPrice(cartRequest.getCartTotalPrice())
                .cartCreatedDate(new Date())
                .build();
        CartEntity saveCart = cartRepository.save(cartEntity);

        System.out.println("Cart Quantity: " + cartRequest.getCartTotalPrice());

        CartSaveResult cartSaveResult = CartSaveResult.builder()
                .cartNumber(saveCart.getCartNumber())
                .cartQuantity(saveCart.getCartQuantity())
                .cartTotalPrice(saveCart.getCartTotalPrice())
                .cartCreatedDate(saveCart.getCartCreatedDate())
                .build();

        RestApiResponse restApiResponseCreate = RestApiResponse.builder()
                .code(HttpStatus.CREATED.toString())
                .message("Cart Baru Berhasil Dibuat")
                .data(cartSaveResult)
                .build();

        ObjectMapper mapper = new ObjectMapper();

        auditTrailsService.insertAuditTrails(AuditTrailsRequest.builder()
                .auditTrailsAction("Create")
                .auditTrailsDescription("Creating Audit Trails For New Cart")
                .auditTrailsDate(new Date())
                .auditTrailsRequest(mapper.writeValueAsString(cartRequest))
                .auditTrailsResponse(mapper.writeValueAsString(cartSaveResult))
                .build());

        return restApiResponseCreate;
    }

}
