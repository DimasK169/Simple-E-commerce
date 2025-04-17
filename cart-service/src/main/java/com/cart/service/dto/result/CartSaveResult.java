package com.cart.service.dto.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
public class CartSaveResult {

    @JsonProperty("User_Email")
    private String userEmail;

    @JsonProperty("Product_Name")
    private String productName;

    @JsonProperty("Fs_Code")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String fsCode;

    @JsonProperty("Cart_Quantity")
    private Integer cartQuantity;

    @JsonProperty("Cart_Total_Price")
    private Integer cartTotalPrice;

    @JsonProperty("Cart_Payed")
    private Boolean isPayed;

    @JsonProperty("Cart_Failed")
    private Boolean isFailed;

    @JsonProperty("Cart_Created_Date")
    private Date cartCreatedDate;

    @JsonProperty("Cart_Updated_Date")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Date cartUpdatedDate;


}
