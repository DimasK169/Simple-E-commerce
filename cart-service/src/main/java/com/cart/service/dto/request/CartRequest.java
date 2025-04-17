package com.cart.service.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartRequest {

    @JsonProperty("Product_Code")
    private String productCode;

    @JsonProperty("User_Email")
    private String userEmail;

    @JsonProperty("Cart_Quantity")
    @Min(value = 1, message = "Quantity harus lebih dari 0")
    private Integer cartQuantity;

    @JsonProperty("Fs_Code")
    private String fsCode;

}
