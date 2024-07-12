package com.augefarma.controle_feira.dtos.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.Instant;

@AllArgsConstructor
@Getter
public class CustomErrorDto {

    private Instant timestamp;
    private Integer status;
    private String error;
    private String trace;
}
