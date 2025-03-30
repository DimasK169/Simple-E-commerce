package com.payment.service.services.implementation;

import com.payment.service.dto.request.AuditTrailsRequest;
import com.payment.service.dto.result.AuditTrailsSaveResult;
import com.payment.service.entity.payment.AuditTrails;
import com.payment.service.repository.payment.AuditTrailsRepository;
import com.payment.service.services.services.AuditTrailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditTrailsServiceImplementation implements AuditTrailsService {

    @Autowired
    private AuditTrailsRepository auditTrailsRepository;

    @Override
    public AuditTrailsSaveResult insertAuditTrails(AuditTrailsRequest auditTrailsRequest){
        AuditTrails auditTrails = AuditTrails.builder()
                .auditTrailsAction(auditTrailsRequest.getAuditTrailsAction())
                .auditTrailsDate(auditTrailsRequest.getAuditTrailsDate())
                .auditTrailsDescription(auditTrailsRequest.getAuditTrailsDescription())
                .auditTrailsRequest(auditTrailsRequest.getAuditTrailsRequest())
                .auditTrailsResponse(auditTrailsRequest.getAuditTrailsResponse())
                .auditTrailsThirdPartyResponse(auditTrailsRequest.getAuditTrailsThirdPartyResponse())
                .build();
        AuditTrails createAudit = auditTrailsRepository.save(auditTrails);

        AuditTrailsSaveResult auditTrailsSaveResult = AuditTrailsSaveResult.builder()
                .auditTrailsAction(createAudit.getAuditTrailsAction())
                .auditTrailsDate(createAudit.getAuditTrailsDate())
                .auditTrailsDescription(createAudit.getAuditTrailsDescription())
                .auditTrailsRequest(createAudit.getAuditTrailsRequest())
                .auditTrailsResponse(createAudit.getAuditTrailsResponse())
                .auditTrailsThirdPartyResponse(createAudit.getAuditTrailsThirdPartyResponse())
                .build();

        return auditTrailsSaveResult;
    }



}
