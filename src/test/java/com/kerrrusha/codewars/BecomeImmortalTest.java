package com.kerrrusha.codewars;

import org.junit.jupiter.api.Test;

import static com.kerrrusha.codewars.BecomeImmortal.elderAge;
import static org.junit.jupiter.api.Assertions.*;

class BecomeImmortalTest {
    @Test
    void easy() {
        assertEquals(5, elderAge(8, 5, 1, 100));
    }

    @Test
    void medium() {
        assertEquals(224, elderAge(8, 8, 0, 100007));
        assertEquals(11925, elderAge(25, 31, 0, 100007));
        assertEquals(4323, elderAge(5, 45, 3, 1000007));
        assertEquals(1586, elderAge(31, 39, 7, 2345));
        assertEquals(808451, elderAge(545, 435, 342, 1000007));
    }

    @Test
    void hard() {
        // You need to run this test very quickly before attempting the actual tests :)
        assertEquals(5456283, elderAge(28827050410L, 35165045587L, 7109602, 13719506));
    }
}