package com.kerrrusha.codewars;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BinomialExpressionTest {

    @Test
    public void expressionParseTest() {
        String expr = "(-2k-3)^3";
        BinomialExpression.Expression expected = new BinomialExpression.Expression(-2, "k", -3, 3);

        BinomialExpression.Expression actual = BinomialExpression.Expression.parse(expr);

        assertEquals(expected, actual);
    }
    
    @Test
    public void testBPositive() {
        assertEquals("1", BinomialExpression.expand("(x+1)^0"));
    }

    @Test
    public void test1() {
        assertEquals("x^2+2x+1", BinomialExpression.expand("(x+1)^2"));
    }

    @Test
    public void test2() {
        assertEquals("x+1", BinomialExpression.expand("(x+1)^1"));
    }

    @Test
    public void testBNegative() {
        assertEquals("1", BinomialExpression.expand("(x-1)^0"));
        assertEquals("x-1", BinomialExpression.expand("(x-1)^1"));
        assertEquals("x^2-2x+1", BinomialExpression.expand("(x-1)^2"));
    }

    @Test
    public void testAPositive() {
        assertEquals("625m^4+1500m^3+1350m^2+540m+81", BinomialExpression.expand("(5m+3)^4"));
        assertEquals("8x^3-36x^2+54x-27", BinomialExpression.expand("(2x-3)^3"));
        assertEquals("1", BinomialExpression.expand("(7x-7)^0"));
    }

    @Test
    public void testANegative() {
        assertEquals("625m^4-1500m^3+1350m^2-540m+81", BinomialExpression.expand("(-5m+3)^4"));
        assertEquals("-8k^3-36k^2-54k-27", BinomialExpression.expand("(-2k-3)^3"));
        assertEquals("1", BinomialExpression.expand("(-7x-7)^0"));
    }

}
