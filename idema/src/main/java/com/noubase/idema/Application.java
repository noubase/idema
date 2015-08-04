package com.noubase.idema;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.WriteConcern;
import com.noubase.idema.config.MongoConfig;
import com.noubase.idema.util.RESTObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.validation.constraints.NotNull;

/**
 * © 07.02.15 rshuper
 */
@Import(value = {MongoConfig.class})
@SpringBootApplication
@ComponentScan(basePackages = "com.noubase")
@EnableConfigurationProperties
@EnableCaching
@EnableWebMvc
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class Application {

    @NotNull
    @Bean
    public ObjectMapper httpObjectMapper() {
        return new RESTObjectMapper();
    }

    @NotNull
    @Autowired
    @Bean
    public MappingJackson2HttpMessageConverter jsonMessageConverter(ObjectMapper mapper) {
        return new MappingJackson2HttpMessageConverter(mapper);
    }

    @NotNull
    @Bean
    public CharacterEncodingFilter characterEncodingFilter() {
        final CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);
        return characterEncodingFilter;
    }

    @NotNull
    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager();
    }

    @Bean
    public WriteConcern clientWriteConcern() {
        return WriteConcern.ACKNOWLEDGED;
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }
}
