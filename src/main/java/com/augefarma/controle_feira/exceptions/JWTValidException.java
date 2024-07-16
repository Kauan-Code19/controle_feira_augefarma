package com.augefarma.controle_feira.exceptions;

public class JWTValidException extends RuntimeException{

    public JWTValidException(String message) {
        super(message);
    }
}
