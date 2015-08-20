package com.noubase.idema.config;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import com.noubase.core.crud.repository.CRUDRepositoryImpl;
import com.noubase.core.crud.repository.CustomMongoRepositoryFactoryBean;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * © 07.02.15 rshuper
 */
@Configuration
@EnableMongoAuditing
@EnableMongoRepositories(basePackages = "com.noubase.idema.repository",
        repositoryBaseClass = CRUDRepositoryImpl.class,
        repositoryFactoryBeanClass = CustomMongoRepositoryFactoryBean.class)
public class MongoConfig extends AbstractMongoConfiguration {

    @Value("${mongodb.host}")
    private String host;

    @Value("${mongodb.port}")
    private Integer port;

    @Value("${mongodb.db}")
    private String dbName;


    @Override
    protected String getDatabaseName() {
        return this.dbName;
    }


    @NotNull
    @Override
    public Mongo mongo() throws Exception {
        return new MongoClient(this.host, this.port);
    }

    @NotNull
    @Override
    public MongoTemplate mongoTemplate() throws Exception {
        MongoTemplate template = super.mongoTemplate();
        template.setWriteConcern(WriteConcern.SAFE);
        template.setWriteResultChecking(WriteResultChecking.EXCEPTION);
        return template;
    }
}
