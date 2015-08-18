package com.noubase.core.crud.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.noubase.core.crud.serialization.Public;
import com.noubase.core.crud.serialization.RESTObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.filter.CharacterEncodingFilter;

/**
 * Created by rshuper on 18.08.15.
 */
@Configuration
public abstract class CRUDApplication {

    @NotNull
    @Bean
    public ObjectMapper httpObjectMapper() {
        ObjectMapper mapper = new RESTObjectMapper();

        return mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
                .setConfig(mapper.getSerializationConfig().withView(Public.class));
    }

    @Bean
    @NotNull
    public MappingJackson2HttpMessageConverter jsonMessageConverter() {
        return new MappingJackson2HttpMessageConverter(httpObjectMapper());
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

    @NotNull
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager();
    }
}
