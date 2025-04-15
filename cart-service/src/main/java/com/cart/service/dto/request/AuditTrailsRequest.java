package com.cart.service.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Data
@Builder
public class AuditTrailsRequest {

    @JsonProperty("Audit_Trails_Action")
    private String auditTrailsAction;

    @JsonProperty("Audit_Trails_Date")
    @NotNull(message = "Tanggal dibuat tidak boleh kosong")
    private Date auditTrailsDate;

    @JsonProperty("Audit_Trails_Description")
    private String auditTrailsDescription;

    @JsonProperty("Audit_Trails_Request")
    private String auditTrailsRequest;

    @JsonProperty("Audit_Trails_Response")
    private String auditTrailsResponse;

}
