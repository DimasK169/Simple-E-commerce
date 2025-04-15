package com.product.app.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.product.app.dto.request.ProductRequest;
import com.product.app.dto.request.ProductUpdateRequest;
import com.product.app.dto.response.RestApiResponse;
import com.product.app.dto.result.ProductCreateResponse;
import com.product.app.dto.result.ProductUpdateResponse;
import com.product.app.entity.Product;
import com.product.app.service.interfacing.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/create")
    public RestApiResponse<ProductCreateResponse> createProduct(HttpServletRequest request, @Valid @RequestBody ProductRequest productRequest) throws JsonProcessingException {
        return productService.create((String) request.getAttribute("userRole"), productRequest);
    }

    @PatchMapping("/{productCode}")
    public RestApiResponse<ProductUpdateResponse> updateProduct(@Valid @RequestBody ProductUpdateRequest request, @PathVariable String productCode) throws JsonProcessingException {
        return productService.update(request, productCode);
    }

    @DeleteMapping("/{productCode}")
    public RestApiResponse<ProductUpdateResponse> deleteProduct(@PathVariable String productCode) throws JsonProcessingException {
        return productService.delete(productCode);
    }

    @GetMapping
    public RestApiResponse<Page<Product>> getAllProducts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return productService.getAllProducts(page, size);
    }
}
