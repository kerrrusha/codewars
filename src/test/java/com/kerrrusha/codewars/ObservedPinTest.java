package com.kerrrusha.codewars;

import org.junit.jupiter.api.Test;

import java.util.List;

class ObservedPinTest {
    @Test
    void getPINs() {
        List<String> pins = ObservedPin.getPINs("10");
        System.out.println(pins);
    }
}