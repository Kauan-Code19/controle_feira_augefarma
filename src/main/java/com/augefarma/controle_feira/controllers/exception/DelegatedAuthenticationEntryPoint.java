package com.augefarma.controle_feira.controllers.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import java.io.IOException;

/**
 * DelegatedAuthenticationEntryPoint is an implementation of the AuthenticationEntryPoint interface.
 * This class is responsible for handling authentication exceptions that occur during
 * the user authentication process. When an authentication exception is thrown,
 * the commence method is called, where the exception is handled using a specific
 * HandlerExceptionResolver.
 */
@Component("delegatedAuthenticationEntryPoint")
public class DelegatedAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // Dependency injection of the HandlerExceptionResolver that will be used to resolve exceptions.
    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    /**
     * This method is called when an authentication exception occurs.
     * It delegates the exception handling to the configured HandlerExceptionResolver.
     *
     * @param request        The HttpServletRequest object that contains the client's request.
     * @param response       The HttpServletResponse object that will be used to send the response to the client.
     * @param authException  The authentication exception that occurred.
     * @throws IOException   Thrown if an input/output error occurs.
     * @throws ServletException Thrown if an error occurs while processing the request.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        // Resolves the authentication exception using the HandlerExceptionResolver.
        resolver.resolveException(request, response, null, authException);
    }
}