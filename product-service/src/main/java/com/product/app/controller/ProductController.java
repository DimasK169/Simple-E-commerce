package com.product.app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.product.app.dto.request.ProductRequest;
import com.product.app.dto.request.ProductUpdateRequest;
import com.product.app.dto.response.RestApiResponse;
import com.product.app.dto.result.ProductCreateResponse;
import com.product.app.dto.result.ProductUpdateResponse;
import com.product.app.service.interfacing.ProductService;
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
            @Valid @RequestParam("name") String productName,
            @Valid @RequestParam("code") String productCode,
            @Valid @RequestParam("description") String productDescription,
            @Valid @RequestParam("category") String productCategory,
            @Valid @RequestParam("stock") Integer productStock,
            @Valid @RequestParam("price") Integer productPrice,
            @Valid @RequestParam("isAvailable") Boolean productAvailable,
            @Valid @RequestParam("createdBy") String createdBy,
            @RequestParam("image") MultipartFile imageFile
    ) throws IOException {

        ProductRequest productRequest = ProductRequest.builder()
                .productName(productName)
                .productCode(productCode)
                .productDescription(productDescription)
                .productCategory(productCategory)
                .productStock(productStock)
                .productPrice(productPrice)
                .productIsAvailable(productAvailable)
                .createdBy(createdBy)
                .build();
        return productService.create(productRequest, imageFile);
    }

    @PutMapping(value = "/{productCode}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RestApiResponse<ProductUpdateResponse> updateProduct(
            @PathVariable String productCode,
            @Valid @RequestParam("name") String productName,
            @Valid @RequestParam("description") String productDescription,
            @Valid @RequestParam("category") String productCategory,
            @Valid @RequestParam("stock") Integer productStock,
            @Valid @RequestParam("price") Integer productPrice,
            @Valid @RequestParam("isAvailable") Boolean productAvailable,
            @RequestParam(value = "image", required = false) MultipartFile imageFile
    ) throws IOException {
        ProductUpdateRequest productRequest = ProductUpdateRequest.builder()
                .productName(productName)
                .productDescription(productDescription)
                .productCategory(productCategory)
                .productStock(productStock)
                .productPrice(productPrice)
                .productIsAvailable(productAvailable)
                .build();

        return productService.update(productRequest, productCode, imageFile);
    }

    @GetMapping("/{productCode}")
    public RestApiResponse<ProductUpdateResponse> getProductbyCode(@PathVariable String productCode){
        return productService.getbyCode(productCode);
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
