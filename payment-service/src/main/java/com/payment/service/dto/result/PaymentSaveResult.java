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
public class PaymentSaveResult {

    @JsonProperty("Cart_Id")
    private Long cartId;

    @JsonProperty("Payment_Number")
    private String paymentNumber;

    @JsonProperty("Payment_Type")
    private String paymentType;

    @JsonProperty("Payment_Price")
    private String paymentPrice;

    @JsonProperty("Payment_Status")
    private String paymentStatus;

    @JsonProperty("Payment_Start_Date")
    private Date paymentStartDate;

    @JsonProperty("Payment_End_Date")
    private Date paymentEndDate;

    @JsonProperty("Payment_Third_Party")
    private String paymentThirdParty;

    @JsonProperty("Payment_Created_Date")
    private Date paymentCreatedDate;

}
