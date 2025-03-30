package com.payment.service.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class AuditTrailsRequest {

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

    @JsonProperty("Audit_Trails_Third_Party_Response")
    private String auditTrailsThirdPartyResponse;

}
