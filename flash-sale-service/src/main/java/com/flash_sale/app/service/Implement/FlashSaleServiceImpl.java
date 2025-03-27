package com.flash_sale.app.service.Implement;

import com.flash_sale.app.dto.request.AuditTrailsRequest;
import com.flash_sale.app.dto.request.FlashSaleRequest;
import com.flash_sale.app.dto.response.RestApiResponse;
import com.flash_sale.app.dto.result.FlashSaleSaveResponse;
import com.flash_sale.app.dto.result.FlashSaleUpdateResponse;
import com.flash_sale.app.entity.FlashSale;
import com.flash_sale.app.repository.FlashSaleRepository;
import com.flash_sale.app.service.Interface.FlashSaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class FlashSaleServiceImpl implements FlashSaleService {

    @Autowired
    private FlashSaleRepository flashSaleRepository;

    @Autowired
    private AuditTrailsServiceImpl auditTrailsService;


    @Override
    public RestApiResponse<FlashSaleSaveResponse> createFlashSale(FlashSaleRequest flashSaleRequest) {

        FlashSale flashSale = FlashSale.builder()
                .FsName(flashSaleRequest.getFsName())
                .FsProduct(flashSaleRequest.getFsProduct())
                .FsStartDate(flashSaleRequest.getFsStartDate())
                .FsEndDate(flashSaleRequest.getFsEndDate())
                .FsDiscount(flashSaleRequest.getFsDiscount())
                .FsDiscountedPrice(flashSaleRequest.getFsDiscountedPrice())
                .FsStatus(flashSaleRequest.getFsStatus())
                .FsCreatedBy(flashSaleRequest.getFsCreatedBy())
                .FsCreatedDate(new Date())
                .build();

        RestApiResponse<FlashSaleSaveResponse> response = new RestApiResponse<>();
        FlashSale saveFlashSale = flashSaleRepository.save(flashSale);

        FlashSaleSaveResponse flashSaleSaveResponse = FlashSaleSaveResponse.builder()
                .FsName(saveFlashSale.getFsName())
                .FsProduct(saveFlashSale.getFsProduct())
                .FsStartDate(saveFlashSale.getFsStartDate())
                .FsEndDate(saveFlashSale.getFsEndDate())
                .FsDiscount(saveFlashSale.getFsDiscount())
                .FsDiscountedPrice(saveFlashSale.getFsDiscountedPrice())
                .FsStatus(saveFlashSale.getFsStatus())
                .FsCreatedBy(saveFlashSale.getFsCreatedBy())
                .FsCreatedDate(saveFlashSale.getFsCreatedDate())
                .build();

        auditTrailsService.saveAuditTrails(AuditTrailsRequest.builder()
                .AtAction("Create Flash Sale")
                .AtDescription("New Flash Sale")
                .AtDate(new Date())
                .AtRequest(String.valueOf(flashSaleRequest))
                .AtResponse(String.valueOf(flashSaleSaveResponse))
                .build());

        response.setData(flashSaleSaveResponse);
        response.setCode(HttpStatus.OK.toString());
        response.setMessage("Berhasil Create Flash-sale");

        return response;

    }

    @Override
    public RestApiResponse<FlashSaleUpdateResponse> updateFlashSale(FlashSaleRequest flashSaleRequest, String fsName) {
        FlashSale t = getFsName(fsName);
        if (t == null){
            throw new IllegalArgumentException("Invalid Flash-Sale Name");
        }

        FlashSale flashSale = FlashSale.builder()
                .FsId(t.getFsId())
                .FsName(t.getFsName())
                .FsProduct(flashSaleRequest.getFsProduct())
                .FsStartDate(flashSaleRequest.getFsStartDate())
                .FsEndDate(flashSaleRequest.getFsEndDate())
                .FsDiscount(flashSaleRequest.getFsDiscount())
                .FsDiscountedPrice(flashSaleRequest.getFsDiscountedPrice())
                .FsStatus(flashSaleRequest.getFsStatus())
                .FsCreatedBy(flashSaleRequest.getFsCreatedBy())
                .FsUpdateDate(new Date())
                .FsIsDelete(false)
                .build();

        FlashSale saveFlashSale = flashSaleRepository.save(flashSale);

        FlashSaleUpdateResponse flashSaleUpdateResponse = FlashSaleUpdateResponse.builder()
                .FsId(t.getFsId())
                .FsName(saveFlashSale.getFsName())
                .FsProduct(saveFlashSale.getFsProduct())
                .FsStartDate(saveFlashSale.getFsStartDate())
                .FsEndDate(saveFlashSale.getFsEndDate())
                .FsDiscount(saveFlashSale.getFsDiscount())
                .FsDiscountedPrice(saveFlashSale.getFsDiscountedPrice())
                .FsStatus(saveFlashSale.getFsStatus())
                .FsCreatedBy(saveFlashSale.getFsCreatedBy())
                .FsUpdateDate(saveFlashSale.getFsUpdateDate())
                .FsIsDelete(saveFlashSale.getFsIsDelete())
                .build();

        RestApiResponse<FlashSaleUpdateResponse> response = new RestApiResponse<>();
        auditTrailsService.saveAuditTrails(AuditTrailsRequest.builder()
                .AtAction("Update")
                .AtDescription("Update Flash Sale")
                .AtDate(new Date())
                .AtRequest(String.valueOf(flashSaleRequest))
                .AtResponse(String.valueOf(flashSaleUpdateResponse))
                .build());

        response.setData(flashSaleUpdateResponse);
        response.setCode(HttpStatus.OK.toString());
        response.setMessage("Berhasil Update Flash-sale");

        return response;
    }

    @Override
    public RestApiResponse<FlashSaleUpdateResponse> deleteFlashSale(FlashSaleRequest flashSaleRequest, String fsName) {
        FlashSale t = getFsName(fsName);
        if (t == null){
            throw new IllegalArgumentException("Invalid Flash-Sale Name");
        }

        // Update hanya kolom yang diperlukan
        t.setFsUpdateDate(new Date());
        t.setFsIsDelete(true);

        // Simpan perubahan (bukan buat objek baru)
        FlashSale saveFlashSale = flashSaleRepository.save(t);

        FlashSaleUpdateResponse flashSaleUpdateResponse = FlashSaleUpdateResponse.builder()
                .FsId(saveFlashSale.getFsId())
                .FsName(saveFlashSale.getFsName())
                .FsProduct(saveFlashSale.getFsProduct())
                .FsStartDate(saveFlashSale.getFsStartDate())
                .FsEndDate(saveFlashSale.getFsEndDate())
                .FsDiscount(saveFlashSale.getFsDiscount())
                .FsDiscountedPrice(saveFlashSale.getFsDiscountedPrice())
                .FsStatus(saveFlashSale.getFsStatus())
                .FsCreatedBy(saveFlashSale.getFsCreatedBy())
                .FsUpdateDate(saveFlashSale.getFsUpdateDate())
                .FsIsDelete(saveFlashSale.getFsIsDelete())
                .build();

        RestApiResponse<FlashSaleUpdateResponse> response = new RestApiResponse<>();
        auditTrailsService.saveAuditTrails(AuditTrailsRequest.builder()
                .AtAction("Delete")
                .AtDescription("Delete Flash Sale")
                .AtDate(new Date())
                .AtRequest(String.valueOf(fsName))
                .AtResponse(String.valueOf(flashSaleUpdateResponse))
                .build());

        response.setData(flashSaleUpdateResponse);
        response.setCode(HttpStatus.OK.toString());
        response.setMessage("Berhasil menghapus Flash-sale");

        return response;

    }

    public FlashSale getFsName(String fsName){
        FlashSale flashSale = flashSaleRepository.findByFsName(fsName);
        return flashSale;
    }
}
