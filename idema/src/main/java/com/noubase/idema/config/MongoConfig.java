package com.noubase.idema.config;

import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import javax.validation.constraints.NotNull;

/**
 * © 07.02.15 rshuper
 */
@Configuration
@EnableMongoAuditing
@EnableMongoRepositories(basePackages = "com.noubase.idema.repository")
public class MongoConfig {

    @Value("${mongodb.host}")
    private String host;

    @Value("${mongodb.port}")
    private Integer port;

    @Value("${mongodb.db}")
    private String dbName;

    @NotNull
    @Bean
    public MongoClient mongo() throws Exception {
        return new MongoClient(host, port);
    }

    @NotNull
    @Bean
    public MongoDbFactory mongoDBFactory() throws Exception {
        return new SimpleMongoDbFactory(mongo(), dbName);
    }

    @Bean
    public DefaultMongoTypeMapper defaultTypeMapper() {
        return new DefaultMongoTypeMapper(null);
    }

    @NotNull
    @Bean
    public MappingMongoConverter mappingConverter() throws Exception {
        MappingMongoConverter converter
                = new MappingMongoConverter(new DefaultDbRefResolver(mongoDBFactory()), new MongoMappingContext());
        converter.setTypeMapper(defaultTypeMapper());
        return converter;
    }

    @NotNull
    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        MongoTemplate template = new MongoTemplate(mongoDBFactory(), mappingConverter());
        template.setWriteConcern(WriteConcern.SAFE);
        template.setWriteResultChecking(WriteResultChecking.EXCEPTION);
        return template;
    }
}
