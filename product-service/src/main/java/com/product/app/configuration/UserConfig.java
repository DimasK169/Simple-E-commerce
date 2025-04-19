package com.product.app.configuration;

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

import static com.product.app.constant.GeneralConstant.*;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = USER_REPOSITORY,
        entityManagerFactoryRef = USER_ENTITY_MANAGER_FACTORY,
        transactionManagerRef = USER_TRANSACTION_MANAGER
)
public class UserConfig {

    @Bean(name = USER_DATA_SOURCE)
    @ConfigurationProperties(prefix = USER_DATASOURCE_PATH)
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = USER_ENTITY_MANAGER_FACTORY)
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier(USER_DATA_SOURCE) DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages(USER_ENTITY)
                .persistenceUnit("users")
                .build();
    }

    @Bean(name = USER_TRANSACTION_MANAGER)
    public PlatformTransactionManager transactionManager(
            @Qualifier(USER_ENTITY_MANAGER_FACTORY) EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

}
