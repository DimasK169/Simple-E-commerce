package com.user.app.service.implement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.user.app.dto.request.AuditTrailsRequest;
import com.user.app.dto.result.AuditTrailsResponse;
import com.user.app.entity.AuditTrails;
import com.user.app.repository.AuditTrailsRepository;
import com.user.app.service.interfacing.AuditTrailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuditTrailsServiceImpl implements AuditTrailsService {

    @Autowired
    private AuditTrailsRepository auditTrailsRepository;

    @Override
    public AuditTrailsResponse saveAuditTrails(AuditTrailsRequest request) {

        //menyimpan data ke DB
        AuditTrails trails = new AuditTrails();
        trails.setAtAction(request.getAtAction());
        trails.setAtDescription(request.getAtDescription());
        trails.setAtRequest(request.getAtRequest());
        trails.setAtResponse(request.getAtResponse());
        trails.setAtDate(new Date());
        AuditTrails save = auditTrailsRepository.save(trails);

        //manampilkan data dari DB
        AuditTrailsResponse response = new AuditTrailsResponse();
        response.setAtAction(trails.getAtAction());
        response.setAtDescription(trails.getAtDescription());
        response.setAtRequest(trails.getAtRequest());
        response.setAtResponse(trails.getAtResponse());
        response.setAtDate(trails.getAtDate());

        return response;
    }
}
