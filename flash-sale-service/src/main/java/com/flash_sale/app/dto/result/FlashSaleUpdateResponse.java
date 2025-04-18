package com.flash_sale.app.dto.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class FlashSaleUpdateResponse {

    @JsonProperty("FlashSale_Name")
    private String fsName;

    @JsonProperty("FlashSale_Code")
    private String fsCode;

    @JsonProperty("FlashSale_Product")
    private String fsProduct;

    @JsonProperty("FlashSale_Discount")
    private Double trxDiscount;

    @JsonProperty("Product_Price")
    private Integer productPrice;

    @JsonProperty("FlashSale_Price")
    private Integer trxPrice;

    @JsonProperty("Product_Image")
    private String productImage;

    @JsonProperty("FlashSale_StartDate")
    private Date fsStartDate;

    @JsonProperty("FlashSale_EndDate")
    private Date fsEndDate;

    @JsonProperty("FlashSale_CreatedBy")
    private String fsCreatedBy;

    @JsonProperty("FlashSale_UpdateDate")
    private Date fsUpdateDate;

    @JsonProperty("FlashSale_IsDelete")
    private Boolean fsIsDelete;

    @JsonProperty("Status")
    private String status;

}
