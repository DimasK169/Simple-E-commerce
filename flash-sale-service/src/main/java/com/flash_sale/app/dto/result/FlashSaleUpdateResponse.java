package com.flash_sale.app.dto.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class FlashSaleUpdateResponse {

    @JsonProperty("FlashSale_Id")
    private Long FsId;
    @JsonProperty("FlashSale_Name")
    private String FsName;
    @JsonProperty("FlashSale_Product")
    private String FsProduct;
    @JsonProperty("FlashSale_StartDate")
    private Date FsStartDate;
    @JsonProperty("FlashSale_EndDate")
    private Date FsEndDate;
    @JsonProperty("FlashSale_Discount")
    private Double FsDiscount;
    @JsonProperty("FlashSale_DiscountedPrice")
    private Integer FsDiscountedPrice;
    @JsonProperty("FlashSale_Status")
    private String FsStatus;
    @JsonProperty("FlashSale_CreatedBy")
    private String FsCreatedBy;
    @JsonProperty("FlashSale_UpdateDate")
    private Date FsUpdateDate;
    @JsonProperty("FlashSale_IsDelete")
    private Boolean FsIsDelete;
}
