package com.product.app.entity.flashsale;

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
public class TrxFlashSale {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long trxFlashSaleId;
    private Long productId;
    private Long fsId;
    private String fsCode;
    private String productCode;
    private Double trxDiscount;
    private Integer trxPrice;

}
