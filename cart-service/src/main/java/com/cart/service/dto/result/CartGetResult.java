package com.cart.service.dto.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class CartGetResult {

    @JsonProperty("Product_Name")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String productName;

    @JsonProperty("Product_Desc")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String productDesc;

    @JsonProperty("Product_Price")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Integer productPrice;

    @JsonProperty("Product_Code")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String productCode;

    @JsonProperty("Fs_Code")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String fsCode;

    @JsonProperty("Cart_Total_Price_Per_Item")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Integer cartTotalPricePerItem;

    @JsonProperty("Cart_Total_Price")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Integer cartTotalPrice;

    @JsonProperty("Cart_Quantity")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Integer cartQuantity;

    @JsonProperty("Product_Quantity")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Integer productStock;

    @JsonProperty("Product_Image")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String productImage;

    @JsonProperty("Created_Date")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Date createdDate;
}
