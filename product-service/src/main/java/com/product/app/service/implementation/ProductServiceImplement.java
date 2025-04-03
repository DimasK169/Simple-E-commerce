package com.product.app.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.product.app.dto.request.AuditTrailsRequest;
import com.product.app.dto.request.ProductRequest;
import com.product.app.dto.response.RestApiResponse;
import com.product.app.dto.result.ProductCreateResponse;
import com.product.app.dto.result.ProductUpdateResponse;
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

    @Autowired
    private AuditTrailsServiceImp auditTrailsService;

    @Override
    public RestApiResponse<ProductCreateResponse> create(ProductRequest request) throws JsonProcessingException {
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

        auditTrailsService.saveAuditTrails(AuditTrailsRequest.builder()
                .AtAction("Create")
                .AtDescription("Create New Product")
                .AtDate(new Date())
                .AtRequest(String.valueOf(request))
                .AtResponse(String.valueOf(response))
                .build());

        return response;
    }

    @Override
    public RestApiResponse<ProductUpdateResponse> update(ProductRequest request, String productName) throws JsonProcessingException {
        Product p = getProductName(productName);
        if (p == null){
            throw  new IllegalArgumentException("Invalid Product Name");
        }

        Product product = Product.builder()
                .productId(p.getProductId())
                .productName(p.getProductName())
                .productDescription(request.getProductDescription())
                .productCategory(request.getProductCategory())
                .productStock(request.getProductStock())
                .productPrice(request.getProductPrice())
                .productIsAvailable(request.getProductIsAvailable())
                .productIsDelete(p.getProductIsDelete())
                .createdDate(p.getCreatedDate())
                .updatedDate(new Date())
                .createdBy(p.getCreatedBy())
                .build();

        Product saveProduct = productRepository.save(product);

        ProductUpdateResponse result = ProductUpdateResponse.builder()
                .productName(saveProduct.getProductName())
                .productDescription(saveProduct.getProductDescription())
                .productCategory(saveProduct.getProductCategory())
                .productStock(saveProduct.getProductStock())
                .productPrice(saveProduct.getProductPrice())
                .productIsAvailable(saveProduct.getProductIsAvailable())
                .productIsDelete(saveProduct.getProductIsDelete())
                .updatedDate(new Date())
                .createdBy(saveProduct.getCreatedBy())
                .build();

        RestApiResponse<ProductUpdateResponse> response = new RestApiResponse<>();
        response.setData(result);
        response.setCode(HttpStatus.OK.toString());
        response.setMessage("Berhasil Update Produk");

        auditTrailsService.saveAuditTrails(AuditTrailsRequest.builder()
                .AtAction("Update")
                .AtDescription("Update Product")
                .AtDate(new Date())
                .AtRequest(String.valueOf(request))
                .AtResponse(String.valueOf(response))
                .build());

        return response;
    }

    @Override
    public RestApiResponse<ProductUpdateResponse> delete(String productName) throws JsonProcessingException {
        Product product = getProductName(productName);
        if(product == null){
            throw new IllegalArgumentException("Invalid product Name");
        }

        product.setUpdatedDate(new Date());
        product.setProductIsDelete(true);

        Product saveProduct = productRepository.save(product);
        ProductUpdateResponse result = ProductUpdateResponse.builder()
                .productName(saveProduct.getProductName())
                .productDescription(saveProduct.getProductDescription())
                .productCategory(saveProduct.getProductCategory())
                .productStock(saveProduct.getProductStock())
                .productPrice(saveProduct.getProductPrice())
                .productIsAvailable(saveProduct.getProductIsAvailable())
                .productIsDelete(saveProduct.getProductIsDelete())
                .updatedDate(new Date())
                .createdBy(saveProduct.getCreatedBy())
                .build();

        RestApiResponse<ProductUpdateResponse> response = new RestApiResponse<>();
        response.setCode(HttpStatus.NO_CONTENT.toString());
        response.setMessage("Berhasil Delete Produk");

        auditTrailsService.saveAuditTrails(AuditTrailsRequest.builder()
                .AtAction("Delete")
                .AtDescription("Delete Product")
                .AtDate(new Date())
                .AtRequest(String.valueOf(productName))
                .AtResponse(String.valueOf(response))
                .build());

        return response;
    }

    public Product getProductName(String productName){
        Product product = productRepository.findByProductName(productName);
        return product;
    }

}
