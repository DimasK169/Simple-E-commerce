package com.flash_sale.app.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class FlashSaleRequest {

    @JsonProperty("FlashSale_Name")
    @NotEmpty
    private String FsName;

    @JsonProperty("FlashSale_Product")
    @NotEmpty
    private String FsProduct;

    @JsonProperty("FlashSale_StartDate")
    @NotEmpty
    private Date FsStartDate;

    @JsonProperty("FlashSale_EndDate")
    @NotEmpty
    private Date FsEndDate;

    @JsonProperty("FlashSale_Discount")
    @NotEmpty
    private Double FsDiscount;

    @JsonProperty("FlashSale_DiscountedPrice")
    @NotEmpty
    private Integer FsDiscountedPrice;

    @JsonProperty("FlashSale_Status")
    @NotEmpty
    private String FsStatus;

    @JsonProperty("FlashSale_CreatedBy")
    @NotEmpty
    private String FsCreatedBy;

}

