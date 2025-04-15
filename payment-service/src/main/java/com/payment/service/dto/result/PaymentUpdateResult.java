package com.payment.service.dto.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class PaymentUpdateResult {

    @JsonProperty("Payment_Number")
    private String paymentNumber;

    @JsonProperty("Payment_Type")
    private String paymentType;

    @JsonProperty("Payment_Price")
    private String paymentPrice;

    @JsonProperty("Payment_Status")
    private String paymentStatus;

    @JsonProperty("Payment_Updated_Date")
    private Date paymentUpdatedDate;

}
