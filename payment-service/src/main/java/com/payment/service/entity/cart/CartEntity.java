package com.payment.service.entity.cart;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class CartEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("Cart_Id")
    private Long cartId;
    private Long productId;
    private Long userId;
    private String userEmail;
    private String productCode;
    private String productName;
    private Integer cartQuantity;
    private Integer cartTotalPrice;
    private String paymentNumber;
    private Boolean isDeleted;
    private Boolean isPayed;
    private Boolean isFailed;
    private Date cartCreatedDate;
    private Date cartUpdatedDate;

}
