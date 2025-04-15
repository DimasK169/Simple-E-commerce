package com.cart.service.services.implementation;

import com.cart.service.dto.request.AuditTrailsRequest;
import com.cart.service.dto.result.AuditTrailsSaveResult;
import com.cart.service.entity.cart.AuditTrails;
import com.cart.service.repository.cart.AuditTrailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cart.service.services.services.AuditTrailsService;

@Service
public class AuditTrailsServiceImplementation implements AuditTrailsService {

    @Autowired
    AuditTrailsRepository auditTrailsRepository;

    @Override
    public AuditTrailsSaveResult insertAuditTrails(AuditTrailsRequest auditTrailsRequest) {
        AuditTrails auditTrails = AuditTrails.builder()
                .auditTrailsAction(auditTrailsRequest.getAuditTrailsAction())
                .auditTrailsDate(auditTrailsRequest.getAuditTrailsDate())
                .auditTrailsDescription(auditTrailsRequest.getAuditTrailsDescription())
                .auditTrailsRequest(auditTrailsRequest.getAuditTrailsRequest())
                .auditTrailsResponse(auditTrailsRequest.getAuditTrailsResponse())
                .build();
        AuditTrails saved = auditTrailsRepository.save(auditTrails);

        AuditTrailsSaveResult auditTrailsSaveResult = AuditTrailsSaveResult.builder()
                .auditTrailsAction(auditTrails.getAuditTrailsAction())
                .auditTrailsDate(auditTrails.getAuditTrailsDate())
                .auditTrailsDescription(auditTrails.getAuditTrailsDescription())
                .auditTrailsRequest(auditTrails.getAuditTrailsRequest())
                .auditTrailsResponse(auditTrails.getAuditTrailsResponse())
                .build();

        return auditTrailsSaveResult;
    }
}
