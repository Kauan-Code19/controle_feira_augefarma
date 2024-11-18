package com.augefarma.controle_feira.entities.administrator;

import lombok.Getter;

@Getter
public enum AdministratorRole {
    LEVEL_ONE("levelOne"),
    LEVEL_TWO("levelTwo");

    private String role;

    AdministratorRole(String role) {
        this.role = role;
    }
}
