package com.noubase.core;

import com.noubase.core.crud.config.CRUDApplication;
import com.noubase.core.test.common.AbstractTest;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by rshuper on 20.08.15.
 */
@SpringApplicationConfiguration(classes = {ApplicationTest.Application.class})
public abstract class ApplicationTest extends AbstractTest {

    @SpringBootApplication
    @ComponentScan(basePackages = "com.noubase")
    @EnableConfigurationProperties
    static class Application extends CRUDApplication {
    }
}
