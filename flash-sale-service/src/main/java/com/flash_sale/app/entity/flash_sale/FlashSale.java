package com.flash_sale.app.entity.flash_sale;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class FlashSale {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private Long fsId;
    private String fsName;
    private String fsCode;
    private String fsProduct;
    private Date fsStartDate;
    private Date fsEndDate;
    private String fsCreatedBy;
    private Date fsCreatedDate;
    private Date fsUpdateDate;
    private Boolean fsIsDelete;
}
