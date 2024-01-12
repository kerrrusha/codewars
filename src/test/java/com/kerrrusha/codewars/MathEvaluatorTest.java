package com.kerrrusha.codewars;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MathEvaluatorTest {
    @Test
    public void testParentheses() {
        assertEquals(26, new MathEvaluator().calculate("(20 / (1.5 + 3.5) * 4 + (0 + 4)) - -6"), 0.01);
    }

    @Test
    public void testHard() {
        assertEquals(1, new MathEvaluator().calculate("(123.45*(678.90 / (-2.5+ 11.5)-(((80 -(19))) *33.25)) / 20) - (123.45*(678.90 / (-2.5+ 11.5)-(((80 -(19))) *33.25)) / 20) + (13 - 2)/ -(-11) "), 0.01);
    }

    @Test
    public void testAddition() {
        assertEquals(2d, new MathEvaluator().calculate("1+1"), 0.01);
    }

    @Test
    public void testSubtraction() {
        assertEquals(0d, new MathEvaluator().calculate("1 - 1"), 0.01);
    }

    @Test
    public void testMultiplication() {
        assertEquals(1d, new MathEvaluator().calculate("1* 1"), 0.01);
    }

    @Test
    public void testDivision() {
        assertEquals(1d, new MathEvaluator().calculate("1 /1"), 0.01);
    }

    @Test
    public void testNegative() {
        assertEquals(-123d, new MathEvaluator().calculate("-123"), 0.01);
    }

    @Test
    public void testLiteral() {
        assertEquals(123d, new MathEvaluator().calculate("123"), 0.01);
    }

    @Test
    public void testExpression() {
        assertEquals(21.25, new MathEvaluator().calculate("2 /2+3 * 4.75- -6"), 0.01);
    }

    @Test
    public void testSimple() {
        assertEquals(1476d, new MathEvaluator().calculate("12* 123"), 0.01);
    }

    @Test
    public void testComplex() {
        assertEquals(7.732, new MathEvaluator().calculate("2 / (2 + 3) * 4.33 - -6"), 0.01);
    }
}