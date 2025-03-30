package com.cart.service.entity;

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
public class CartEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("Cart_Id")
    private Long cartId;

    @JsonProperty("Cart_Number")
    @NotEmpty
    private String cartNumber;

    @JsonProperty("Cart_Quantity")
    @NotEmpty
    private Long cartQuantity;

    @JsonProperty("Cart_Total_Price")
    @NotEmpty
    private Long cartTotalPrice;

    @JsonProperty("Cart_Created_Date")
    @NotEmpty
    private Date cartCreatedDate;

    @JsonProperty("Cart_Updated_Date")
    @NotEmpty
    private Date cartUpdatedDate;

}
