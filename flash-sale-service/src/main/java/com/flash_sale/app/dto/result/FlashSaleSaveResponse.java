package com.flash_sale.app.dto.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Date;

@Data
@Builder
public class FlashSaleSaveResponse {

    @JsonProperty("FlashSale_Name")
    private String fsName;

    @JsonProperty("FlashSale_Code")
    private String fsCode;

    @JsonProperty("FlashSale_Product")
    private String fsProduct;

    @JsonProperty("FlashSale_StartDate")
    private LocalDateTime fsStartDate;

    @JsonProperty("FlashSale_EndDate")
    private LocalDateTime   fsEndDate;

    @JsonProperty("FlashSale_Discount")
    private Double trxDiscount;

    @JsonProperty("FlashSale_Price")
    private Integer trxPrice;

    @JsonProperty("FlashSale_CreatedBy")
    private String fsCreatedBy;

    @JsonProperty("FlashSale_CreatedDate")
    private Date fsCreatedDate;

    @JsonProperty("FlashSale_IsDelete")
    private Boolean fsIsDelete;

}
