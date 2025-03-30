package com.cart.service.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Data
@Builder
public class AuditTrailsRequest {

    @JsonProperty("Audit_Trails_Action")
    @NotEmpty
    private String auditTrailsAction;

    @JsonProperty("Audit_Trails_Date")
    @NotEmpty
    private Date auditTrailsDate;

    @JsonProperty("Audit_Trails_Description")
    @NotEmpty
    private String auditTrailsDescription;

    @JsonProperty("Audit_Trails_Request")
    @NotEmpty
    private String auditTrailsRequest;

    @JsonProperty("Audit_Trails_Response")
    @NotEmpty
    private String auditTrailsResponse;

}
