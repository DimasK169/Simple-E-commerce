package com.product.app.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.product.app.dto.request.ProductRequest;
import com.product.app.dto.response.RestApiResponse;
import com.product.app.dto.result.ProductCreateResponse;
import com.product.app.dto.result.ProductUpdateResponse;
import com.product.app.entity.Product;
import com.product.app.service.interfacing.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    //TODO VALIDASI UNIQUE KEY
    @PostMapping("")
    public RestApiResponse<ProductCreateResponse> createProduct(@Valid @RequestBody ProductRequest productRequest) throws JsonProcessingException {
        return productService.create(productRequest);
    }

    @PatchMapping("/{productName}")
    public RestApiResponse<ProductUpdateResponse> updateProduct(@Valid @RequestBody ProductRequest request, @PathVariable String productName) throws JsonProcessingException {
        return productService.update(request, productName);
    }

    @DeleteMapping("/{productName}")
    public RestApiResponse<ProductUpdateResponse> deleteProduct(@PathVariable String productName) throws JsonProcessingException {
        return productService.delete(productName);
    }

    @GetMapping
    public RestApiResponse<Page<Product>> getAllProducts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return productService.getAllProducts(page, size);
    }

}
