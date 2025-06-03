package com.ms_security.ms_security.utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.validation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class EncoderUtilities {
    private static Validator _validator = Validation.buildDefaultValidatorFactory().getValidator();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JavaTimeModule());
        configureDateFormat(objectMapper);
    }

    private static void configureDateFormat(ObjectMapper objectMapper) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        objectMapper.registerModule(new JavaTimeModule()
                .addSerializer(LocalDate.class, new LocalDateSerializer(dateFormatter))
                .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter))
        );
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"));
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public static <T> List<T> decodeRequestListNoBase64(String encode, Class<T> type) {
        ObjectMapper localObjectMapper = new ObjectMapper(); // Cambiado a variable local
        JavaType listType = localObjectMapper.getTypeFactory().constructCollectionType(List.class, type);
        try {
            return localObjectMapper.readValue(encode, listType);
        } catch (UnrecognizedPropertyException e) {
            throw new ValidationException("ERROR PROCESSING JSON: " + e.getMessage());
        } catch (JsonProcessingException e) {
            throw new ValidationException("ERROR PROCESSING JSON: " + e.getMessage());
        }
    }

    public static String base64Decode(String encoded) {
        byte[] decodedBytes = java.util.Base64.getDecoder().decode(encoded);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }

    public static String base64Encode(String encode) {
        Base64 base64 = new Base64();
        return base64.encodeToString(encode.getBytes());
    }

    public static boolean isBase64Format(String encode) {
        return encode.matches("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$");
    }

    public static <T> T decodeRequest(String encode, Class<T> type) {

        try {
            return objectMapper.readValue(base64Decode(encode), type);
        } catch (UnrecognizedPropertyException e) {
            throw new ValidationException(e.getMessage());
        } catch (JsonProcessingException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    public static <T> List<T> decodeRequestList(String encode, Class<T> type) {
        JavaType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, type);
        try {
            return objectMapper.readValue(base64Decode(encode), listType);
        } catch (UnrecognizedPropertyException e) {
            throw new ValidationException("ERROR PROCESSING JSON: " + e.getMessage());
        } catch (JsonProcessingException e) {
            throw new ValidationException("ERROR PROCESSING JSON: " + e.getMessage());
        }
    }

    public static String encodeResponse(Object response) {
        try {
            return base64Encode(objectMapper.writeValueAsString(response));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("ERROR ENCODING RESPONSE: " + e.getMessage());
        }
    }

    public static String formatJson(Object response) {
        try {
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("ERROR FORMATTING JSON: " + e.getMessage());
        }
    }

    public static void validateBase64(String request) {
        if (Objects.nonNull(request) && isBase64Format(request)) {
        } else {
            throw new IllegalArgumentException("INVALID BASE64 ENCODING");
        }
    }

    public static <T> void validator(T encode) {
        Set<ConstraintViolation<T>> violations = _validator.validate(encode);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException("VALIDATION FAILED", violations);
        }
    }

    public static <T> void validator(T encode, Class<?> clase) {
        Set<ConstraintViolation<T>> violations = _validator.validate(encode, clase);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    public static JsonNode convertToJsonNode(Object object) {
        return objectMapper.valueToTree(object);
    }
}
