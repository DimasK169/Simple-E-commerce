package com.flash_sale.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flash_sale.app.controller.config.FlashSaleMockConfig;
import com.flash_sale.app.dto.request.FlashSaleRequest;
import com.flash_sale.app.dto.request.FlashSaleUpdateRequest;
import com.flash_sale.app.dto.response.RestApiResponse;
import com.flash_sale.app.dto.result.FlashSaleSaveResponse;
import com.flash_sale.app.dto.result.FlashSaleUpdateResponse;
import com.flash_sale.app.service.Interface.FlashSaleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = FlashSaleController.class)
@Import(FlashSaleMockConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class FlashSaleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FlashSaleService flashSaleService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void FlashSaleController_CreateFlashSale() throws Exception {
        // Given
        FlashSaleRequest request = FlashSaleRequest.builder()
                .fsName("Lebaran Sale")
                .fsCode("FS123")
                .fsStartDate(new Date())
                .fsEndDate(new Date(System.currentTimeMillis() + 86400000))
                .fsCreatedBy("admin")
                .trxDiscount(0.2)
                .productCode(List.of("PROD001"))
                .build();

        FlashSaleSaveResponse responseData = FlashSaleSaveResponse.builder()
                .fsName("Lebaran Sale")
                .fsProduct("Produk Contoh")
                .trxDiscount(0.2)
                .trxPrice(8000000)
                .build();

        RestApiResponse<List<FlashSaleSaveResponse>> response = RestApiResponse.<List<FlashSaleSaveResponse>>builder()
                .code("201 CREATED")
                .message("Flash sale created")
                .data(List.of(responseData))
                .build();

        // When
        when(flashSaleService.createFlashSale(any(FlashSaleRequest.class))).thenReturn(response);

        // Then
        mockMvc.perform(post("/flash-sale/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()) // tergantung apa yang dikembalikan, bisa isCreated()
                .andExpect(jsonPath("$.code").value("201 CREATED"))
                .andExpect(jsonPath("$.data[0].FlashSale_Product").value("Produk Contoh"));
    }

    @Test
    public void FlashSaleController_UpdateFlashSale() throws Exception {
        // Given
        String fsCode = "FS001";
        FlashSaleUpdateRequest request = FlashSaleUpdateRequest.builder()
                .fsName("Lebaran Sale Updated")
                .fsStartDate(new Date())
                .fsEndDate(new Date(System.currentTimeMillis() + 86400000))
                .fsCreatedBy("admin")
                .trxDiscount(0.25)
                .productCode(List.of("PROD001"))
                .build();

        FlashSaleUpdateResponse responseData = FlashSaleUpdateResponse.builder()
                .fsName("Lebaran Sale Updated")
                .fsProduct("Produk Contoh Updated")
                .trxDiscount(0.25)
                .trxPrice(7500000)
                .status("Updated")
                .build();

        RestApiResponse<List<FlashSaleUpdateResponse>> response = RestApiResponse.<List<FlashSaleUpdateResponse>>builder()
                .code("200 OK")
                .message("Flash sale updated")
                .data(List.of(responseData))
                .build();

        // When
        when(flashSaleService.updateFlashSale(any(FlashSaleUpdateRequest.class), eq(fsCode))).thenReturn(response);

        // Then
        mockMvc.perform(patch("/flash-sale/update/{fsCode}", fsCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())  // Expecting status 200 OK
                .andExpect(jsonPath("$.code").value("200 OK"))
                .andExpect(jsonPath("$.data[0].FlashSale_Product").value("Produk Contoh Updated"))
                .andExpect(jsonPath("$.data[0].Status").value("Updated"));
    }

    @Test
    public void FlashSaleController_DeleteFlashSale() throws Exception {
        // Given
        String fsCode = "FS001";

        FlashSaleUpdateResponse responseData = FlashSaleUpdateResponse.builder()
                .fsName("Promo Lebaran")
                .fsCode(fsCode)
                .fsProduct("Product A")
                .productCode("PROD001")
                .trxDiscount(0.2)
                .productPrice(100000)
                .trxPrice(80000)
                .productImage("image.jpg")
                .fsStartDate(new Date())
                .fsEndDate(new Date(System.currentTimeMillis() + 86400000))
                .fsCreatedBy("admin")
                .fsUpdateDate(new Date())
                .fsIsDelete(true)
                .status("Deleted")
                .build();

        RestApiResponse<FlashSaleUpdateResponse> response = RestApiResponse.<FlashSaleUpdateResponse>builder()
                .code("200 OK")
                .message("Flash sale deleted successfully")
                .data(responseData)
                .build();

        // When
        when(flashSaleService.deleteFlashSale(fsCode)).thenReturn(response);

        // Then
        mockMvc.perform(delete("/flash-sale/delete/{fsCode}", fsCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200 OK"))
                .andExpect(jsonPath("$.message").value("Flash sale deleted successfully"))
                .andExpect(jsonPath("$.data.FlashSale_Code").value(fsCode))
                .andExpect(jsonPath("$.data.Status").value("Deleted"))
                .andExpect(jsonPath("$.data.FlashSale_IsDelete").value(true));
    }

    @Test
    public void FlashSaleController_GetByFlashSaleCode() throws Exception {
        // Given
        String fsCode = "FS001";

        FlashSaleUpdateResponse responseData = FlashSaleUpdateResponse.builder()
                .fsName("Flash Sale Akhir Tahun")
                .fsCode(fsCode)
                .fsProduct("Product A")
                .productCode("PROD001")
                .trxDiscount(0.3)
                .productPrice(150000)
                .trxPrice(105000)
                .productImage("image-a.jpg")
                .fsStartDate(new Date())
                .fsEndDate(new Date(System.currentTimeMillis() + 86400000))
                .fsCreatedBy("admin")
                .fsUpdateDate(new Date())
                .fsIsDelete(false)
                .status("Fetched")
                .build();

        RestApiResponse<List<FlashSaleUpdateResponse>> response = RestApiResponse.<List<FlashSaleUpdateResponse>>builder()
                .code("200 OK")
                .message("Data found")
                .data(List.of(responseData))
                .build();

        // When
        when(flashSaleService.getByFlashSaleCode(fsCode)).thenReturn(response);

        // Then
        mockMvc.perform(get("/flash-sale/{fsCode}", fsCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200 OK"))
                .andExpect(jsonPath("$.message").value("Data found"))
                .andExpect(jsonPath("$.data[0].FlashSale_Code").value(fsCode))
                .andExpect(jsonPath("$.data[0].FlashSale_Product").value("Product A"))
                .andExpect(jsonPath("$.data[0].Status").value("Fetched"));
    }

    @Test
    public void FlashSaleController_GetAllFlashSale() throws Exception {
        // Given
        FlashSaleUpdateResponse responseData = FlashSaleUpdateResponse.builder()
                .fsName("Flash Sale Ramadan")
                .fsCode("FS002")
                .fsProduct("Product B")
                .productCode("PROD002")
                .trxDiscount(0.2)
                .productPrice(200000)
                .trxPrice(160000)
                .productImage("image-b.jpg")
                .fsStartDate(new Date())
                .fsEndDate(new Date(System.currentTimeMillis() + 86400000))
                .fsCreatedBy("admin")
                .fsUpdateDate(new Date())
                .fsIsDelete(false)
                .status("Fetched")
                .build();

        List<FlashSaleUpdateResponse> content = List.of(responseData);
        Page<FlashSaleUpdateResponse> pageResponse = new PageImpl<>(content);

        RestApiResponse<Page<FlashSaleUpdateResponse>> response = RestApiResponse.<Page<FlashSaleUpdateResponse>>builder()
                .code("200 OK")
                .message("All flash sales retrieved")
                .data(pageResponse)
                .build();

        // When
        when(flashSaleService.getAllFlashSale(0, 10)).thenReturn(response);

        // Then
        mockMvc.perform(get("/flash-sale/get")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200 OK"))
                .andExpect(jsonPath("$.message").value("All flash sales retrieved"))
                .andExpect(jsonPath("$.data.content[0].FlashSale_Code").value("FS002"))
                .andExpect(jsonPath("$.data.content[0].FlashSale_Product").value("Product B"))
                .andExpect(jsonPath("$.data.content[0].Status").value("Fetched"));
    }

    @Test
    public void FlashSaleController_GetActiveFlashSaleCode() throws Exception {

        Map<String, String> activeData = Map.of("activeCode", "FS123");
        RestApiResponse<Map<String, String>> response = RestApiResponse.<Map<String, String>>builder()
                .code("200 OK")
                .message("Active flash sale found")
                .data(activeData)
                .build();

        when(flashSaleService.getActiveFlashSaleCodeResponse()).thenReturn(response);

        mockMvc.perform(get("/flash-sale/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200 OK"))
                .andExpect(jsonPath("$.data.activeCode").value("FS123"));
    }

}
