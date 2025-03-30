package com.product.app.service.implementation;

import com.product.app.dto.request.ProductRequest;
import com.product.app.dto.response.RestApiResponse;
import com.product.app.dto.result.ProductCreateResponse;
import com.product.app.entity.Product;
import com.product.app.repository.ProductRepository;
import com.product.app.service.interfacing.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ProductServiceImplement implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public RestApiResponse<ProductCreateResponse> save(ProductRequest request) {
        Product product = Product.builder()
                .productName(request.getProductName())
                .productDescription(request.getProductDescription())
                .productCategory(request.getProductCategory())
                .productStock(request.getProductStock())
                .productPrice(request.getProductPrice())
                .productIsAvailable(request.getProductIsAvailable())
                .productIsDelete(false)
                .createdDate(new Date())
                .createdBy("admin 1")
                .build();

        Product savedProduct = productRepository.save(product);
        RestApiResponse<ProductCreateResponse> response = new RestApiResponse<>();

        ProductCreateResponse results = ProductCreateResponse.builder()
                .productName(savedProduct.getProductName())
                .productDescription(savedProduct.getProductDescription())
                .productCategory(savedProduct.getProductCategory())
                .productStock(savedProduct.getProductStock())
                .productPrice(savedProduct.getProductPrice())
                .productIsAvailable(savedProduct.getProductIsAvailable())
                .productIsDelete(savedProduct.getProductIsDelete())
                .createdBy(savedProduct.getCreatedBy())
                .createdDate(savedProduct.getCreatedDate())
                .build();

        response.setData(results);
        response.setCode(HttpStatus.OK.toString());
        response.setMessage("Berhasil Membuat Produk");
        return response;
    }
}
