package com.augefarma.controle_feira.controllers.exception;

import com.augefarma.controle_feira.dtos.exception.CustomErrorDto;
import com.augefarma.controle_feira.dtos.exception.ValidationErrorDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.time.Instant;

@ControllerAdvice
public class ExceptionHandlerController {

    /**
     * Handles validation exceptions when method arguments annotated with @Valid fail validation.
     *
     * @param exception the exception thrown when validation fails
     * @param request the HTTP request during which the exception was thrown
     * @return a ResponseEntity containing the validation error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomErrorDto> methodArgumentNotValidation(MethodArgumentNotValidException exception,
                                                                      HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY; // Set the HTTP status to 422 Unprocessable Entity

        // Create a new ValidationErrorDto with the current timestamp, status code, error message, and request URI
        ValidationErrorDto validationErrorDto = new ValidationErrorDto(Instant.now(),
                status.value(), "Dados Invalidos", request.getRequestURI());

        // Iterate over field errors and add them to the validationErrorDto
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            validationErrorDto.addError(fieldError.getField(), fieldError.getDefaultMessage());
        }

        // Return a ResponseEntity with the validation error details and the HTTP status
        return ResponseEntity.status(status).body(validationErrorDto);
    }
}
