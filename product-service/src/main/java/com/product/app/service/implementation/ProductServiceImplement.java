package com.product.app.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.product.app.dto.request.AuditTrailsRequest;
import com.product.app.dto.request.ProductRequest;
import com.product.app.dto.request.ProductUpdateRequest;
import com.product.app.dto.response.RestApiResponse;
import com.product.app.dto.result.ProductCreateResponse;
import com.product.app.dto.result.ProductUpdateResponse;
import com.product.app.entity.product.Product;
import com.product.app.repository.product.ProductRepository;
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
                .createdBy(request.getCreatedBy())
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

    public RestApiResponse<Page<ProductUpdateResponse>> getAllProducts(int page, int size) {
        Page<Product> pageData = productRepository.findAllByProductIsDeleteFalse(PageRequest.of(page, size));

        Page<ProductUpdateResponse> responsePage = pageData.map(product -> {
            ProductUpdateResponse response = new ProductUpdateResponse();
            response.setProductName(product.getProductName());
            response.setProductCode(product.getProductCode());
            response.setProductImage(baseUrl + "/images/" + product.getProductImage());
            response.setProductDescription(product.getProductDescription());
            response.setProductCategory(product.getProductCategory());
            response.setProductStock(product.getProductStock());
            response.setProductPrice(product.getProductPrice());
            response.setProductIsAvailable(product.getProductIsAvailable());
            response.setCreatedBy(product.getCreatedBy());
            response.setCreatedDate(product.getCreatedDate());
            return response;
        });

        return RestApiResponse.<Page<ProductUpdateResponse>>builder()
                .code("200")
                .message("Products retrieved successfully")
                .data(responsePage)
                .build();
    }


    public RestApiResponse<Page<ProductUpdateResponse>> searchProducts(String keyword, Pageable pageable) {
        Page<Product> pageData = productRepository.searchByNameOrCategory(keyword, pageable);

        Page<ProductUpdateResponse> responsePage = pageData.map(product -> {
            ProductUpdateResponse response = new ProductUpdateResponse();
            response.setProductName(product.getProductName());
            response.setProductCode(product.getProductCode());
            response.setProductImage(baseUrl + "/images/" + product.getProductImage());
            response.setProductDescription(product.getProductDescription());
            response.setProductCategory(product.getProductCategory());
            response.setProductStock(product.getProductStock());
            response.setProductPrice(product.getProductPrice());
            response.setProductIsAvailable(product.getProductIsAvailable());
            response.setProductIsDelete(product.getProductIsDelete());
            response.setCreatedBy(product.getCreatedBy());
            response.setCreatedDate(product.getCreatedDate());
            response.setUpdatedDate(product.getUpdatedDate());
            return response;
        });

        return RestApiResponse.<Page<ProductUpdateResponse>>builder()
                .code("200")
                .message("Products retrieved successfully")
                .data(responsePage)
                .build();
    }

    public RestApiResponse<Page<ProductUpdateResponse>> searchProductsAdmin(String keyword, Pageable pageable) {
        Page<Product> pageData = productRepository.searchByNameOrCategoryForAdmin(keyword, pageable);

        Page<ProductUpdateResponse> responsePage = pageData.map(product -> {
            ProductUpdateResponse response = new ProductUpdateResponse();
            response.setProductName(product.getProductName());
            response.setProductCode(product.getProductCode());
            response.setProductImage(baseUrl + "/images/" + product.getProductImage());
            response.setProductDescription(product.getProductDescription());
            response.setProductCategory(product.getProductCategory());
            response.setProductStock(product.getProductStock());
            response.setProductPrice(product.getProductPrice());
            response.setProductIsAvailable(product.getProductIsAvailable());
            response.setProductIsDelete(product.getProductIsDelete());
            response.setCreatedBy(product.getCreatedBy());
            response.setCreatedDate(product.getCreatedDate());
            response.setUpdatedDate(product.getUpdatedDate());
            return response;
        });

        return RestApiResponse.<Page<ProductUpdateResponse>>builder()
                .code("200")
                .message("Products retrieved successfully")
                .data(responsePage)
                .build();
    }

    @Override
    public RestApiResponse<ProductUpdateResponse> getbyCode(String productCode) {
        Product product = getProductCode(productCode);
        if(product == null){
            throw new IllegalArgumentException("Invalid product Name");
        }

        ProductUpdateResponse productResponse = ProductUpdateResponse.builder()
                .productName(product.getProductName())
                .productCode(product.getProductCode())
                .productDescription(product.getProductDescription())
                .productCategory(product.getProductCategory())
                .productImage(baseUrl + "/images/" + product.getProductImage())
                .productPrice(product.getProductPrice())
                .productIsAvailable(product.getProductIsAvailable())
                .productIsDelete(product.getProductIsDelete())
                .productStock(product.getProductStock())
                .build();

        return RestApiResponse.<ProductUpdateResponse>builder()
                .code("200")
                .message("Products retrieved successfully")
                .data(productResponse)
                .build();
    }

    @Override
    public Product getProductCode(String productCode){
        return productRepository.findByproductCode(productCode);
    }


}
