package com.flash_sale.app.controller;

import com.flash_sale.app.dto.request.FlashSaleRequest;
import com.flash_sale.app.dto.response.RestApiResponse;
import com.flash_sale.app.dto.result.FlashSaleSaveResponse;
import com.flash_sale.app.dto.result.FlashSaleUpdateResponse;
import com.flash_sale.app.service.Interface.FlashSaleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/flash-sale")
public class FlashSaleController {

    @Autowired
    private FlashSaleService flashSaleService;

    @PostMapping("/save")
    public RestApiResponse<FlashSaleSaveResponse> createFlashSale(@Valid @RequestBody FlashSaleRequest flashSaleRequest ){
        return flashSaleService.createFlashSale(flashSaleRequest);
    }

    @PatchMapping("/update/{FsName}")
    public RestApiResponse<FlashSaleUpdateResponse> createFlashSale(@Valid @RequestBody FlashSaleRequest flashSaleRequest, @PathVariable String FsName){
        return flashSaleService.updateFlashSale(flashSaleRequest, FsName);
    }

    @PatchMapping("/delete/{FsName}")
    public RestApiResponse<FlashSaleUpdateResponse> deleteFlashSale(@RequestBody FlashSaleRequest flashSaleRequest,@PathVariable String FsName){
        return flashSaleService.deleteFlashSale(flashSaleRequest, FsName);
    }
}
