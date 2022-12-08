package com.rapipay.NewTransactionManager.configuration;


import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

import javax.sql.DataSource;

@Configuration
public class DataConfig {

    LoadsecretKey loadsecretKey = new LoadsecretKey();

    @Bean
    public DataSource dataSource() {

        loadsecretKey.getSecretKeyPostgres();
        return DataSourceBuilder.create()
                .driverClassName("org.postgresql.Driver")
                .password(loadsecretKey.getPostgresPassword())
                .username(loadsecretKey.getPostgresUsername())
                .url(loadsecretKey.getPostgresUrl())
                .build();
    }

    @Bean
    public MongoDatabaseFactory mongoDbFactory() {
        loadsecretKey.getSecretKeyMongoDb();
        return new SimpleMongoClientDatabaseFactory(loadsecretKey.getMongodbUrl());
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());

        return mongoTemplate;

    }

}
