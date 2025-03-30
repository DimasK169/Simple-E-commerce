package com.payment.service.entity.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuditTrails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("Audit_Trails_Id")
    private Long auditTrailsId;

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
    @Column(columnDefinition = "TEXT")
    private String auditTrailsRequest;

    @JsonProperty("Audit_Trails_Response")
    @NotEmpty
    @Column(columnDefinition = "TEXT")
    private String auditTrailsResponse;

    @JsonProperty("Audit_Trails_Third_Party_Response")
    @NotEmpty
    @Column(columnDefinition = "TEXT")
    private String auditTrailsThirdPartyResponse;

}
