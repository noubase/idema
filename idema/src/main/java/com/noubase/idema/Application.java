package com.noubase.idema;

import com.mongodb.WriteConcern;
import com.noubase.core.crud.config.CRUDApplication;
import com.noubase.idema.config.MongoConfig;
import com.noubase.idema.config.SecurityConfig;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * © 07.02.15 rshuper
 */
@Import(value = {MongoConfig.class, SecurityConfig.class})
@SpringBootApplication
@ComponentScan(basePackages = "com.noubase")
@EnableConfigurationProperties
@EnableCaching
public class Application extends CRUDApplication {

    @NotNull
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
