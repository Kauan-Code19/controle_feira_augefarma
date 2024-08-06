package com.augefarma.controle_feira.entities.entry_exit;

import lombok.Getter;

@Getter
public enum EventSegment {
    FAIR("FAIR"),
    PARTY("PARTY"),
    BUFFET("BUFFET");

    private final String segment;

    EventSegment(String segment) {this.segment = segment;}
}
