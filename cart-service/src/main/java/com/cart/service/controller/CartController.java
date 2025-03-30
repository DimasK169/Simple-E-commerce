package com.cart.service.controller;

import com.cart.service.entity.cart.CartEntity;
import com.cart.service.services.implementation.CartServiceImplementation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.cart.service.dto.request.CartRequest;
import com.cart.service.dto.response.RestApiResponse;
import com.cart.service.dto.result.CartSaveResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.cart.service.services.services.CartService;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public RestApiResponse<CartSaveResult> addToCart(@RequestBody CartRequest cartRequest) throws JsonProcessingException {
        return cartService.addToCart(cartRequest);
    }

}
