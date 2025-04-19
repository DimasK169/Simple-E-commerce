package com.product.app.service.implementation;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.product.app.dto.request.AuditTrailsRequest;
import com.product.app.dto.result.AuditTrailsResponse;
import com.product.app.entity.product.AuditTrails;
import com.product.app.repository.product.AuditTrailsRepository;
import com.product.app.service.interfacing.AuditTrailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuditTrailsServiceImp implements AuditTrailsService {

    @Autowired
    private AuditTrailsRepository auditTrailsRepository;

    @Override
    public AuditTrailsResponse saveAuditTrails(AuditTrailsRequest request) throws JsonProcessingException {

        AuditTrails auditTrails = AuditTrails.builder()
                .AtAction(request.getAtAction())
                .AtDescription(request.getAtDescription())
                .AtRequest(request.getAtRequest())
                .AtResponse(request.getAtResponse())
                .AtDate(new Date())
                .build();
        AuditTrails save = auditTrailsRepository.save(auditTrails);

        AuditTrailsResponse response = new AuditTrailsResponse();
        response.setAtAction(auditTrails.getAtAction());
        response.setAtDescription(auditTrails.getAtDescription());
        response.setAtRequest(auditTrails.getAtRequest());
        response.setAtResponse(auditTrails.getAtResponse());
        response.setAtDate(auditTrails.getAtDate());

        return response;
    }
}
