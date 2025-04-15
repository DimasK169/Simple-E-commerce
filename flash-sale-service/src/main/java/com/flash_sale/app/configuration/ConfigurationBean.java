package com.flash_sale.app.configuration;

import com.flash_sale.app.entity.flash_sale.AuditTrails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigurationBean {

    @Bean
    public AuditTrails auditTrails(){
        return new AuditTrails();
    }
}
