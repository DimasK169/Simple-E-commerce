package com.product.app.service.interfacing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.product.app.dto.request.ProductRequest;
import com.product.app.dto.response.RestApiResponse;
import com.product.app.dto.result.ProductCreateResponse;
import com.product.app.dto.result.ProductUpdateResponse;
import com.product.app.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface ProductService {

    RestApiResponse<ProductCreateResponse> create(ProductRequest request) throws JsonProcessingException;
    RestApiResponse<ProductUpdateResponse> update(ProductRequest request, String productName) throws JsonProcessingException;
    RestApiResponse<ProductUpdateResponse> delete(String productName) throws JsonProcessingException;
    RestApiResponse<Page<Product>> getAllProducts(int page, int size);
}
