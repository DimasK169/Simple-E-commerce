package com.payment.service.services.services;

import com.payment.service.dto.request.AuditTrailsRequest;
import com.payment.service.dto.result.AuditTrailsSaveResult;
import org.springframework.stereotype.Service;

@Service
public interface AuditTrailsService {

    public AuditTrailsSaveResult insertAuditTrails(AuditTrailsRequest auditTrailsRequest);

}
