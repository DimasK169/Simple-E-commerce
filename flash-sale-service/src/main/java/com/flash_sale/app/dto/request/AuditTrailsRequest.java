package com.flash_sale.app.dto.request;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class AuditTrailsRequest {

    private String AtAction;
    private Date AtDate;
    private String AtDescription;
    private String AtRequest;
    private String AtResponse;
}
