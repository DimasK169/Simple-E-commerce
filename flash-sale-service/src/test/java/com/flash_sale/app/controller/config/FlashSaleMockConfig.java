package com.flash_sale.app.controller.config;

import com.flash_sale.app.service.Interface.FlashSaleService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class FlashSaleMockConfig {

    @Bean
    public FlashSaleService flashSaleService() {
        return Mockito.mock(FlashSaleService.class);
    }
}
