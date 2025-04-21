package com.flash_sale.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.flash_sale.app.dto.request.FlashSaleRequest;
import com.flash_sale.app.dto.request.FlashSaleUpdateRequest;
import com.flash_sale.app.dto.response.RestApiResponse;
import com.flash_sale.app.dto.result.FlashSaleSaveResponse;
import com.flash_sale.app.dto.result.FlashSaleUpdateResponse;
import com.flash_sale.app.entity.flash_sale.FlashSale;
import com.flash_sale.app.entity.flash_sale.TrxFlashSale;
import com.flash_sale.app.entity.product.Product;
import com.flash_sale.app.repository.flash_sale.FlashSaleRepository;
import com.flash_sale.app.repository.flash_sale.TrxFlashSaleRepository;
import com.flash_sale.app.repository.product.ProductRepository;
import com.flash_sale.app.service.Implement.AuditTrailsServiceImpl;
import com.flash_sale.app.service.Implement.FlashSaleServiceImpl;
import com.pusher.rest.Pusher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FlashSaleServiceTest {

    @Mock
    private FlashSaleRepository flashSaleRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private TrxFlashSaleRepository trxFlashSaleRepository;

    @Mock
    private Pusher pusher;

    @Mock
    private AuditTrailsServiceImpl auditTrailsService;

    @InjectMocks
    private FlashSaleServiceImpl flashSaleService;

    @Test
    public void FlashSaleService_Create_ReturnFlashSaleDto() throws JsonProcessingException {
        // Given
        FlashSaleRequest request = FlashSaleRequest.builder()
                .fsName("Ramadhan Sale")
                .fsCode("FS2025")
                .fsStartDate(LocalDateTime.now().minusDays(1))
                .fsEndDate(LocalDateTime.now().plusDays(3))
                .fsCreatedBy("admin")
                .trxDiscount(0.2)
                .productCode(List.of("PROD001"))
                .build();

        Product mockProduct = Product.builder()
                .productId(1L)
                .productName("iPhone 15")
                .productCode("PROD001")
                .productPrice(15000000)
                .build();

        FlashSale mockSavedFlashSale = FlashSale.builder()
                .fsId(10L)
                .fsCode("FS2025")
                .fsProduct("iPhone 15")
                .fsName("Ramadhan Sale")
                .fsStartDate(request.getFsStartDate())
                .fsEndDate(request.getFsEndDate())
                .fsCreatedBy("admin")
                .fsIsDelete(false)
                .fsCreatedDate(new Date())
                .build();

        TrxFlashSale mockTrx = TrxFlashSale.builder()
                .productId(1L)
                .fsId(10L)
                .fsCode("FS2025")
                .productCode("PROD001")
                .trxDiscount(0.2)
                .trxPrice(12000000)
                .build();

        // When
        when(flashSaleRepository.findByFsCode("FS2025")).thenReturn(Collections.emptyList());
        when(productRepository.findByproductCode("PROD001")).thenReturn(List.of(mockProduct));
        when(flashSaleRepository.save(any(FlashSale.class))).thenReturn(mockSavedFlashSale);
        when(trxFlashSaleRepository.save(any(TrxFlashSale.class))).thenReturn(mockTrx);

        RestApiResponse<List<FlashSaleSaveResponse>> response = flashSaleService.createFlashSale(request);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK.toString(), response.getCode());
        assertEquals(1, response.getData().size());
        assertEquals("FS2025", response.getData().get(0).getFsCode());
        assertEquals(12000000, response.getData().get(0).getTrxPrice());

        verify(pusher, times(1)).trigger(eq("flash-sale"), eq("create"), any());
        verify(flashSaleRepository, times(1)).save(any(FlashSale.class));
        verify(trxFlashSaleRepository, times(1)).save(any(TrxFlashSale.class));
    }

    @Test
    public void FlashSaleService_CreateFlashSale_FsCodeAlreadyUsed_ThrowsException() {
        // Given
        String fsCode = "FS123";
        String productCode = "PRD001";

        FlashSaleRequest request = FlashSaleRequest.builder()
                .fsCode(fsCode)
                .fsName("Flash Sale Penuh")
                .fsCreatedBy("admin")
                .fsStartDate(LocalDateTime.now().minusDays(1))
                .fsEndDate(LocalDateTime.now().plusDays(3))
                .productCode(List.of(productCode))
                .trxDiscount(0.2)
                .build();

        // Simulasikan flash sale dengan kode tersebut sudah ada
        List<FlashSale> existingFs = List.of(
                FlashSale.builder().fsCode(fsCode).build()
        );

        when(flashSaleRepository.findByFsCode(fsCode)).thenReturn(existingFs);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            flashSaleService.createFlashSale(request);
        });

        assertEquals("Flash sale code has been used", exception.getMessage());
        verify(flashSaleRepository, times(1)).findByFsCode(fsCode);
        verifyNoMoreInteractions(flashSaleRepository);
        verifyNoInteractions(productRepository);
    }

    @Test
    public void FlashSaleService_CreateFlashSale_ProductNotFound_ThrowsException() {
        // Given
        String fsCode = "FS999";
        String missingProductCode = "NOTFOUND";

        FlashSaleRequest request = FlashSaleRequest.builder()
                .fsCode(fsCode)
                .fsName("Flash Sale Gagal")
                .fsCreatedBy("admin")
                .fsStartDate(LocalDateTime.now().minusDays(1))
                .fsEndDate(LocalDateTime.now().plusDays(3))
                .productCode(List.of(missingProductCode))
                .trxDiscount(0.3)
                .build();

        // Flash sale code belum digunakan
        when(flashSaleRepository.findByFsCode(fsCode)).thenReturn(Collections.emptyList());

        // Product code tidak ditemukan
        when(productRepository.findByproductCode(missingProductCode)).thenReturn(Collections.emptyList());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            flashSaleService.createFlashSale(request);
        });

        assertEquals("Product code not found: " + missingProductCode, exception.getMessage());

        verify(flashSaleRepository, times(1)).findByFsCode(fsCode);
        verify(productRepository, times(1)).findByproductCode(missingProductCode);
        verifyNoMoreInteractions(productRepository);
    }


    @Test
    public void FlashSaleService_Update_ReturnUpdatedFlashSaleDto() throws JsonProcessingException {
        // Given
        String fsCode = "FS2025";
        String productCode = "PROD001";

        FlashSaleUpdateRequest request = FlashSaleUpdateRequest.builder()
                .fsName("Ramadhan Super Sale")
                .fsStartDate(LocalDateTime.now())
                .fsEndDate(LocalDateTime.now().plusDays(5))
                .fsCreatedBy("admin")
                .trxDiscount(0.3)
                .productCode(List.of(productCode))
                .build();

        Product product = Product.builder()
                .productId(1L)
                .productCode(productCode)
                .productName("Samsung Galaxy")
                .productPrice(10000000)
                .build();

        FlashSale existingFlashSale = FlashSale.builder()
                .fsId(99L)
                .fsCode(fsCode)
                .fsProduct("Samsung Galaxy")
                .fsCreatedDate(new Date())
                .build();

        TrxFlashSale existingTrx = TrxFlashSale.builder()
                .trxFlashSaleId(88L)
                .productId(1L)
                .fsCode(fsCode)
                .build();

        // When
        when(productRepository.findByproductCode(productCode)).thenReturn(List.of(product));
        when(flashSaleRepository.findByFsCodeAndFsProduct(fsCode, "Samsung Galaxy")).thenReturn(Optional.of(existingFlashSale));
        when(flashSaleRepository.save(any(FlashSale.class))).thenAnswer(i -> i.getArgument(0));
        when(trxFlashSaleRepository.findByProductIdAndFsCode(1L, fsCode)).thenReturn(Optional.of(existingTrx));
        when(trxFlashSaleRepository.save(any(TrxFlashSale.class))).thenAnswer(i -> i.getArgument(0));

        // Then
        RestApiResponse<List<FlashSaleUpdateResponse>> response = flashSaleService.updateFlashSale(request, fsCode);

        assertNotNull(response);
        assertEquals("200 OK", response.getCode());
        assertEquals(1, response.getData().size());

        FlashSaleUpdateResponse data = response.getData().get(0);
        assertEquals("Samsung Galaxy", data.getFsProduct());
        assertEquals(7000000, data.getTrxPrice()); // 10jt * 0.7
        assertEquals("Updated", data.getStatus());

        verify(auditTrailsService, times(1)).saveAuditTrails(any());
        verify(flashSaleRepository, times(1)).save(any(FlashSale.class));
        verify(trxFlashSaleRepository, times(1)).save(any(TrxFlashSale.class));
    }

    @Test
    public void FlashSaleService_UpdateFlashSale_ProductNotFound_ThrowsException() {
        // Given
        String fsCode = "FS123";
        String productCode = "INVALID";

        FlashSaleUpdateRequest request = FlashSaleUpdateRequest.builder()
                .fsName("Update Flash")
                .fsCreatedBy("admin")
                .fsStartDate(LocalDateTime.now().minusDays(1))
                .fsEndDate(LocalDateTime.now().plusDays(3))
                .trxDiscount(0.1)
                .productCode(List.of(productCode))
                .build();

        // Product tidak ditemukan
        when(productRepository.findByproductCode(productCode)).thenReturn(Collections.emptyList());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            flashSaleService.updateFlashSale(request, fsCode);
        });

        assertEquals("Product code not found: " + productCode, exception.getMessage());

        verify(productRepository, times(1)).findByproductCode(productCode);
        verifyNoInteractions(flashSaleRepository);
    }

    @Test
    public void FlashSaleService_DeleteFlashSale_Success() throws JsonProcessingException {
        // Given
        String fsCode = "FS2025";

        FlashSale flashSale = FlashSale.builder()
                .fsId(1L)
                .fsCode(fsCode)
                .fsName("Promo Lebaran")
                .fsIsDelete(false)
                .build();

        List<FlashSale> fsList = List.of(flashSale);

        // Mock getFsCode()
        FlashSaleServiceImpl spyService = Mockito.spy(flashSaleService);
        doReturn(fsList).when(spyService).getFsCode(fsCode);

        // When
        RestApiResponse<FlashSaleUpdateResponse> response = spyService.deleteFlashSale(fsCode);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT.toString(), response.getCode());
        assertEquals("Berhasil Delete Flash Sale", response.getMessage());

        assertTrue(fsList.get(0).getFsIsDelete());

        verify(flashSaleRepository, times(1)).saveAll(fsList);
        verify(auditTrailsService, times(1)).saveAuditTrails(any());
    }

    @Test
    public void FlashSaleService_DeleteFlashSale_InvalidCode_ThrowsException() {
        // Given
        String fsCode = "INVALID_CODE";
        FlashSaleServiceImpl spyService = Mockito.spy(flashSaleService);

        doReturn(Collections.emptyList()).when(spyService).getFsCode(fsCode);

        // Then
        assertThrows(IllegalArgumentException.class, () -> {
            spyService.deleteFlashSale(fsCode);
        });

        verify(flashSaleRepository, never()).saveAll(any());
        verify(auditTrailsService, never()).saveAuditTrails(any());
    }

    @Test
    public void FlashSaleService_GetByCode_ReturnFlashSaleList() throws JsonProcessingException {
        // Given
        String fsCode = "FS2025";
        Long fsId = 1L;
        Long productId = 99L;

        FlashSale flashSale = FlashSale.builder()
                .fsId(fsId)
                .fsCode(fsCode)
                .fsName("Flash Sale Lebaran")
                .fsProduct("iPhone 15")
                .fsStartDate(LocalDateTime.now())
                .fsEndDate(LocalDateTime.now().plusDays(2))
                .fsCreatedBy("admin")
                .fsIsDelete(false)
                .fsUpdateDate(new Date())
                .build();

        TrxFlashSale trxFlashSale = TrxFlashSale.builder()
                .trxFlashSaleId(123L)
                .productId(productId)
                .productCode("IP15")
                .fsId(fsId)
                .fsCode(fsCode)
                .trxDiscount(0.2)
                .trxPrice(16000000)
                .build();

        Product product = Product.builder()
                .productId(productId)
                .productCode("IP15")
                .productName("iPhone 15")
                .productPrice(20000000)
                .productImage("iphone15.jpg")
                .build();

        // When
        when(flashSaleRepository.findByFsCode(fsCode)).thenReturn(List.of(flashSale));
        when(trxFlashSaleRepository.findByFsId(fsId)).thenReturn(List.of(trxFlashSale));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // Then
        RestApiResponse<List<FlashSaleUpdateResponse>> response = flashSaleService.getByFlashSaleCode(fsCode);

        assertNotNull(response);
        assertEquals("200 OK", response.getCode());
        assertEquals("Berhasil menampilkan data flash sale", response.getMessage());
        assertEquals(1, response.getData().size());

        FlashSaleUpdateResponse responseData = response.getData().get(0);
        assertEquals("iPhone 15", responseData.getFsProduct());
        assertEquals("IP15", responseData.getProductCode());
        assertEquals(16000000, responseData.getTrxPrice());
        assertEquals(20000000, responseData.getProductPrice());
        assertTrue(responseData.getProductImage().contains("iphone15.jpg"));

        verify(auditTrailsService, times(1)).saveAuditTrails(any());
    }

    @Test
    public void FlashSaleService_GetByFlashSaleCode_InvalidCode_ThrowsException() {
        // Given
        String fsCode = "NOT_EXIST";

        when(flashSaleRepository.findByFsCode(fsCode)).thenReturn(Collections.emptyList());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            flashSaleService.getByFlashSaleCode(fsCode);
        });

        assertEquals("Invalid Flash-Sale Code", exception.getMessage());

        verify(flashSaleRepository, times(1)).findByFsCode(fsCode);
    }

    @Test
    public void FlashSaleService_GetFsCode_ReturnsListOfFlashSale() {
        // Given
        String fsCode = "FS2025";
        List<FlashSale> flashSaleList = List.of(
                FlashSale.builder().fsCode(fsCode).fsProduct("Product A").build(),
                FlashSale.builder().fsCode(fsCode).fsProduct("Product B").build()
        );

        when(flashSaleRepository.findByFsCode(fsCode)).thenReturn(flashSaleList);

        // When
        List<FlashSale> result = flashSaleService.getFsCode(fsCode);

        // Then
        assertEquals(2, result.size());
        assertEquals(fsCode, result.get(0).getFsCode());
        verify(flashSaleRepository, times(1)).findByFsCode(fsCode);
    }

    @Test
    public void FlashSaleService_GetActiveFlashSaleCode_Found() {
        // Given
        String activeCode = "FS_ACTIVE";
        when(flashSaleRepository.findActiveFlashSaleCode()).thenReturn(Optional.of(activeCode));

        // When
        RestApiResponse<Map<String, String>> response = flashSaleService.getActiveFlashSaleCodeResponse();

        // Then
        assertEquals("200 OK", response.getCode());
        assertEquals("Flash sale aktif ditemukan", response.getMessage());
        assertEquals(activeCode, response.getData().get("FlashSale_Code"));
        assertNull(response.getError());

        verify(flashSaleRepository, times(1)).findActiveFlashSaleCode();
    }

    @Test
    public void FlashSaleService_GetActiveFlashSaleCode_NotFound() {
        // Given
        when(flashSaleRepository.findActiveFlashSaleCode()).thenReturn(Optional.empty());

        // When
        RestApiResponse<Map<String, String>> response = flashSaleService.getActiveFlashSaleCodeResponse();

        // Then
        assertEquals("404 NOT_FOUND", response.getCode());
        assertEquals("Tidak ada flash sale yang aktif saat ini", response.getMessage());
        assertNotNull(response.getError());
        assertEquals("Flash sale tidak ditemukan", response.getError().get(0));

        verify(flashSaleRepository, times(1)).findActiveFlashSaleCode();
    }
}
