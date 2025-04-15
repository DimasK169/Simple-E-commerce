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
    private Long paymentId;
    private Long userId;
    private String userEmail;
    private String paymentNumber;
    private String paymentType;
    private Integer paymentPrice;
    private String paymentStatus;
    private Date paymentStartDate;
    private Date paymentEndDate;
    @Column(columnDefinition = "TEXT")
    private String paymentThirdParty;
    private Date paymentCreatedDate;
    private Date paymentUpdatedDate;

}
