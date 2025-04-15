package com.cart.service.entity.flashsale;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long fsId;
    private Long productId;
    private String fsName;
    private String fsCode;
    private String productCode;
    private String fsProduct;
    private Date fsStartDate;
    private Date fsEndDate;
    private String fsCreatedBy;
    private Date fsCreatedDate;
    private Date fsUpdateDate;
    private Boolean fsIsDelete;

}
