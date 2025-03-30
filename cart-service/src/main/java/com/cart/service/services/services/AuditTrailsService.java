package com.cart.service.services.services;

import com.cart.service.dto.request.AuditTrailsRequest;
import com.cart.service.dto.result.AuditTrailsSaveResult;
import org.springframework.stereotype.Service;

@Service
public interface AuditTrailsService {

    AuditTrailsSaveResult insertAuditTrails(AuditTrailsRequest auditTrailsRequest);

}
