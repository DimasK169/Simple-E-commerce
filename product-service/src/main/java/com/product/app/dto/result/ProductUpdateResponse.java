package com.product.app.dto.result;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ProductUpdateResponse {
    private String productName;
    private String productDescription;
    private String productCategory;
    private String productStock;
    private String productPrice;
    private Boolean productIsAvailable;
    private String createdBy;
    private Date createdDate;
    private Date updatedDate;
    private Boolean productIsDelete;
}
