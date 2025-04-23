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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RestApiResponse<ProductCreateResponse>> createProduct(
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
        RestApiResponse<ProductCreateResponse> response = productService.create(productRequest, imageFile);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{productCode}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RestApiResponse<ProductUpdateResponse>> updateProduct(
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

        RestApiResponse<ProductUpdateResponse> response = productService.update(productRequest, productCode, imageFile);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{productCode}")
    public ResponseEntity<RestApiResponse<ProductUpdateResponse>> getProductbyCode(@PathVariable String productCode) throws JsonProcessingException{
        RestApiResponse<ProductUpdateResponse> response = productService.getbyCode(productCode);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{productCode}")
    public ResponseEntity<RestApiResponse<ProductUpdateResponse>> deleteProduct(@PathVariable String productCode) throws JsonProcessingException {
        RestApiResponse<ProductUpdateResponse> response = productService.delete(productCode);
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<RestApiResponse<Page<ProductUpdateResponse>>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) throws JsonProcessingException{
        RestApiResponse<Page<ProductUpdateResponse>> response = productService.getAllProducts(page, size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping ("/getForCustomer")
    public ResponseEntity<RestApiResponse<Page<ProductUpdateResponse>>> getAllProductsCustomer(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) throws JsonProcessingException{
        RestApiResponse<Page<ProductUpdateResponse>> response = productService.getAllProductsCustomer(page, size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping ("/getForAdmin")
    public ResponseEntity<RestApiResponse<Page<ProductUpdateResponse>>> getAllProductsAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) throws JsonProcessingException{
        RestApiResponse<Page<ProductUpdateResponse>> response = productService.getAllProductsAdmin(page, size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<RestApiResponse<Page<ProductUpdateResponse>>> searchProducts(
            @Valid @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) throws JsonProcessingException{
        RestApiResponse<Page<ProductUpdateResponse>> response = productService.searchProducts(keyword, PageRequest.of(page, size));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/searchAdmin")
    public ResponseEntity<RestApiResponse<Page<ProductUpdateResponse>>> searchProductsAdmin(
            @Valid @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) throws JsonProcessingException{
        RestApiResponse<Page<ProductUpdateResponse>> response = productService.searchProductsAdmin(keyword, PageRequest.of(page, size));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
