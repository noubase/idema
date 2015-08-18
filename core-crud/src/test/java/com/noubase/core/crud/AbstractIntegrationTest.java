package com.noubase.core.crud;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import com.noubase.core.crud.config.CRUDApplication;
import com.noubase.core.crud.repository.CRUDRepositoryImpl;
import com.noubase.core.crud.repository.CustomMongoRepositoryFactoryBean;
import com.noubase.core.crud.test.AbstractControllerTest;
import com.noubase.core.security.ExpirableUserDetails;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Created by rshuper on 18.08.15.
 */

@SpringApplicationConfiguration(classes = {AbstractIntegrationTest.Application.class})
abstract class AbstractIntegrationTest<U extends ExpirableUserDetails> extends AbstractControllerTest<U> {

    @SpringBootApplication
    @ComponentScan(basePackages = "com.noubase.core.crud")
    @EnableConfigurationProperties
    static class Application extends CRUDApplication {
    }

    @Configuration
    @EnableMongoAuditing
    @EnableMongoRepositories(basePackages = "com.noubase.core.crud",
            repositoryBaseClass = CRUDRepositoryImpl.class,
            repositoryFactoryBeanClass = CustomMongoRepositoryFactoryBean.class)
    static class MongoConfig extends AbstractMongoConfiguration {

        @Override
        protected String getDatabaseName() {
            return "noubase_core_crud_tests";
        }


        @NotNull
        @Override
        public Mongo mongo() throws Exception {
            return new MongoClient("localhost", 27017);
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

    protected AbstractIntegrationTest(Class<U> userClass) {
        super(userClass);
    }
}
