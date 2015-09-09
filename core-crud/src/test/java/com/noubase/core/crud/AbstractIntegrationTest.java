package com.noubase.core.crud;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import com.noubase.core.crud.config.CRUDApplication;
import com.noubase.core.crud.config.security.StatelessAuthenticationSecurityConfig;
import com.noubase.core.crud.repository.ResourceMongoRepositoryFactoryBean;
import com.noubase.core.crud.repository.ResourceRepositoryImpl;
import com.noubase.core.crud.test.AbstractControllerTest;
import com.noubase.core.security.ExpirableUserDetails;
import com.noubase.core.security.SecurityUserRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.io.Serializable;

import static com.noubase.core.crud.test.TestUtil.convertTo;
import static java.lang.String.format;

/**
 * Created by rshuper on 18.08.15.
 */

@SpringApplicationConfiguration(classes = {AbstractIntegrationTest.Application.class})
abstract class AbstractIntegrationTest<User extends ExpirableUserDetails, ID extends Serializable, T extends Persistable<ID>>
        extends AbstractControllerTest<User> {

    protected final Class<T> tClass;

    protected final Class controllerClass;

    public AbstractIntegrationTest(Class<User> userClass, Class<T> tClass, Class controllerClass) {
        super(userClass);
        this.tClass = tClass;
        this.controllerClass = controllerClass;
    }

    protected String getURI() {
        return getURI(controllerClass);
    }

    protected String getURI(@NotNull T object) {
        return format("%s/%s", getURI(), object.getId());
    }

    protected T createAndConvert(T object) throws Exception {
        String location = getLocation(createSuccess(this.getURI(), object));
        return convertTo(getSuccess(location), tClass);
    }

    @SpringBootApplication
    @ComponentScan(basePackages = "com.noubase.core.crud")
    @EnableConfigurationProperties
    static class Application extends CRUDApplication {
    }

    @Configuration
    static class SecurityConfig extends StatelessAuthenticationSecurityConfig<com.noubase.core.crud.User> {

        private SecurityUserRepository userRepo;

        @Autowired
        public void setUserRepo(SecurityUserRepository userRepo) {
            this.userRepo = userRepo;
        }

        @SuppressWarnings("unused")
        public SecurityConfig() {
            super(com.noubase.core.crud.User.class);
        }

        @Override
        protected SecurityUserRepository userRepo() {
            return userRepo;
        }
    }

    @Configuration
    @EnableMongoAuditing
    @EnableMongoRepositories(basePackages = "com.noubase.core.crud",
            repositoryBaseClass = ResourceRepositoryImpl.class,
            repositoryFactoryBeanClass = ResourceMongoRepositoryFactoryBean.class)
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

        @Override
        public MappingMongoConverter mappingMongoConverter() throws Exception {
            MappingMongoConverter converter = super.mappingMongoConverter();
            converter.setTypeMapper(new DefaultMongoTypeMapper(null));
            return converter;
        }
    }

}
