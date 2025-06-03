package com.ms_security.ms_security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class that defines beans for the application context.
 *
 * This class is marked with @Configuration, indicating that it provides
 * configuration metadata for Spring. It contains methods that are annotated
 * with @Bean, which tells Spring to manage the returned objects as beans.
 */
@Configuration
public class AppConfig {

    /**
     * Creates and configures a RestTemplate bean.
     *
     * The RestTemplate is a synchronous client that allows the application
     * to make HTTP requests and interact with RESTful web services.
     * By defining this method as a @Bean, Spring will manage the
     * RestTemplate instance, making it available for dependency injection
     * throughout the application.
     *
     * @return a new instance of RestTemplate
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}