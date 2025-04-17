package com.product.app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RestApiResponse<ProductCreateResponse> createProduct(
            @Valid @RequestParam("product") String productJson,
            @RequestParam("image") MultipartFile imageFile
    ) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        ProductRequest productRequest = mapper.readValue(productJson, ProductRequest.class);

        return productService.create(productRequest, imageFile);
    }

    @PutMapping(value = "/{productCode}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RestApiResponse<ProductUpdateResponse> updateProduct(
            @PathVariable String productCode,
            @Valid @RequestParam("product") String productJson,
            @RequestParam(value = "image", required = false) MultipartFile imageFile
    ) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ProductUpdateRequest request = mapper.readValue(productJson, ProductUpdateRequest.class);

        return productService.update(request, productCode, imageFile);
    }

    @DeleteMapping("/{productCode}")
    public RestApiResponse<ProductUpdateResponse> deleteProduct(@PathVariable String productCode) throws JsonProcessingException {
        return productService.delete(productCode);
    }

    @GetMapping
    public RestApiResponse<Page<ProductUpdateResponse>> getAllProducts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return productService.getAllProducts(page, size);
    }

    @GetMapping("/search")
    public RestApiResponse<Page<ProductUpdateResponse>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productService.searchProducts(keyword, PageRequest.of(page, size));
    }

}
