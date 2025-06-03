package com.ms_security.ms_security.config;

import com.ms_security.ms_security.service.model.validation.UserValidation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up user validation.
 *
 * This class is responsible for defining a bean that handles user validation
 * logic. The UserValidation bean can then be injected and used across the
 * application wherever validation is needed.
 */
@Configuration
public class ValidationConfig {

    /**
     * Creates and provides a bean of UserValidation.
     *
     * This method defines a bean of type UserValidation. By annotating the
     * method with @Bean, Spring will manage the UserValidation instance,
     * making it available for dependency injection throughout the application.
     *
     * @return an instance of UserValidation
     */
    @Bean
    public UserValidation userValidation() {
        return new UserValidation();
    }

}
