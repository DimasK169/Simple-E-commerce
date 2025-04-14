package com.flash_sale.app.configuration;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.flash_sale.app.repository.flash_sale",
        entityManagerFactoryRef = "fsEntityManagerFactory",
        transactionManagerRef = "fsTransactionManager"
)
public class FlashSaleConfig {

    @Primary
    @Bean(name = "fsDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.flash-sale")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "fsEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("fsDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.flash_sale.app.entity.flash_sale")
                .persistenceUnit("fs")
                .build();
    }

    @Primary
    @Bean(name = "fsTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("fsEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

}
