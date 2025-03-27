package com.flash_sale.app.entity;

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
public class FlashSale {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private Long FsId;
    private String FsName;
    private String FsProduct;
    private Date FsStartDate;
    private Date FsEndDate;
    private Double FsDiscount;
    private Integer FsDiscountedPrice;
    private String FsStatus;
    private String FsCreatedBy;
    private Date FsCreatedDate;
    private Date FsUpdateDate;
    private Boolean FsIsDelete;
}
