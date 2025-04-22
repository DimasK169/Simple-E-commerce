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
import com.pusher.rest.Pusher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class FlashSaleServiceImpl implements FlashSaleService {

    String baseUrl = "http://localhost:8080";

    @Autowired
    private FlashSaleRepository flashSaleRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TrxFlashSaleRepository trxFlashSaleRepository;

    @Autowired
    private AuditTrailsServiceImpl auditTrailsService;

    @Autowired
    private Pusher pusher;


    @Transactional
    @Override
    public RestApiResponse<List<FlashSaleSaveResponse>> createFlashSale(FlashSaleRequest request) throws JsonProcessingException {
        List<FlashSaleSaveResponse> responseList = new ArrayList<>();

        List<FlashSale> fetchFlashSale = flashSaleRepository.findByFsCode(request.getFsCode());
        if (fetchFlashSale.isEmpty()){
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
                            .productCode(saveProduct.getProductCode())
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
            pusher.trigger("flash-sale","create", responseList);
            return RestApiResponse.<List<FlashSaleSaveResponse>>builder()
                    .code(HttpStatus.OK.toString())
                    .message("Berhasil Membuat Flash-sale")
                    .data(responseList)
                    .build();


        } else if (request.getFsCode().equals(fetchFlashSale.get(0).getFsCode())){
            throw new IllegalArgumentException("Flash sale code has been used");
        }
        return  null;
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
                        .productCode(saveProduct.getProductCode())
                        .trxDiscount(request.getTrxDiscount())
                        .trxPrice((int)(saveProduct.getProductPrice()* (1 - request.getTrxDiscount())))
                        .build();

                trxFlashSaleRepository.save(trxFlashSale);

                String status = isInsert ? "Inserted" : "Updated";

                FlashSaleUpdateResponse response = FlashSaleUpdateResponse.builder()
                        .fsName(updatedFlashSale.getFsName())
                        .fsCode(updatedFlashSale.getFsCode())
                        .fsProduct(updatedFlashSale.getFsProduct())
                        .productCode(saveProduct.getProductCode())
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
    public RestApiResponse<List<FlashSaleUpdateResponse>> getByFlashSaleCode(String fsCode) throws JsonProcessingException {
        List<FlashSale> fsList = getFsCode(fsCode);
        if (fsList == null || fsList.isEmpty()) {
            throw new IllegalArgumentException("Invalid Flash-Sale Code");
        }


        List<FlashSaleUpdateResponse> responseList = new ArrayList<>();

        for (FlashSale flashSale : fsList) {
            List<TrxFlashSale> trxList = trxFlashSaleRepository.findByFsId(flashSale.getFsId());

            for (TrxFlashSale trx : trxList) {
                List<Product> products = productRepository.findByproductCode(trx.getProductCode());
                for (Product getProduct : products){
                    if (getProduct.equals("") || getProduct.equals(null)) continue;

                    FlashSaleUpdateResponse response = FlashSaleUpdateResponse.builder()
                            .fsName(flashSale.getFsName())
                            .fsCode(flashSale.getFsCode())
                            .fsProduct(flashSale.getFsProduct())
                            .productCode(trx.getProductCode())
                            .fsStartDate(flashSale.getFsStartDate())
                            .fsEndDate(flashSale.getFsEndDate())
                            .fsCreatedBy(flashSale.getFsCreatedBy())
                            .fsUpdateDate(flashSale.getFsUpdateDate())
                            .fsIsDelete(flashSale.getFsIsDelete())
                            .trxDiscount(trx.getTrxDiscount())
                            .trxPrice(trx.getTrxPrice())
                            .productPrice(getProduct.getProductPrice())
                            .productImage(baseUrl + "/images/" + getProduct.getProductImage())
                            .status("Fetched")
                            .build();

                    responseList.add(response);

                }

                auditTrailsService.saveAuditTrails(AuditTrailsRequest.builder()
                        .AtAction("Get")
                        .AtDescription("Get Flash Sale By Code")
                        .AtDate(new Date())
                        .AtRequest(fsCode)
                        .AtResponse("Berhasil ambil flash sale: " + flashSale.getFsProduct())
                        .build());
            }
        }

        return RestApiResponse.<List<FlashSaleUpdateResponse>>builder()
                .code(HttpStatus.OK.toString())
                .message("Berhasil menampilkan data flash sale")
                .data(responseList)
                .build();

    }

    @Override
    public RestApiResponse<Page<FlashSaleUpdateResponse>> getAllFlashSale(int page, int size) {
        Page<FlashSale> pageData = flashSaleRepository.findAllByFsIsDeleteFalse(PageRequest.of(page, size));

        List<FlashSaleUpdateResponse> responseList = new ArrayList<>();

        for (FlashSale flashSale : pageData.getContent()) {
            List<TrxFlashSale> trxList = trxFlashSaleRepository.findByFsId(flashSale.getFsId());

            for (TrxFlashSale trx : trxList) {
                Optional<Product> productOpt = productRepository.findById(trx.getProductId());
                if (productOpt.isEmpty()) continue;

                Product product = productOpt.get();

                FlashSaleUpdateResponse response = FlashSaleUpdateResponse.builder()
                        .fsName(flashSale.getFsName())
                        .fsCode(flashSale.getFsCode())
                        .fsProduct(flashSale.getFsProduct())
                        .fsStartDate(flashSale.getFsStartDate())
                        .fsEndDate(flashSale.getFsEndDate())
                        .fsCreatedBy(flashSale.getFsCreatedBy())
                        .fsUpdateDate(flashSale.getFsUpdateDate())
                        .fsIsDelete(flashSale.getFsIsDelete())
                        .trxDiscount(trx.getTrxDiscount())
                        .trxPrice(trx.getTrxPrice())
                        .productPrice(product.getProductPrice())
                        .productCode(product.getProductCode())
                        .productImage(baseUrl + "/images/" + product.getProductImage())
                        .status("Fetched")
                        .build();

                responseList.add(response);
            }
        }

        // Construct a new Page object with the transformed list
        Page<FlashSaleUpdateResponse> responsePage = new PageImpl<>(
                responseList,
                PageRequest.of(page, size),
                pageData.getTotalElements()
        );

        return RestApiResponse.<Page<FlashSaleUpdateResponse>>builder()
                .code("200")
                .message("Berhasil menampilkan semua data flash sale")
                .data(responsePage)
                .build();
    }

    public List<FlashSale> getFsCode(String fsCode) {
        return flashSaleRepository.findByFsCode(fsCode);
    }


    public RestApiResponse<Map<String, String>> getActiveFlashSaleCodeResponse() {
        Optional<String> fsCode = flashSaleRepository.findActiveFlashSaleCode();
        if (fsCode.isPresent()) {
            return RestApiResponse.<Map<String, String>>builder()
                    .code("200 OK")
                    .message("Flash sale aktif ditemukan")
                    .data(Map.of("FlashSale_Code", fsCode.get()))
                    .build();
        } else {
            return RestApiResponse.<Map<String, String>>builder()
                    .code("404 NOT_FOUND")
                    .message("Tidak ada flash sale yang aktif saat ini")
                    .error(List.of("Flash sale tidak ditemukan"))
                    .build();
        }
    }

}
