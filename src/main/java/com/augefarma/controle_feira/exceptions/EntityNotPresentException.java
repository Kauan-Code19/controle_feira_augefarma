package com.augefarma.controle_feira.exceptions;

public class EntityNotPresentException extends RuntimeException {
    public EntityNotPresentException(String message) {
        super(message);
    }
}
