package com.augefarma.controle_feira.exceptions;

public class JWTGenerationException extends RuntimeException{

    public JWTGenerationException(String message) {
        super(message);
    }
}
