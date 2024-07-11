package com.augefarma.controle_feira.dtos.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FielMessageDto {

    private String fieldName;
    private String message;
}
