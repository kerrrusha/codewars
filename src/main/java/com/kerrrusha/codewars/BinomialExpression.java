package com.kerrrusha.codewars;

import static java.lang.Math.abs;
import static java.lang.Math.pow;

public class BinomialExpression {

    public static String expand(String expr) {
        return Expression.parse(expr).expand();
    }

    // (ax+b)^n
    public record Expression(long a, String var, long b, long n) {

        public String expand() {
            StringBuilder result = new StringBuilder();
            for (long i = n; i >= 0; i--) {
                long coef = getCoef(i);

                if (coef == 0) {
                    continue;
                }

                if (coef > 0 && i != n) {
                    result.append("+");
                }

                if (coef == -1 && i == n) {
                    result.append("-");
                } else if (abs(coef) != 1 || i == 0) {
                    result.append(coef);
                }

                if (i == 1) {
                    result.append(var);
                } else if (i != 0) {
                    result.append(var).append("^").append(i);
                }
            }
            return result.toString();
        }

        private long getCoef(long i) {
            long numerator = 1;
            for (long j = 0; j < i; j++) {
                numerator *= n - j;
            }
            long k = numerator / fact(i);
            long aPow = (long) pow(a, i);
            long bPow = (long) pow(b, n - i);
            return k * aPow * bPow;
        }

        public static long fact(long n) {
            return (n == 0 || n == 1) ? 1 : n * fact(n - 1);
        }

        public static Expression parse(String str) {
            long n = parseN(str);
            long a = parseA(str);
            long b = parseB(str);
            String var = parseVar(str);
            return new Expression(a, var, b, n);
        }

        private static long parseN(String str) {
            int index = str.indexOf("^");
            String resultStr = str.substring(index + 1);
            return Long.parseLong(resultStr);
        }

        private static long parseA(String str) {
            str = str.replace("(", "")
                    .replace(")", "");
            int varIndex = findVarIndex(str);
            String resultStr = str.substring(0, varIndex);

            if (resultStr.length() == 0) {
                return 1;
            }
            if (resultStr.equals("-")) {
                return -1;
            }
            return Long.parseLong(resultStr);
        }

        private static long parseB(String str) {
            int varIndex = findVarIndex(str);
            int endIndex = str.indexOf(")");
            String resultStr = str.substring(varIndex + 1, endIndex);
            return Long.parseLong(resultStr);
        }

        private static String parseVar(String str) {
            int varIndex = findVarIndex(str);
            return str.substring(varIndex, varIndex + 1);
        }

        private static int findVarIndex(String str) {
            for (int i = 0; i < str.length(); i++) {
                if (Character.isLetter(str.charAt(i))) {
                    return i;
                }
            }
            throw new IllegalArgumentException("There's no variable in expression string: " + str);
        }
    }
}
