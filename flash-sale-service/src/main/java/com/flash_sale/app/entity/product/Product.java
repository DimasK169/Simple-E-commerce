package com.flash_sale.app.entity.product;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
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
    @NotEmpty
    private String productName;
    @Column(unique = true, nullable = false)
    private String productCode;
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
