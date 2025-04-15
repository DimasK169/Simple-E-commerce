package com.flash_sale.app.service.Interface;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.flash_sale.app.dto.request.AuditTrailsRequest;
import com.flash_sale.app.dto.result.AuditTrailsResponse;

public interface AuditTrailsService {

    AuditTrailsResponse saveAuditTrails(AuditTrailsRequest request) throws JsonProcessingException;
}
