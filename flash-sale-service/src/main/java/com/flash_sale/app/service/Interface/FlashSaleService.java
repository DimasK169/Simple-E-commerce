package com.flash_sale.app.service.Interface;

import com.flash_sale.app.dto.request.FlashSaleRequest;
import com.flash_sale.app.dto.response.RestApiResponse;
import com.flash_sale.app.dto.result.FlashSaleSaveResponse;
import com.flash_sale.app.dto.result.FlashSaleUpdateResponse;

public interface FlashSaleService {

    RestApiResponse<FlashSaleSaveResponse> createFlashSale(FlashSaleRequest flashSaleRequest);

    RestApiResponse<FlashSaleUpdateResponse> updateFlashSale(FlashSaleRequest flashSaleRequest, String fsName);

    RestApiResponse<FlashSaleUpdateResponse> deleteFlashSale(FlashSaleRequest flashSaleRequest,String fsName);
}
