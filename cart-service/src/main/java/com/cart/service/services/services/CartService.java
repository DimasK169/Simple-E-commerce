package com.cart.service.services.services;

import com.cart.service.dto.result.CartUpdateResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.cart.service.dto.request.CartRequest;
import com.cart.service.dto.response.RestApiResponse;
import com.cart.service.dto.result.CartSaveResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CartService {

    RestApiResponse<CartSaveResult> addToCart(String jwtPayload, CartRequest cartRequest) throws JsonProcessingException;
    RestApiResponse<CartSaveResult> updateQuantityCart(String jwtPayload, CartRequest cartRequest) throws JsonProcessingException;
    RestApiResponse<List<CartUpdateResult>> updateReadyStateCart(String jwtPayload, CartRequest cartRequest) throws JsonProcessingException;
    RestApiResponse deleteCart(String jwtPayload, CartRequest cartRequest) throws JsonProcessingException;

}
