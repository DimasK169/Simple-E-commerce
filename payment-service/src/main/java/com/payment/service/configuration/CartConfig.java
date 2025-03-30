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

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.payment.service.repository.cart",
        entityManagerFactoryRef = "cartEntityManagerFactory",
        transactionManagerRef = "cartTransactionManager"
)
public class CartConfig {

    @Bean(name = "cartDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.cart")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "cartEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("cartDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.payment.service.entity.cart")
                .persistenceUnit("cart")
                .build();
    }

    @Bean(name = "cartTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("cartEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

}
