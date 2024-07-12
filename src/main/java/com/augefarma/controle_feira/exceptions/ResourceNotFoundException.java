package com.augefarma.controle_feira.exceptions;

public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException (String mensagem) {
        super(mensagem);
    }
}
