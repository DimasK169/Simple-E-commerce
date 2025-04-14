package com.flash_sale.app.entity.flash_sale;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Double trxDiscount;
    private Integer trxPrice;

}
