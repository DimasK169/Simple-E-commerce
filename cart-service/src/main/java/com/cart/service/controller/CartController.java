package com.cart.service.controller;

import com.cart.service.dto.result.CartGetResult;
import com.cart.service.dto.result.CartUpdateResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.cart.service.dto.request.CartRequest;
import com.cart.service.dto.response.RestApiResponse;
import com.cart.service.dto.result.CartSaveResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.cart.service.services.services.CartService;

import java.util.List;

import static com.cart.service.utility.RestApiPath.*;

@RestController
@RequestMapping(CART_API_PATH)
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("")
    public RestApiResponse<CartSaveResult> addToCart(HttpServletRequest request, @RequestBody @Valid CartRequest cartRequest) throws JsonProcessingException {
        return cartService.addToCart((String) request.getAttribute("userRole"), cartRequest);
    }

    @PutMapping("")
    public RestApiResponse<CartSaveResult> updateQuantityCart(HttpServletRequest request, @RequestBody @Valid CartRequest cartRequest) throws JsonProcessingException {
        return cartService.updateQuantityCart((String) request.getAttribute("userRole"), cartRequest);
    }

    @PutMapping(CART_READY_API_PATH)
    public RestApiResponse<List<CartUpdateResult>> updateReadyStatusCart(HttpServletRequest request, @RequestBody @Valid CartRequest cartRequest) throws JsonProcessingException{
        return cartService.updateReadyStateCart((String) request.getAttribute("userRole"), cartRequest);
    }

    @DeleteMapping("")
    public RestApiResponse deleteCart(HttpServletRequest request, @RequestBody @Valid CartRequest cartRequest) throws JsonProcessingException {
        return cartService.deleteCart((String) request.getAttribute("userRole"), cartRequest);
    }

    @GetMapping("")
    public RestApiResponse<List<CartGetResult>> getAllCart(HttpServletRequest request, @RequestBody @Valid CartRequest cartRequest)throws JsonProcessingException{
        return cartService.getCartByUserEmailAndProductCode((String) request.getAttribute("userRole"), cartRequest);
    }

}
