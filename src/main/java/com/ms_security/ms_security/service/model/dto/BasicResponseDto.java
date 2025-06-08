package com.ms_security.ms_security.service.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Objects;

/**
 * A class representing a standard response structure for API responses.
 *
 * @param <T> the type of the data contained in the response
 */
@NoArgsConstructor
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class BasicResponseDto<T> {
    @NonNull
    @JsonProperty("codeResponse")
    private Long code;
    @NonNull
    @JsonProperty("messageResponse")
    private String message;
    private Object data;

    /**
     * Checks if this {@code BasicResponseDto} is equal to another object.
     *
     * @param object the object to compare
     * @return {@code true} if the objects are equal, {@code false} otherwise
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        BasicResponseDto<?> that = (BasicResponseDto<?>) object;
        return Objects.equals(code, that.code) && Objects.equals(message, that.message) && Objects.equals(data, that.data);
    }

    /**
     * Returns the hash code value for this {@code BasicResponseDto}.
     *
     * @return the hash code value
     */
    @Override
    public int hashCode() {
        return Objects.hash(code, message, data);
    }
}
