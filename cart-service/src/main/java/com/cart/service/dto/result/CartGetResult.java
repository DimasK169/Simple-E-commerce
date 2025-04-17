package com.cart.service.dto.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class CartGetResult {

    @JsonProperty("Product_Name")
    private String productName;

    @JsonProperty("Product_Desc")
    private String productDesc;

    @JsonProperty("Product_Price")
    private Integer productPrice;

    @JsonProperty("Cart_Total_Price_Per_Item")
    private Integer cartTotalPricePerItem;

    @JsonProperty("Cart_Total_Price")
    private Integer cartTotalPrice;

    @JsonProperty("Cart_Quantity")
    private Integer cartQuantity;

    @JsonProperty("Product_Image")
    private String productImage;

    @JsonProperty("Created_Date")
    private Date createdDate;
}
