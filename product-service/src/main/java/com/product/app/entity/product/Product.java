package com.product.app.entity.product;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "Product")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;
    @Column(unique = true, nullable = false)
    private String productCode;
    private String productName;
    private String productImage;
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
