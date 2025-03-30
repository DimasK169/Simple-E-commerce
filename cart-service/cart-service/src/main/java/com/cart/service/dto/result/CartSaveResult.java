package com.cart.service.dto.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
public class CartSaveResult {

    @JsonProperty("Cart_Id")
    @NotEmpty
    private Long cartId;

    @JsonProperty("Cart_Number")
    @NotEmpty
    private String cartNumber;

    @JsonProperty("Cart_Quantity")
    @NotEmpty
    private Long cartQuantity;

    @JsonProperty("Cart_Total_Price")
    @NotEmpty
    private Long cartTotalPrice;

    @JsonProperty("Cart_Created_Date")
    @NotEmpty
    private Date cartCreatedDate;

    @JsonProperty("Cart_Updated_Date")
    @NotEmpty
    private Date cartUpdatedDate;

}
