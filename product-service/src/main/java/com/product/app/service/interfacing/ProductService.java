package com.product.app.service.interfacing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.product.app.dto.request.ProductRequest;
import com.product.app.dto.request.ProductUpdateRequest;
import com.product.app.dto.response.RestApiResponse;
import com.product.app.dto.result.ProductCreateResponse;
import com.product.app.dto.result.ProductUpdateResponse;
import com.product.app.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface ProductService {

    RestApiResponse<ProductCreateResponse> create(String jwtPayload, ProductRequest request) throws JsonProcessingException;
    RestApiResponse<ProductUpdateResponse> update(ProductUpdateRequest request, String productCode) throws JsonProcessingException;
    RestApiResponse<ProductUpdateResponse> delete(String productCode) throws JsonProcessingException;
    RestApiResponse<Page<Product>> getAllProducts(int page, int size);

}
