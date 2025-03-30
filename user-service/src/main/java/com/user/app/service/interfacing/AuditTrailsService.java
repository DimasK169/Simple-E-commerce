package com.user.app.service.interfacing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.user.app.dto.request.AuditTrailsRequest;
import com.user.app.dto.result.AuditTrailsResponse;

public interface AuditTrailsService {

    AuditTrailsResponse saveAuditTrails(AuditTrailsRequest request) throws JsonProcessingException;
}
