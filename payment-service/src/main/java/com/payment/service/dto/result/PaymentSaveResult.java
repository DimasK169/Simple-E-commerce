package com.payment.service.dto.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class PaymentSaveResult<T> {

    @JsonProperty("User_Email")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String userEmail;

    @JsonProperty("Payment_Number")
    private String paymentNumber;

    @JsonProperty("Product_Name")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String productName;

    @JsonProperty("Product_Quantity")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Integer productQuantity;

    @JsonProperty("Payment_Type")
    private String paymentType;

    @JsonProperty("Payment_Price")
    private Integer paymentPrice;

    @JsonProperty("Product")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<PaymentProductSaved> product;

    @JsonProperty("Cart_Total_Price_Per_Item")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Integer cartTotalPricePerItem;

    @JsonProperty("Cart_Total_Price")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Integer cartTotalPrice;

    @JsonProperty("Payment_Status")
    private String paymentStatus;

    @JsonProperty("Payment_Start_Date")
    private Date paymentStartDate;

    @JsonProperty("Payment_End_Date")
    private Date paymentEndDate;

    @JsonProperty("Payment_Third_Party")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private T paymentThirdParty;

    @JsonProperty("Payment_Created_Date")
    private Date paymentCreatedDate;

}
