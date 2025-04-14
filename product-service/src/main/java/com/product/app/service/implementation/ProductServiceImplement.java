package com.product.app.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.product.app.dto.request.AuditTrailsRequest;
import com.product.app.dto.request.ProductRequest;
import com.product.app.dto.request.ProductUpdateRequest;
import com.product.app.dto.response.RestApiResponse;
import com.product.app.dto.result.ProductCreateResponse;
import com.product.app.dto.result.ProductUpdateResponse;
import com.product.app.entity.Product;
import com.product.app.repository.ProductRepository;
import com.product.app.service.interfacing.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

@Service
public class ProductServiceImplement implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AuditTrailsServiceImp auditTrailsService;

    @Autowired
    private FileStorageService fileStorageService;

    @Value("${app.base-url}")
    private String baseUrl;

    public RestApiResponse<ProductCreateResponse> create(ProductRequest request, MultipartFile imageFile) throws IOException {
        if (productRepository.existsByProductCode(request.getProductCode())) {
            throw new IllegalArgumentException("Product code must be unique.");
        }

        String savedFileName = fileStorageService.storeFile(imageFile);

        Product product = Product.builder()
                .productName(request.getProductName())
                .productCode(request.getProductCode())
                .productImage(savedFileName)
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

        ProductCreateResponse results = ProductCreateResponse.builder()
                .productName(savedProduct.getProductName())
                .productCode(savedProduct.getProductCode())
                .productImage(baseUrl + "/images/" + savedProduct.getProductImage())
                .productDescription(savedProduct.getProductDescription())
                .productCategory(savedProduct.getProductCategory())
                .productStock(savedProduct.getProductStock())
                .productPrice(savedProduct.getProductPrice())
                .productIsAvailable(savedProduct.getProductIsAvailable())
                .productIsDelete(savedProduct.getProductIsDelete())
                .createdBy(savedProduct.getCreatedBy())
                .createdDate(savedProduct.getCreatedDate())
                .build();

        RestApiResponse<ProductCreateResponse> response = new RestApiResponse<>();
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
    public RestApiResponse<ProductUpdateResponse> update(ProductUpdateRequest request, String productCode, MultipartFile imageFile) throws IOException {
        Product p = getProductCode(productCode);
        if (p == null) {
            throw new IllegalArgumentException("Invalid Product Code");
        }

        String savedFileName = p.getProductImage();
        if (imageFile != null && !imageFile.isEmpty()) {
            savedFileName = fileStorageService.storeFile(imageFile);
        }

        Product product = Product.builder()
                .productId(p.getProductId())
                .productName(request.getProductName())
                .productImage(savedFileName)
                .productCode(p.getProductCode())
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
                .productImage(baseUrl + "/images/" + saveProduct.getProductImage()) // ✅ Return URL
                .productDescription(saveProduct.getProductDescription())
                .productCategory(saveProduct.getProductCategory())
                .productStock(saveProduct.getProductStock())
                .productPrice(saveProduct.getProductPrice())
                .productIsAvailable(saveProduct.getProductIsAvailable())
                .productIsDelete(saveProduct.getProductIsDelete())
                .updatedDate(saveProduct.getUpdatedDate())
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
    public RestApiResponse<ProductUpdateResponse> delete(String productCode) throws JsonProcessingException {
        Product product = getProductCode(productCode);
        if(product == null){
            throw new IllegalArgumentException("Invalid product Name");
        }

        product.setUpdatedDate(new Date());
        product.setProductIsDelete(true);

        RestApiResponse<ProductUpdateResponse> response = new RestApiResponse<>();
        response.setCode(HttpStatus.NO_CONTENT.toString());
        response.setMessage("Berhasil Delete Produk");

        auditTrailsService.saveAuditTrails(AuditTrailsRequest.builder()
                .AtAction("Delete")
                .AtDescription("Delete Product")
                .AtDate(new Date())
                .AtRequest(String.valueOf(productCode))
                .AtResponse(String.valueOf(response))
                .build());

        return response;
    }

    //TODO keamanan jika frontend minta 1000 size
    public RestApiResponse<Page<Product>> getAllProducts(int page, int size) {
        Page<Product> pageData = productRepository.findAllByProductIsDeleteFalse(PageRequest.of(page, size));
        return RestApiResponse.<Page<Product>>builder()
                .code("200")
                .message("Products retrieved successfully")
                .data(pageData)
                .build();
    }

    public RestApiResponse<Page<Product>> searchProducts(String keyword, Pageable pageable) {
        Page<Product> pageData = productRepository.searchByNameOrCategory(keyword, pageable);
        return RestApiResponse.<Page<Product>>builder()
                .code("200")
                .message("Products retrieved successfully")
                .data(pageData)
                .build();
    }

    @Override
    public Product getProductCode(String productCode){
        return productRepository.findByproductCode(productCode);
    }



}
