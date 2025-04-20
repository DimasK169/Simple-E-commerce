package com.flash_sale.app.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;

@Data
@Builder
public class FlashSaleRequest {

    @JsonProperty("Product_Code")
    @NotEmpty(message = "Product code cannot be empty")
    private List<String> productCode;

    @JsonProperty("FlashSale_Name")
    @NotEmpty(message = "Flash sale name cannot be empty")
    private String fsName;

    @JsonProperty("FlashSale_Code")
    @NotEmpty(message = "Flash sale code cannot be empty")
    private String fsCode;

    @JsonProperty("FlashSale_StartDate")
    @NotNull(message = "Flash sale start date cannot be empty")
    private LocalDateTime fsStartDate;

    @JsonProperty("FlashSale_EndDate")
    @NotNull(message = "Flash sale end date cannot be empty")
    private LocalDateTime   fsEndDate;

    @JsonProperty("FlashSale_CreatedBy")
    private String fsCreatedBy;

    @JsonProperty("FlashSale_Discount")
    @NotNull(message = "Discount cannot be empty")
    private Double trxDiscount;

}

