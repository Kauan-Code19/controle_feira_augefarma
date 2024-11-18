package com.augefarma.controle_feira.controllers.exception;

import com.augefarma.controle_feira.dtos.exception.CustomErrorDto;
import com.augefarma.controle_feira.dtos.exception.ValidationErrorDto;
import com.augefarma.controle_feira.exceptions.EntityAlreadyPresentException;
import com.augefarma.controle_feira.exceptions.EntityNotPresentException;
import com.augefarma.controle_feira.exceptions.InvalidCredentialsException;
import com.augefarma.controle_feira.exceptions.JWTGenerationException;
import com.augefarma.controle_feira.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ControllerAdvice
public class ExceptionHandlerController  extends ResponseEntityExceptionHandler {

    /**
     * Custom handler for validation errors when method arguments are not valid.
     * <p>
     * Overrides the default behavior to provide a custom response for validation errors.
     *
     * @param exception the MethodArgumentNotValidException triggered by validation failure
     * @param headers the HTTP headers for the response
     * @param status the HTTP status code
     * @param request the web request that resulted in the validation error
     * @return a ResponseEntity containing a custom ValidationErrorDto with error details
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        HttpStatus customStatus = HttpStatus.UNPROCESSABLE_ENTITY;

        ValidationErrorDto validationErrorDto = new ValidationErrorDto(Instant.now(),
                customStatus.value(), "Dados inválidos",
                request.getDescription(false).replace("uri=", ""));

        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            validationErrorDto.addError(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity.status(customStatus).body(validationErrorDto);
    }

    /**
     * Handles exceptions when a resource is not found.
     *
     * @param exception the exception thrown when a resource is not found
     * @param request the HTTP request during which the exception was thrown
     * @return a ResponseEntity containing the error details
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CustomErrorDto> resourceNotFoundException(ResourceNotFoundException exception,
                                                                    HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;  // Set the HTTP status to 404 Not Found

        // Create a new CustomErrorDto with the current timestamp, status code, error message, and request URI
        CustomErrorDto customErrorDto = new CustomErrorDto(Instant.now(),
                status.value(), exception.getMessage(), request.getRequestURI());

        // Return a ResponseEntity with the error details and the HTTP status
        return ResponseEntity.status(status).body(customErrorDto);
    }

    /**
     * Handles exceptions related to authentication failures.
     *
     * @param ex the AuthenticationException thrown during an authentication failure
     * @param request the HTTP request during which the exception was thrown
     * @return a ResponseEntity containing the error details
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseBody
    public ResponseEntity<CustomErrorDto> handleAuthenticationException(AuthenticationException ex,
                                                                        HttpServletRequest request) {
        CustomErrorDto errorDto = new CustomErrorDto(
                Instant.now(),
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDto);
    }

    /**
     * Handles exceptions related to invalid credentials.
     *
     * @param exception the exception thrown when invalid credentials are provided
     * @param request the HTTP request during which the exception was thrown
     * @return a ResponseEntity containing the error details
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<CustomErrorDto> invalidCredentialsException(InvalidCredentialsException exception,
                                                                      HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;  // Set the HTTP status to 401 Unauthorized

        // Create a new CustomErrorDto with the current timestamp, status code, error message, and request URI
        CustomErrorDto customErrorDto = new CustomErrorDto(Instant.now(),
                status.value(), exception.getMessage(), request.getRequestURI());

        // Return a ResponseEntity with the error details and the HTTP status
        return ResponseEntity.status(status).body(customErrorDto);
    }

    /**
     * Handles exceptions related to JWT generation errors.
     *
     * @param exception the exception thrown when there is an issue generating the JWT
     * @param request the HTTP request during which the exception was thrown
     * @return a ResponseEntity containing the error details
     */
    @ExceptionHandler(JWTGenerationException.class)
    public ResponseEntity<CustomErrorDto> illegalArgumentException(JWTGenerationException exception,
                                                                   HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;  // Set the HTTP status to 401 Unauthorized

        // Create a new CustomErrorDto with the current timestamp, status code, error message, and request URI
        CustomErrorDto customErrorDto = new CustomErrorDto(Instant.now(),
                status.value(), exception.getMessage(), request.getRequestURI());

        // Return a ResponseEntity with the error details and the HTTP status
        return ResponseEntity.status(status).body(customErrorDto);
    }

    @ExceptionHandler(EntityAlreadyPresentException.class)
    public ResponseEntity<CustomErrorDto> entityReadyPresent(EntityAlreadyPresentException exception,
                                                                   HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;

        CustomErrorDto customErrorDto = new CustomErrorDto(Instant.now(),
                status.value(), exception.getMessage(), request.getRequestURI());

        return ResponseEntity.status(status).body(customErrorDto);
    }

    @ExceptionHandler(EntityNotPresentException.class)
    public ResponseEntity<CustomErrorDto> entityNotPresent(EntityNotPresentException exception,
                                                             HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;

        CustomErrorDto customErrorDto = new CustomErrorDto(Instant.now(),
                status.value(), exception.getMessage(), request.getRequestURI());

        return ResponseEntity.status(status).body(customErrorDto);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<CustomErrorDto> handleDataIntegrityViolation(DataIntegrityViolationException exception, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;

        String mensagemErro = exception.getMessage();

        Pattern pattern = Pattern.compile("ERRO:.*");
        Matcher matcher = pattern.matcher(mensagemErro);

        String erroDetalhado = "Detalhe não identificado";

        if (matcher.find()) {
            erroDetalhado = matcher.group().trim();
        }

        CustomErrorDto customErrorDto = new CustomErrorDto(Instant.now(),
                status.value(), erroDetalhado, request.getRequestURI());

        return ResponseEntity.status(status).body(customErrorDto);
    }
}
