package com.saltlux.khnp.searcher.common.config;

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

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "gaonEntityManagerFactory",
        transactionManagerRef = "gaonTransactionManager",
        basePackages = {"com.saltlux.khnp.searcher.tagname.repository"})
public class GaonDatasourceConfig {

    @Bean(name = "gaonDataSource")
    @ConfigurationProperties(prefix = "gaon.datasource")
    public DataSource gaonDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "gaonEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean gaonEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("gaonDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.saltlux.khnp.searcher.tagname")
                .persistenceUnit("gaon")
                .build();
    }

    @Bean(name = "gaonTransactionManager")
    public PlatformTransactionManager gaonTransactionManager(
            @Qualifier("gaonEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

}
