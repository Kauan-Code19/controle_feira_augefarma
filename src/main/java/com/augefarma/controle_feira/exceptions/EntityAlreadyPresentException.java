package com.augefarma.controle_feira.exceptions;

public class EntityAlreadyPresentException extends RuntimeException {
    public EntityAlreadyPresentException(String message) {
        super(message);
    }
}
