package com.product.app.dto.result;

import lombok.Data;

import java.util.Date;

@Data
public class AuditTrailsResponse {

    private Long AtId;
    private String AtAction;
    private Date AtDate;
    private String AtDescription;
    private String AtRequest;
    private String AtResponse;
}