package com.product.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.app.dto.request.ProductRequest;
import com.product.app.dto.request.ProductUpdateRequest;
import com.product.app.dto.response.RestApiResponse;
import com.product.app.dto.result.ProductCreateResponse;
import com.product.app.dto.result.ProductUpdateResponse;
import com.product.app.service.interfacing.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
    }

    @Test
    void testCreateProduct() throws Exception {
        ProductRequest productRequest = ProductRequest.builder()
                .productName("Laptop")
                .productCode("P001")
                .productDescription("High performance laptop")
                .productCategory("Electronics")
                .productStock(10)
                .productPrice(1500)
                .productIsAvailable(true)
                .createdBy("admin")
                .build();

        ProductCreateResponse productCreateResponse = ProductCreateResponse.builder()
                .productName("Laptop")
                .productCode("P001")
                .productImage("http://localhost:8080/images/laptop.png")
                .productDescription("High performance laptop")
                .productCategory("Electronics")
                .productStock(10)
                .productPrice(1500)
                .productIsAvailable(true)
                .createdBy("admin")
                .build();

        RestApiResponse<ProductCreateResponse> response = new RestApiResponse<>();
        response.setData(productCreateResponse);
        response.setCode("200");
        response.setMessage("Product created successfully");

        given(productService.create(Mockito.any(ProductRequest.class), Mockito.any(MultipartFile.class)))
                .willReturn(response);

        MvcResult result = mockMvc.perform(multipart("/product")
                        .file("image", "imageContent".getBytes())
                        .param("name", "Laptop")
                        .param("code", "P001")
                        .param("description", "High performance laptop")
                        .param("category", "Electronics")
                        .param("stock", "10")
                        .param("price", "1500")
                        .param("isAvailable", "true")
                        .param("createdBy", "admin"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.productName").value("Laptop"))
                .andExpect(jsonPath("$.message").value("Product created successfully"))
                .andReturn();
    }

    @Test
    void testUpdateProduct() throws Exception {
        // Prepare the product update request
        ProductUpdateRequest productUpdateRequest = ProductUpdateRequest.builder()
                .productName("Updated Laptop")
                .productDescription("Updated description")
                .productCategory("Electronics")
                .productStock(15)
                .productPrice(1600)
                .productIsAvailable(true)
                .build();

        // Mocked response for the updated product
        ProductUpdateResponse productUpdateResponse = ProductUpdateResponse.builder()
                .productName("Updated Laptop")
                .productCode("P001")
                .productImage("http://localhost:8080/images/laptop.png")
                .productDescription("Updated description")
                .productCategory("Electronics")
                .productStock(15)
                .productPrice(1600)
                .productIsAvailable(true)
                .updatedDate(new Date())
                .createdBy("admin")
                .build();

        RestApiResponse<ProductUpdateResponse> response = new RestApiResponse<>();
        response.setData(productUpdateResponse);
        response.setCode("200");
        response.setMessage("Product updated successfully");

        // Mock the service call to return the mocked response
        given(productService.update(Mockito.any(ProductUpdateRequest.class), Mockito.anyString(), Mockito.any(MultipartFile.class)))
                .willReturn(response);

        // Create a MockMultipartFile to simulate the image upload
        MockMultipartFile imageFile = new MockMultipartFile(
                "image",             // Field name (should match the @RequestParam in the controller)
                "imageContent".getBytes()  // The byte content of the file
        );

        // Perform the PUT request and assert the results
        MvcResult result = mockMvc.perform(multipart("/product/P001")
                        .file("image", "imageContent".getBytes())
                        .param("name", "Updated Laptop")
                        .param("description", "Updated description")
                        .param("category", "Electronics")
                        .param("stock", "15")
                        .param("price", "1600")
                        .param("isAvailable", "true")
                        .with(request -> {
                            request.setMethod("PUT"); // or "PATCH" depending on your controller
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.productName").value("Updated Laptop"))
                .andExpect(jsonPath("$.message").value("Product updated successfully"))
                .andReturn();
    }





    @Test
    void testGetProductByCode() throws Exception {
        ProductUpdateResponse productUpdateResponse = ProductUpdateResponse.builder()
                .productName("Laptop")
                .productCode("P001")
                .productImage("http://localhost:8080/images/laptop.png")
                .productDescription("High performance laptop")
                .productCategory("Electronics")
                .productStock(10)
                .productPrice(1500)
                .productIsAvailable(true)
                .updatedDate(new Date())
                .createdBy("admin")
                .build();

        RestApiResponse<ProductUpdateResponse> response = new RestApiResponse<>();
        response.setData(productUpdateResponse);
        response.setCode("200");
        response.setMessage("Product retrieved successfully");

        given(productService.getbyCode(Mockito.anyString()))
                .willReturn(response);

        mockMvc.perform(get("/product/P001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.productCode").value("P001"))
                .andExpect(jsonPath("$.data.productName").value("Laptop"))
                .andExpect(jsonPath("$.message").value("Product retrieved successfully"));
    }

    @Test
    void testDeleteProduct() throws Exception {
        RestApiResponse<ProductUpdateResponse> response = new RestApiResponse<>();
        response.setCode("204");
        response.setMessage("Product deleted successfully");

        given(productService.delete(Mockito.anyString()))
                .willReturn(response);

        mockMvc.perform(delete("/product/P001"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.message").value("Product deleted successfully"));
    }
}
