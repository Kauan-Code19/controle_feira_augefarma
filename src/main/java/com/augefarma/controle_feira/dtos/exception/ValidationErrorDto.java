package com.augefarma.controle_feira.dtos.exception;

import lombok.Getter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ValidationErrorDto extends CustomErrorDto {

    private final List<FielMessageDto> errors = new ArrayList<>(); // List to hold field-specific error messages

    /**
     * Constructor to initialize the validation error DTO.
     *
     * @param timestamp the time at which the error occurred
     * @param status the HTTP status code
     * @param error a brief description of the error
     * @param trace the stack trace of the error
     */
    public ValidationErrorDto(Instant timestamp, Integer status, String error, String trace) {
        super(timestamp, status, error, trace);
    }

    /**
     * Method to add an error message for a specific field.
     *
     * @param fieldName the name of the field where the error occurred
     * @param message the error message
     */
    public void addError(String fieldName, String message) { errors.add(new FielMessageDto(fieldName, message)); }
}
