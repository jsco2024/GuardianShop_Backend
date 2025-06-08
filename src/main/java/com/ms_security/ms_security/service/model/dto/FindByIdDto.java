package com.ms_security.ms_security.service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class FindByIdDto implements Serializable {
    private Long id;
}
