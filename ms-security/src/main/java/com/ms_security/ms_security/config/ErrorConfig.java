package com.ms_security.ms_security.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms_security.ms_security.service.IErrorService;
import com.ms_security.ms_security.service.model.dto.ErrorResponseDto;
import com.ms_security.ms_security.utilities.EncoderUtilities;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration class for loading and encoding error details.
 * Retrieves all error details from the IErrorService and encodes them for later use.
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class ErrorConfig {

    private final IErrorService errorService;
    private final ObjectMapper objectMapper;
    @Getter
    private List<ErrorResponseDto> errorResponseDtos;

    /**
     * Initializes the ErrorConfig by loading and encoding all error details.
     * This method is executed after the bean's properties have been set.
     *
     * @throws Exception if there is an issue with encoding or decoding error details
     */
    @PostConstruct
    public void init() {
        try {
            List<ErrorResponseDto> allErrors = errorService.findAllErrors();
            String erroresString = EncoderUtilities.encodeResponse(allErrors);
            errorResponseDtos = objectMapper.readValue(
                    EncoderUtilities.base64Decode(erroresString),
                    new TypeReference<List<ErrorResponseDto>>() {}
            );
            log.info("Errores cargados: {}", errorResponseDtos);
        } catch (Exception e) {
            log.error("Error al cargar los detalles de error: {}", e.getMessage(), e);
        }
    }
}
