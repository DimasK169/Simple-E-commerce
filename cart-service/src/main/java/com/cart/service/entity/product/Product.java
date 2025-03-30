package com.cart.service.entity.product;

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
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotEmpty
    private Long productId;

    @NotEmpty
    private String productName;

    @NotEmpty
    private String productDescription;

    @NotEmpty
    private String productCategory;

    @NotEmpty
    private String productStock;

    @NotEmpty
    private String productPrice;

    @NotEmpty
    private Boolean productIsAvailable;

    @NotEmpty
    private Boolean productIsDelete;

    @NotEmpty
    private String createdBy;

    @NotEmpty
    private Date createdDate;

    @NotEmpty
    private Date updatedDate;

}
