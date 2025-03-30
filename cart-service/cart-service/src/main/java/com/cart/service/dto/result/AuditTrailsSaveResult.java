package com.cart.service.dto.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
public class AuditTrailsSaveResult {

    @JsonProperty("Audit_Trails_Action")
    private String auditTrailsAction;

    @JsonProperty("Audit_Trails_Date")
    private Date auditTrailsDate;

    @JsonProperty("Audit_Trails_Description")
    private String auditTrailsDescription;

    @JsonProperty("Audit_Trails_Request")
    private String auditTrailsRequest;

    @JsonProperty("Audit_Trails_Response")
    private String auditTrailsResponse;

}
