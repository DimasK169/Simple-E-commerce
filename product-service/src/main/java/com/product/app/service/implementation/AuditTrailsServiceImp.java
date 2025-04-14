package com.product.app.service.implementation;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.product.app.dto.request.AuditTrailsRequest;
import com.product.app.dto.result.AuditTrailsResponse;
import com.product.app.entity.AuditTrails;
import com.product.app.repository.AuditTrailsRepository;
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

        AuditTrails trails = new AuditTrails();
        trails.setAtAction(request.getAtAction());
        trails.setAtDescription(request.getAtDescription());
        trails.setAtRequest(request.getAtRequest());
        trails.setAtResponse(request.getAtResponse());
        trails.setAtDate(new Date());
        AuditTrails save = auditTrailsRepository.save(trails);

        AuditTrailsResponse response = new AuditTrailsResponse();
        response.setAtAction(trails.getAtAction());
        response.setAtDescription(trails.getAtDescription());
        response.setAtRequest(trails.getAtRequest());
        response.setAtResponse(trails.getAtResponse());
        response.setAtDate(trails.getAtDate());

        return response;
    }
}
