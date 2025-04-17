package com.payment.service.dto.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentProductSaved {

    @JsonProperty("Product_Name")
    private String productName;

    @JsonProperty("Product_Quantity")
    private String productQuantity;

    @JsonProperty("Cart_Total_Price_Per_Item")
    private String cartTotalPricePerItem;
}
