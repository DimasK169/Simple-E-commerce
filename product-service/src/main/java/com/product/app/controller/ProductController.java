package com.product.app.controller;


import com.product.app.dto.request.ProductRequest;
import com.product.app.dto.response.RestApiResponse;
import com.product.app.dto.result.ProductCreateResponse;
import com.product.app.service.interfacing.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/create")
    public RestApiResponse<ProductCreateResponse> createProduct(@Valid @RequestBody ProductRequest productRequest){
        return productService.save(productRequest);
    }
}
