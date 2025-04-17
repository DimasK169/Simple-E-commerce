package com.cart.service.dto.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class CartUpdateResult {

    @JsonProperty("Product_Id")
    private Long productId;

    @JsonProperty("User_Id")
    private Long userId;

    @JsonProperty("User_Email")
    private String userEmail;

    @JsonProperty("Product_Name")
    private String productName;

    @JsonProperty("Cart_Quantity")
    private Integer cartQuantity;

    @JsonProperty("Cart_Total_Price")
    private Integer cartTotalPrice;

    @JsonProperty("Cart_Updated_Date")
    private Date cartUpdatedDate;

}
