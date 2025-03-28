package com.product.app.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "Product")
@Data
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;
    private String productName;
    private String productDescription;
    private String productCategory;
    private String productStock;
    private String productPrice;
    private Boolean productIsAvailable;
    private Boolean productIsDelete;
    private String createdBy;
    private Date createdDate;
    private Date updatedDate;
}
