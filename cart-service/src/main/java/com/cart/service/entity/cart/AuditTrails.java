package com.cart.service.entity.cart;

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

    @Column(columnDefinition = "TEXT")
    private String auditTrailsAction;

    private Date auditTrailsDate;

    @Column(columnDefinition = "TEXT")
    private String auditTrailsDescription;

    @Column(columnDefinition = "TEXT")
    private String auditTrailsRequest;

    @Column(columnDefinition = "TEXT")
    private String auditTrailsResponse;

}
