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
    @JsonProperty("Cart_Id")
    private Long cartId;

    @NotEmpty
    private Long productId;

    @NotEmpty
    private Long userId;

    @NotEmpty
    private String userEmail;

    @NotEmpty
    private String productName;

    @NotEmpty
    private String cartNumber;

    @NotEmpty
    private Long cartQuantity;

    @NotEmpty
    private Long cartTotalPrice;

    @NotEmpty
    private Date cartCreatedDate;

    @NotEmpty
    private Date cartUpdatedDate;

}
