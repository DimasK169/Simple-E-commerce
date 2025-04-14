package com.flash_sale.app.service.Implement;

import com.flash_sale.app.configuration.ConfigurationBean;
import com.flash_sale.app.dto.request.AuditTrailsRequest;
import com.flash_sale.app.dto.result.AuditTrailsResponse;
import com.flash_sale.app.entity.flash_sale.AuditTrails;
import com.flash_sale.app.repository.flash_sale.AuditTrailsRepository;
import com.flash_sale.app.service.Interface.AuditTrailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuditTrailsServiceImpl implements AuditTrailsService {

    @Autowired
    ConfigurationBean configurationBean;

    @Autowired
    private AuditTrailsRepository auditTrailsRepository;

    @Override
    public AuditTrailsResponse saveAuditTrails(AuditTrailsRequest request)  {
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
