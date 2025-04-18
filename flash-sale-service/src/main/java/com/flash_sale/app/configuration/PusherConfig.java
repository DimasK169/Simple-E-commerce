package com.flash_sale.app.configuration;

import com.pusher.rest.Pusher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PusherConfig {


    @Value("${pusher.app-id}")
    private String appId;

    @Value("${pusher.key}")
    private String key;

    @Value("${pusher.secret}")
    private String secret;

    @Bean
    public Pusher pusher() {
        Pusher pusher = new Pusher(appId, key, secret);
        pusher.setCluster("ap1");
        pusher.setEncrypted(true);
        return pusher;
    }

}
