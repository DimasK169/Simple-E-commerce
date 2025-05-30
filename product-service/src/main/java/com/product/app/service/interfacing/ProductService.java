package com.product.app.service.interfacing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.product.app.dto.request.ProductRequest;
import com.product.app.dto.request.ProductUpdateRequest;
import com.product.app.dto.response.RestApiResponse;
import com.product.app.dto.result.ProductCreateResponse;
import com.product.app.dto.result.ProductUpdateResponse;
import com.product.app.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface ProductService {
    RestApiResponse<ProductCreateResponse> create(ProductRequest request, MultipartFile imageFile) throws IOException;
    RestApiResponse<ProductUpdateResponse> update(ProductUpdateRequest request, String productCode, MultipartFile imageFile) throws IOException;
    RestApiResponse<ProductUpdateResponse> delete(String productCode) throws JsonProcessingException;
    RestApiResponse<Page<ProductUpdateResponse>> getAllProducts(int page, int size) throws JsonProcessingException;
    RestApiResponse<Page<ProductUpdateResponse>> searchProducts(String keyword, Pageable pageable) throws JsonProcessingException;
    RestApiResponse<ProductUpdateResponse> getbyCode(String productCode) throws JsonProcessingException;
    RestApiResponse<Page<ProductUpdateResponse>> searchProductsAdmin(String keyword, Pageable pageable) throws JsonProcessingException;
    Product getProductCode(String productCode);
    RestApiResponse<Page<ProductUpdateResponse>> getAllProductsCustomer(int page, int size) throws JsonProcessingException;
    RestApiResponse<Page<ProductUpdateResponse>> getAllProductsAdmin(int page, int size) throws JsonProcessingException;

}
