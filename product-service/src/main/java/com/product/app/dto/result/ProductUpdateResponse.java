package com.product.app.dto.result;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ProductUpdateResponse {
    private String productName;
    private String productCode;
    private String productImage;
    private String productDescription;
    private String productCategory;
    private Integer productStock;
    private Integer productPrice;
    private Boolean productIsAvailable;
    private String createdBy;
    private Date createdDate;
    private Date updatedDate;
    private Boolean productIsDelete;
}
