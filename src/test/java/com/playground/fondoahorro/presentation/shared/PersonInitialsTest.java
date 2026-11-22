package com.playground.fondoahorro.presentation.shared;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PersonInitialsTest {

    @Test
    void usesFirstLettersOfFirstTwoNameParts() {
        assertEquals("AM", PersonInitials.fromName("Ana María Pérez"));
    }

    @Test
    void usesFirstTwoLettersForSinglePartNames() {
        assertEquals("JO", PersonInitials.fromName("Jo"));
    }

    @Test
    void returnsFallbackForBlankNames() {
        assertEquals("?", PersonInitials.fromName("  "));
    }
}
