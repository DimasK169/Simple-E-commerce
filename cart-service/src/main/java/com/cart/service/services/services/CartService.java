package com.cart.service.services.services;

import com.cart.service.dto.result.CartGetResult;
import com.cart.service.dto.result.CartUpdateResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.cart.service.dto.request.CartRequest;
import com.cart.service.dto.response.RestApiResponse;
import com.cart.service.dto.result.CartSaveResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CartService {

    RestApiResponse<CartSaveResult> addToCart(String userRole, String userEmail, CartRequest cartRequest) throws JsonProcessingException;
    RestApiResponse<CartSaveResult> updateQuantityCart(String userRole, String userEmail, CartRequest cartRequest) throws JsonProcessingException;
    RestApiResponse deleteCart(String userRole, String userEmail, CartRequest cartRequest) throws JsonProcessingException;
    RestApiResponse<List<CartGetResult>> getCartByUserEmailAndProductCode(String userRole, String userEmail)throws JsonProcessingException;

}
