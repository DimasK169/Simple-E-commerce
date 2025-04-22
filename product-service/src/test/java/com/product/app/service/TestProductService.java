package com.product.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.product.app.dto.request.AuditTrailsRequest;
import com.product.app.dto.request.ProductRequest;
import com.product.app.dto.request.ProductUpdateRequest;
import com.product.app.dto.response.RestApiResponse;
import com.product.app.dto.result.ProductCreateResponse;
import com.product.app.dto.result.ProductUpdateResponse;
import com.product.app.entity.product.Product;
import com.product.app.repository.product.ProductRepository;
import com.product.app.service.implementation.AuditTrailsServiceImp;
import com.product.app.service.implementation.FileStorageService;
import com.product.app.service.implementation.ProductServiceImplement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestProductService {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private AuditTrailsServiceImp auditTrailsService;

    @InjectMocks
    private ProductServiceImplement productService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(productService, "baseUrl", "http://localhost:8080");
    }

    @Test
    void testCreateProductSuccessfully() throws IOException {

        ProductRequest request = ProductRequest.builder()
                .productCode("P001")
                .productName("Laptop")
                .productDescription("A good laptop")
                .productCategory("Electronics")
                .productStock(10)
                .productPrice(1500)
                .productIsAvailable(true)
                .createdBy("admin")
                .build();


        byte[] content = "fake image".getBytes();
        MockMultipartFile imageFile = new MockMultipartFile("image", "laptop.png", "image/png", content);


        when(productRepository.existsByProductCode("P001")).thenReturn(false);
        when(fileStorageService.storeFile(imageFile)).thenReturn("stored-laptop.png");

        Product savedProduct = Product.builder()
                .productCode("P001")
                .productName("Laptop")
                .productDescription("A good laptop")
                .productCategory("Electronics")
                .productStock(10)
                .productPrice(1500)
                .productImage("stored-laptop.png")
                .productIsAvailable(true)
                .productIsDelete(false)
                .createdBy("admin")
                .createdDate(new Date())
                .build();

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);


        RestApiResponse<ProductCreateResponse> response = productService.create(request, imageFile);


        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo(HttpStatus.CREATED.toString());
        assertThat(response.getMessage()).isEqualTo("Berhasil Membuat Produk");
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData().getProductCode()).isEqualTo("P001");

        String baseUrl = "http://localhost:8080";
        assertThat(response.getData().getProductImage()).isEqualTo(baseUrl + "/images/stored-laptop.png");


        verify(productRepository).existsByProductCode("P001");
        verify(fileStorageService).storeFile(imageFile);
        verify(productRepository).save(any(Product.class));
        verify(auditTrailsService).saveAuditTrails(any(AuditTrailsRequest.class));
    }

    @Test
    void testUpdateProduct() throws IOException {
        Product existingProduct = Product.builder()
                .productId(1L)
                .productCode("P001")
                .productName("Laptop")  // Original name
                .productCategory("Electronics")
                .productStock(10)
                .productPrice(1500)
                .productIsAvailable(true)
                .productIsDelete(false)
                .createdDate(new Date())
                .createdBy("admin")
                .productImage("old-image.png")
                .build();

        ProductUpdateRequest request = ProductUpdateRequest.builder()
                .productName("Updated Laptop")  // Updated name
                .productCategory("Electronics")
                .productStock(15)
                .productPrice(1600)
                .productIsAvailable(true)
                .productDescription("Updated laptop description")
                .build();

        byte[] content = "new image content".getBytes();
        MockMultipartFile imageFile = new MockMultipartFile("image", "updated-laptop.png", "image/png", content);

        // Mocking the repository method to return the existing product
        when(productRepository.findByproductCode("P001")).thenReturn(existingProduct);

        // Mocking the file storage service to return the new image file name
        when(fileStorageService.storeFile(imageFile)).thenReturn("updated-laptop.png");

        // Mocking save to return the updated product with new name
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product updatedProduct = invocation.getArgument(0);
            updatedProduct.setProductName("Updated Laptop");  // Make sure the updated name is set
            return updatedProduct;
        });

        // Act
        RestApiResponse<ProductUpdateResponse> response = productService.update(request, "P001", imageFile);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo(HttpStatus.OK.toString());
        assertThat(response.getMessage()).isEqualTo("Berhasil Update Produk");
        assertThat(response.getData().getProductName()).isEqualTo("Updated Laptop");

        // Verify interactions
        verify(productRepository, times(1)).findByproductCode("P001");
        verify(fileStorageService, times(1)).storeFile(imageFile);
        verify(productRepository, times(1)).save(any(Product.class));
        verify(auditTrailsService, times(1)).saveAuditTrails(any(AuditTrailsRequest.class));
    }

    @Test
    void testDeleteProduct() throws JsonProcessingException {
        Product existingProduct = Product.builder()
                .productId(1L)
                .productCode("P001")
                .productName("Laptop")
                .productCategory("Electronics")
                .productStock(10)
                .productPrice(1500)
                .productIsAvailable(true)
                .productIsDelete(false)
                .createdDate(new Date())
                .createdBy("admin")
                .build();

        // Mock repository method
        when(productRepository.findByproductCode("P001")).thenReturn(existingProduct);

        // Act
        RestApiResponse<ProductUpdateResponse> response = productService.delete("P001");

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo(HttpStatus.NO_CONTENT.toString());
        assertThat(response.getMessage()).isEqualTo("Berhasil Delete Produk");

        // Verify interactions
        verify(productRepository, times(1)).findByproductCode("P001");
        verify(auditTrailsService, times(1)).saveAuditTrails(any(AuditTrailsRequest.class));
    }

    @Test
    void testGetAllProducts() throws JsonProcessingException {
        Product product = Product.builder()
                .productId(1L)
                .productCode("P001")
                .productName("Laptop")
                .productCategory("Electronics")
                .productStock(10)
                .productPrice(1500)
                .productIsAvailable(true)
                .productIsDelete(false)
                .createdDate(new Date())
                .createdBy("admin")
                .productImage("laptop.png")
                .build();

        Page<Product> pageData = new PageImpl<>(Collections.singletonList(product));

        // Mock repository method
        when(productRepository.findAllByProductIsDeleteFalse(PageRequest.of(0, 10))).thenReturn(pageData);

        // Act
        RestApiResponse<Page<ProductUpdateResponse>> response = productService.getAllProducts(0, 10);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo("200");
        assertThat(response.getMessage()).isEqualTo("Products retrieved successfully");
        assertThat(response.getData().getContent().get(0).getProductName()).isEqualTo("Laptop");

        // Verify interactions
        verify(productRepository, times(1)).findAllByProductIsDeleteFalse(PageRequest.of(0, 10));
        verify(auditTrailsService, times(1)).saveAuditTrails(any(AuditTrailsRequest.class));
    }

    @Test
    void testSearchProducts() throws JsonProcessingException {
        Product product = Product.builder()
                .productId(1L)
                .productCode("P001")
                .productName("Laptop")
                .productCategory("Electronics")
                .productStock(10)
                .productPrice(1500)
                .productIsAvailable(true)
                .productIsDelete(false)
                .createdDate(new Date())
                .createdBy("admin")
                .productImage("laptop.png")
                .build();

        Page<Product> pageData = new PageImpl<>(Collections.singletonList(product));

        // Mock repository method
        when(productRepository.searchByNameOrCategory("Laptop", PageRequest.of(0, 10))).thenReturn(pageData);

        // Act
        RestApiResponse<Page<ProductUpdateResponse>> response = productService.searchProducts("Laptop", PageRequest.of(0, 10));

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo("200");
        assertThat(response.getMessage()).isEqualTo("Products retrieved successfully");
        assertThat(response.getData().getContent().get(0).getProductName()).isEqualTo("Laptop");

        // Verify interactions
        verify(productRepository, times(1)).searchByNameOrCategory("Laptop", PageRequest.of(0, 10));
        verify(auditTrailsService, times(1)).saveAuditTrails(any(AuditTrailsRequest.class));
    }

    @Test
    void testSearchProductsForAdmin() throws JsonProcessingException {
        Product product = Product.builder()
                .productId(1L)
                .productCode("P001")
                .productName("Laptop")
                .productCategory("Electronics")
                .productStock(10)
                .productPrice(1500)
                .productIsAvailable(true)
                .productIsDelete(false)
                .createdDate(new Date())
                .createdBy("admin")
                .productImage("laptop.png")
                .build();

        Page<Product> pageData = new PageImpl<>(Collections.singletonList(product));

        // Mock repository method
        when(productRepository.searchByNameOrCategoryForAdmin("Laptop", PageRequest.of(0, 10))).thenReturn(pageData);

        // Act
        RestApiResponse<Page<ProductUpdateResponse>> response = productService.searchProductsAdmin("Laptop", PageRequest.of(0, 10));

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo("200");
        assertThat(response.getMessage()).isEqualTo("Products retrieved successfully");
        assertThat(response.getData().getContent().get(0).getProductName()).isEqualTo("Laptop");

        // Verify interactions
        verify(productRepository, times(1)).searchByNameOrCategoryForAdmin("Laptop", PageRequest.of(0, 10));
        verify(auditTrailsService, times(1)).saveAuditTrails(any(AuditTrailsRequest.class));
    }


}
