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

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.cart.service.repository.user",
        entityManagerFactoryRef = "usersEntityManagerFactory",
        transactionManagerRef = "usersTransactionManager"
)
public class UserConfig {

    @Bean(name = "usersDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.user")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "usersEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("usersDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.cart.service.entity.user")
                .persistenceUnit("users")
                .build();
    }

    @Bean(name = "usersTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("usersEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

}
