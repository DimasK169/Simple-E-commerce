package com.cart.service.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartRequest {

    @JsonProperty("Cart_Number")
    @NotEmpty
    private String cartNumber;

    @JsonProperty("Cart_Quantity")
    @NotEmpty
    private Long cartQuantity;

    @JsonProperty("Cart_Total_Price")
    @NotEmpty
    private Long cartTotalPrice;

}
