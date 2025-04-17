package com.cart.service.entity.cart;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
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
    private Long trxFlashSaleId;
    private String userEmail;
    private String productCode;
    private String productName;
    private Integer cartQuantity;
    private Integer cartTotalPrice;
    private String paymentNumber;
    private String fsCode;
    private Boolean isPayed;
    private Boolean isFailed;
    private Date cartCreatedDate;
    private Date cartUpdatedDate;

}
