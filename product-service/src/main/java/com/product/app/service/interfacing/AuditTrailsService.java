package com.product.app.service.interfacing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.product.app.dto.request.AuditTrailsRequest;
import com.product.app.dto.result.AuditTrailsResponse;

public interface AuditTrailsService {
    AuditTrailsResponse saveAuditTrails(AuditTrailsRequest request) throws JsonProcessingException;
}
