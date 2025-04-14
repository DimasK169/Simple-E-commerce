package com.flash_sale.app.service.Implement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.flash_sale.app.dto.request.AuditTrailsRequest;
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
import com.flash_sale.app.service.Interface.FlashSaleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class FlashSaleServiceImpl implements FlashSaleService {

    @Autowired
    private FlashSaleRepository flashSaleRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TrxFlashSaleRepository trxFlashSaleRepository;

    @Autowired
    private AuditTrailsServiceImpl auditTrailsService;

    @Transactional
    @Override
    public RestApiResponse<List<FlashSaleSaveResponse>> createFlashSale(FlashSaleRequest request) throws JsonProcessingException {
        List<FlashSaleSaveResponse> responseList = new ArrayList<>();

        List<FlashSale> fetchFlashSale = flashSaleRepository.findByFsCode(request.getFsCode());
       if (request.getFsCode().equals(fetchFlashSale.get(0).getFsCode())){
           throw new IllegalArgumentException("Flash sale code has been used");
       }

        for (String productCode : request.getProductCode()) {
            List<Product> productList = productRepository.findByproductCode(productCode);
            if (productList.isEmpty()) {
                throw new IllegalArgumentException("Product code not found: " + productCode);
            }

            for (Product saveProduct : productList) {
                FlashSale flashSale = FlashSale.builder()
                        .fsName(request.getFsName())
                        .fsCode(request.getFsCode())
                        .fsProduct(saveProduct.getProductName())
                        .fsStartDate(request.getFsStartDate())
                        .fsEndDate(request.getFsEndDate())
                        .fsCreatedBy(request.getFsCreatedBy())
                        .fsIsDelete(false)
                        .fsCreatedDate(new Date())
                        .build();

                FlashSale savedFlashSale = flashSaleRepository.save(flashSale);

                TrxFlashSale trxFlashSale = TrxFlashSale.builder()
                        .productId(saveProduct.getProductId())
                        .fsId(savedFlashSale.getFsId())
                        .fsCode(savedFlashSale.getFsCode())
                        .trxDiscount(request.getTrxDiscount())
                        .trxPrice((int)(saveProduct.getProductPrice()* (1 - request.getTrxDiscount())))
                        .build();

                trxFlashSaleRepository.save(trxFlashSale);

                FlashSaleSaveResponse flashSaleSaveResponse = FlashSaleSaveResponse.builder()
                        .fsName(savedFlashSale.getFsName())
                        .fsCode(savedFlashSale.getFsCode())
                        .fsProduct(savedFlashSale.getFsProduct())
                        .trxDiscount(trxFlashSale.getTrxDiscount())
                        .trxPrice(trxFlashSale.getTrxPrice())
                        .fsStartDate(savedFlashSale.getFsStartDate())
                        .fsEndDate(savedFlashSale.getFsEndDate())
                        .fsCreatedBy(savedFlashSale.getFsCreatedBy())
                        .fsIsDelete(savedFlashSale.getFsIsDelete())
                        .fsCreatedDate(savedFlashSale.getFsCreatedDate())
                        .build();

                responseList.add(flashSaleSaveResponse);
            }
        }
        return RestApiResponse.<List<FlashSaleSaveResponse>>builder()
                .code(HttpStatus.OK.toString())
                .message("Berhasil Membuat Flash-sale")
                .data(responseList)
                .build();
    }

    @Transactional
    @Override
    public RestApiResponse<List<FlashSaleUpdateResponse>> updateFlashSale(FlashSaleUpdateRequest request, String fsCode) throws JsonProcessingException {
        List<FlashSaleUpdateResponse> responseList = new ArrayList<>();

        for (String productCode : request.getProductCode()) {
            List<Product> productList = productRepository.findByproductCode(productCode);
            if (productList.isEmpty()) {
                throw new IllegalArgumentException("Product code not found: " + productCode);
            }

            for (Product saveProduct : productList) {
                Optional<FlashSale> existingFs = flashSaleRepository.findByFsCodeAndFsProduct(fsCode, saveProduct.getProductName());

                boolean isInsert = existingFs.isEmpty();

                FlashSale updateFlashSale = FlashSale.builder()
                        .fsId(existingFs.map(FlashSale::getFsId).orElse(null))
                        .fsName(request.getFsName())
                        .fsCode(fsCode)
                        .fsProduct(saveProduct.getProductName())
                        .fsStartDate(request.getFsStartDate())
                        .fsEndDate(request.getFsEndDate())
                        .fsCreatedBy(request.getFsCreatedBy())
                        .fsCreatedDate(existingFs.map(FlashSale::getFsCreatedDate).orElse(new Date()))
                        .fsUpdateDate(new Date())
                        .fsIsDelete(false)
                        .build();

                FlashSale updatedFlashSale = flashSaleRepository.save(updateFlashSale);

                Optional<TrxFlashSale> existingTrx = trxFlashSaleRepository.findByProductIdAndFsCode(saveProduct.getProductId(), updatedFlashSale.getFsCode());

                TrxFlashSale trxFlashSale = TrxFlashSale.builder()
                        .trxFlashSaleId(existingTrx.map(TrxFlashSale::getTrxFlashSaleId).orElse(null))
                        .productId(saveProduct.getProductId())
                        .fsId(updatedFlashSale.getFsId())
                        .fsCode(updatedFlashSale.getFsCode())
                        .trxDiscount(request.getTrxDiscount())
                        .trxPrice((int)(saveProduct.getProductPrice()* (1 - request.getTrxDiscount())))
                        .build();

                trxFlashSaleRepository.save(trxFlashSale);

                String status = isInsert ? "Inserted" : "Updated";

                FlashSaleUpdateResponse response = FlashSaleUpdateResponse.builder()
                        .fsName(updatedFlashSale.getFsName())
                        .fsCode(updatedFlashSale.getFsCode())
                        .fsProduct(updatedFlashSale.getFsProduct())
                        .trxDiscount(trxFlashSale.getTrxDiscount())
                        .trxPrice(trxFlashSale.getTrxPrice())
                        .fsStartDate(updatedFlashSale.getFsStartDate())
                        .fsEndDate(updatedFlashSale.getFsEndDate())
                        .fsCreatedBy(updatedFlashSale.getFsCreatedBy())
                        .fsUpdateDate(updatedFlashSale.getFsUpdateDate())
                        .fsIsDelete(updatedFlashSale.getFsIsDelete())
                        .status(status)
                        .build();

                responseList.add(response);

                auditTrailsService.saveAuditTrails(AuditTrailsRequest.builder()
                        .AtAction(status)
                        .AtDescription(status + " Flash Sale for product: " + saveProduct.getProductName())
                        .AtDate(new Date())
                        .AtRequest(String.valueOf(request))
                        .AtResponse(String.valueOf(response))
                        .build());
            }
        }
        return RestApiResponse.<List<FlashSaleUpdateResponse>>builder()
                .code(HttpStatus.OK.toString())
                .message("Berhasil Update Flash-sale")
                .data(responseList)
                .build();
    }

    @Transactional
    @Override
    public RestApiResponse<FlashSaleUpdateResponse> deleteFlashSale(String fsCode) throws JsonProcessingException {
        List<FlashSale> fsList = getFsCode(fsCode);
        if (fsList == null || fsList.isEmpty()) {
            throw new IllegalArgumentException("Invalid Flash-Sale Code");
        }

        for (FlashSale flashSale : fsList) {
            flashSale.setFsUpdateDate(new Date());
            flashSale.setFsIsDelete(true);
        }

        flashSaleRepository.saveAll(fsList);

        auditTrailsService.saveAuditTrails(AuditTrailsRequest.builder()
                .AtAction("Delete")
                .AtDescription("Delete Flash Sale")
                .AtDate(new Date())
                .AtRequest(String.valueOf(fsCode))
                .AtResponse("Flash Sale berhasil dihapus")
                .build());

        return RestApiResponse.<FlashSaleUpdateResponse>builder()
                .code(HttpStatus.NO_CONTENT.toString())
                .message("Berhasil Delete Flash Sale")
                .build();
    }

    @Override
    public RestApiResponse<Page<FlashSale>> getAllFlashSale(int page, int size) {
        Page<FlashSale> pageData = flashSaleRepository.findAllByFsIsDeleteFalse(PageRequest.of(page, size));
        return RestApiResponse.<Page<FlashSale>>builder()
                .code("200")
                .message("Flash Sale retrieved successfully")
                .data(pageData)
                .build();
    }

    public List<FlashSale> getFsCode(String fsCode) {
        return flashSaleRepository.findByFsCode(fsCode);
    }
}
