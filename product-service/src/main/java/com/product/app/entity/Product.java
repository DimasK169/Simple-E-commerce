package com.product.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
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
    @Column(unique = true, nullable = false)
    private String productCode;
    private String productName;
    private String productImage = "https://www.mon-site-bug.fr/uploads/products/default-product.png";
    private String productDescription;
    private String productCategory;
    private Integer productStock;
    private Integer productPrice;
    private Boolean productIsAvailable;
    private Boolean productIsDelete;
    private String createdBy;
    private Date createdDate;
    private Date updatedDate;
}
