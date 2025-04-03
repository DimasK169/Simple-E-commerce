package com.product.app.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.product.app.dto.request.ProductRequest;
import com.product.app.dto.response.RestApiResponse;
import com.product.app.dto.result.ProductCreateResponse;
import com.product.app.dto.result.ProductUpdateResponse;
import com.product.app.service.interfacing.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/create")
    public RestApiResponse<ProductCreateResponse> createProduct(@Valid @RequestBody ProductRequest productRequest) throws JsonProcessingException {
        return productService.create(productRequest);
    }

    @PatchMapping("/update/{productName}")
    public RestApiResponse<ProductUpdateResponse> updateProduct(@Valid @RequestBody ProductRequest request, @PathVariable String productName) throws JsonProcessingException {
        return productService.update(request, productName);
    }

    @DeleteMapping("/delete/{productName}")
    public RestApiResponse<ProductUpdateResponse> deleteProduct(@PathVariable String productName) throws JsonProcessingException {
        return productService.delete(productName);
    }
}
