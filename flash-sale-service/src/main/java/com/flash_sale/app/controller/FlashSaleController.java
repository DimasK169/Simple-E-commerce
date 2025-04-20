package com.flash_sale.app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.flash_sale.app.dto.request.FlashSaleRequest;
import com.flash_sale.app.dto.request.FlashSaleUpdateRequest;
import com.flash_sale.app.dto.response.RestApiResponse;
import com.flash_sale.app.dto.result.FlashSaleSaveResponse;
import com.flash_sale.app.dto.result.FlashSaleUpdateResponse;
import com.flash_sale.app.entity.flash_sale.FlashSale;
import com.flash_sale.app.service.Interface.FlashSaleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/flash-sale")
public class FlashSaleController {

    @Autowired
    private FlashSaleService flashSaleService;

    @PostMapping("/save")
    public RestApiResponse<List<FlashSaleSaveResponse>> createFlashSale(@Valid @RequestBody FlashSaleRequest flashSaleRequest) throws JsonProcessingException {
        return flashSaleService.createFlashSale(flashSaleRequest);
    }

    @PatchMapping("/update/{fsCode}")
    public RestApiResponse<List<FlashSaleUpdateResponse>> updateFlashSale(@Valid @RequestBody FlashSaleUpdateRequest flashSaleRequest, @PathVariable String fsCode) throws JsonProcessingException{
        return flashSaleService.updateFlashSale(flashSaleRequest, fsCode);
    }

    @DeleteMapping("/delete/{fsCode}")
    public RestApiResponse<FlashSaleUpdateResponse> deleteFlashSale(@PathVariable String fsCode) throws JsonProcessingException{
        return flashSaleService.deleteFlashSale(fsCode);
    }

    @GetMapping("/{fsCode}")
    public RestApiResponse<List<FlashSaleUpdateResponse>> getByFlashSaleCode(@PathVariable String fsCode) throws JsonProcessingException{
        return flashSaleService.getByFlashSaleCode(fsCode);
    }

    @GetMapping("/get")
    public RestApiResponse<Page<FlashSaleUpdateResponse>> getAllFlashSale(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return flashSaleService.getAllFlashSale(page, size);
    }

    @GetMapping("/active")
    public ResponseEntity<RestApiResponse<Map<String, String>>> getActiveFlashSaleCode() {
        RestApiResponse<Map<String, String>> response = flashSaleService.getActiveFlashSaleCodeResponse();
        if (response.getData() != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
