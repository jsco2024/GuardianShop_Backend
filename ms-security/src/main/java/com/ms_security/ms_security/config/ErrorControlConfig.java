package com.ms_security.ms_security.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms_security.ms_security.service.model.dto.ErrorResponseDto;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.logging.Logger;

/**
 * Configuration class for managing error responses.
 *
 * This class is responsible for fetching and deserializing error responses
 * from an external API during the application's startup. The retrieved errors
 * are stored as a list of ErrorResponseDto objects.
 */
@Configuration
public class ErrorControlConfig {

    private static final Logger log = Logger.getLogger(ErrorControlConfig.class.getName());

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private List<ErrorResponseDto> errorResponseDtos;

    @Value("${external.url-api-container}")
    private String externalApiUrl;

    /**
     * Constructor for dependency injection.
     *
     * @param restTemplate the RestTemplate used for making HTTP requests
     * @param objectMapper the ObjectMapper used for JSON deserialization
     */
    public ErrorControlConfig(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Initializes the configuration by fetching the error list from the external API.
     *
     * This method is annotated with @PostConstruct, ensuring it runs after
     * the class has been constructed and all dependencies injected. It sends
     * a POST request to the specified API endpoint, retrieves the error data
     * as a JSON string, and deserializes it into a list of ErrorResponseDto objects.
     *
     * @throws Exception if an error occurs during the API call or deserialization
     */
    @PostConstruct
    public void init() throws Exception {
        String apiUrl = externalApiUrl + "error/list/all";
        String erroresString = restTemplate.postForObject(apiUrl, null, String.class);
        log.info("Response from API: " + erroresString);
        errorResponseDtos = objectMapper.readValue(erroresString, new TypeReference<>() {});
        log.info("Deserialized response: " + errorResponseDtos.toString());
    }
}
