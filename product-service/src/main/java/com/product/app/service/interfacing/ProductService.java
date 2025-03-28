package com.product.app.service.interfacing;

import com.product.app.dto.request.ProductRequest;
import com.product.app.dto.response.RestApiResponse;
import com.product.app.dto.result.ProductCreateResponse;
import org.springframework.stereotype.Service;

@Service
public interface ProductService {

    RestApiResponse<ProductCreateResponse> save(ProductRequest request);
}
