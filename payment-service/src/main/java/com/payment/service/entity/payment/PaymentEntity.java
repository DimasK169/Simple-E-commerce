package com.payment.service.entity.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("Payment_Id")
    @NotEmpty
    private Long paymentId;

    @JsonProperty("Cart_Id")
    @NotEmpty
    private Long cartId;

    @JsonProperty("Payment_Number")
    @NotEmpty
    private String paymentNumber;

    @JsonProperty("Payment_Type")
    @NotEmpty
    private String paymentType;

    @JsonProperty("Payment_Price")
    @NotEmpty
    private String paymentPrice;

    @JsonProperty("Payment_Status")
    @NotEmpty
    private String paymentStatus;

    @JsonProperty("Payment_Start_Date")
    @NotEmpty
    private Date paymentStartDate;

    @JsonProperty("Payment_End_Date")
    @NotEmpty
    private Date paymentEndDate;

    @JsonProperty("Payment_Third_Party")
    @NotEmpty
    @Column(columnDefinition = "TEXT")
    private String paymentThirdParty;

    @JsonProperty("Payment_Created_Date")
    @NotEmpty
    private Date paymentCreatedDate;

    @JsonProperty("Payment_Updated_Date")
    @NotEmpty
    private Date paymentUpdatedDate;

}
