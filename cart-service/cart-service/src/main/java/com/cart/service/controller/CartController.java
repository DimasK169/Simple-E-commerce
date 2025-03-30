package com.cart.service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.cart.service.dto.request.CartRequest;
import com.cart.service.dto.response.RestApiResponse;
import com.cart.service.dto.result.CartSaveResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.cart.service.services.services.CartService;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/save")
    public RestApiResponse<CartSaveResult> save(@RequestBody CartRequest cartRequest) throws JsonProcessingException {
        return cartService.createNewCart(cartRequest);
    }

}
