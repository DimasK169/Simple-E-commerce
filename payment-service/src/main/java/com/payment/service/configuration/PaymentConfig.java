package com.payment.service.configuration;

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

import static com.payment.service.constant.GeneralConstant.*;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = PAYMENT_REPOSITORY,
        entityManagerFactoryRef = PAYMENT_ENTITY_MANAGER_FACTORY,
        transactionManagerRef = PAYMENT_TRANSACTION_MANAGER
)
public class PaymentConfig {

    @Primary
    @Bean(name = PAYMENT_DATA_SOURCE)
    @ConfigurationProperties(prefix = PAYMENT_DATASOURCE_PATH)
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = PAYMENT_ENTITY_MANAGER_FACTORY)
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier(PAYMENT_DATA_SOURCE) DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages(PAYMENT_ENTITY)
                .persistenceUnit("payment")
                .build();
    }

    @Primary
    @Bean(name = PAYMENT_TRANSACTION_MANAGER)
    public PlatformTransactionManager transactionManager(
            @Qualifier(PAYMENT_ENTITY_MANAGER_FACTORY) EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

}
