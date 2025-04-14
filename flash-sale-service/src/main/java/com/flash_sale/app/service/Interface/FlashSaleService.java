package com.flash_sale.app.service.Interface;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.flash_sale.app.dto.request.FlashSaleRequest;
import com.flash_sale.app.dto.request.FlashSaleUpdateRequest;
import com.flash_sale.app.dto.response.RestApiResponse;
import com.flash_sale.app.dto.result.FlashSaleSaveResponse;
import com.flash_sale.app.dto.result.FlashSaleUpdateResponse;
import com.flash_sale.app.entity.flash_sale.FlashSale;
import org.springframework.data.domain.Page;

import java.util.List;

public interface FlashSaleService {

    RestApiResponse<List<FlashSaleSaveResponse>> createFlashSale(FlashSaleRequest request) throws JsonProcessingException;

    RestApiResponse<List<FlashSaleUpdateResponse>> updateFlashSale(FlashSaleUpdateRequest request, String fsCode) throws JsonProcessingException;

    RestApiResponse<FlashSaleUpdateResponse> deleteFlashSale(String fsCode) throws JsonProcessingException;

    RestApiResponse<List<FlashSaleUpdateResponse>> getByFlashSaleCode(String fsCode) throws JsonProcessingException;

    RestApiResponse<Page<FlashSale>> getAllFlashSale(int page, int size);
}

