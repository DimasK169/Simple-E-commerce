package com.payment.service.dto.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class PaymentSaveResult<T> {

    @JsonProperty("User_Email")
    private String userEmail;

    @JsonProperty("Payment_Number")
    private String paymentNumber;

    @JsonProperty("Product_Name")
    private String productName;

    @JsonProperty("Product_Quantity")
    private Integer productQuantity;

    @JsonProperty("Payment_Type")
    private String paymentType;

    @JsonProperty("Cart_Total_Price_Per_Item")
    private Integer cartTotalPricePerItem;

    @JsonProperty("Cart_Total_Price")
    private Integer cartTotalPrice;

    @JsonProperty("Payment_Status")
    private String paymentStatus;

    @JsonProperty("Payment_Start_Date")
    private Date paymentStartDate;

    @JsonProperty("Payment_End_Date")
    private Date paymentEndDate;

    @JsonProperty("Payment_Third_Party")
    private T paymentThirdParty;

    @JsonProperty("Payment_Created_Date")
    private Date paymentCreatedDate;

}
