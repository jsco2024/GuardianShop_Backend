package com.ms_security.ms_security.service.impl;

import com.ms_security.ms_security.service.model.dto.ErrorControlDto;
import com.ms_security.ms_security.service.model.dto.ErrorResponseDto;
import com.ms_security.ms_security.service.IErrorService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Implementation of the IErrorService interface.
 * Provides methods for retrieving error details from an external API.
 */
@RequiredArgsConstructor
@Service
public class ErrorImpl implements IErrorService {

    @Value("${external.url-api-container}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    /**
     * Retrieves details of an error based on its ID from an external API.
     *
     * @param errorId the ID of the error to retrieve
     * @return an ErrorResponseDto containing details about the error
     */
    @Override
    public ErrorResponseDto findByErrorId(String errorId) {
        String url = apiUrl + "error/list/errorId";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<String> requestEntity = new HttpEntity<>(errorId, headers);
        ResponseEntity<ErrorResponseDto> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                ErrorResponseDto.class
        );
        return responseEntity.getBody();
    }

    /**
     * Retrieves details of all errors from an external API.
     *
     * @return a List of ErrorResponseDto containing details about all errors
     */
    @Override
    public List<ErrorResponseDto> findAllErrors() {
        String url = apiUrl + "error/list/all";
        ResponseEntity<List<ErrorResponseDto>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                null,
                new ParameterizedTypeReference<List<ErrorResponseDto>>() {}
        );
        return responseEntity.getBody();
    }
}
