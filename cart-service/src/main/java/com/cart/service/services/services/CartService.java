package com.cart.service.services.services;

import com.cart.service.entity.cart.CartEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.cart.service.dto.request.CartRequest;
import com.cart.service.dto.response.RestApiResponse;
import com.cart.service.dto.result.CartSaveResult;
import org.springframework.stereotype.Service;

@Service
public interface CartService {

    RestApiResponse<CartSaveResult> addToCart(CartRequest cartRequest) throws JsonProcessingException;
}
