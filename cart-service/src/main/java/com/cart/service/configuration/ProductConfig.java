package com.cart.service.configuration;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

import static com.cart.service.constant.GeneralConstant.*;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = PRODUCT_REPOSITORY,
        entityManagerFactoryRef = PRODUCT_ENTITY_MANAGER_FACTORY,
        transactionManagerRef = PRODUCT_TRANSACTION_MANAGER
)
public class ProductConfig {

    @Bean(name = PRODUCT_DATA_SOURCE)
    @ConfigurationProperties(prefix = PRODUCT_DATASOURCE_PATH)
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = PRODUCT_ENTITY_MANAGER_FACTORY)
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier(PRODUCT_DATA_SOURCE) DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages(PRODUCT_ENTITY)
                .persistenceUnit("product")
                .build();
    }

    @Bean(name = PRODUCT_TRANSACTION_MANAGER)
    public PlatformTransactionManager transactionManager(
            @Qualifier(PRODUCT_ENTITY_MANAGER_FACTORY) EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

}
