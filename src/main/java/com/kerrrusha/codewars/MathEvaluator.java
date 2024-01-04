package com.kerrrusha.codewars;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MathEvaluator {
    private static final String WHITESPACE = " ";
    private static final Pattern NUMBER_PATTERN = Pattern.compile("-?(\\d+(\\.\\d+)?)");

    public double calculate(String expression) {
        List<Vector2> numberIndexes = getNumberIndexes(expression);
        List<Vector2> nonNumberIndexes = toNonNumberIndexes(numberIndexes, expression);
        List<Vector2> allElementIndexes = union(numberIndexes, nonNumberIndexes);
        List<Object> parsed = parse(expression, allElementIndexes, numberIndexes, nonNumberIndexes);

        double result = 0;
        for (Object element : parsed) {
            if (element instanceof Operation operation) {
                int operationIndex = parsed.indexOf(element);
                double leftOperand = (double) parsed.get(operationIndex - 1);
                double rightOperand = (double) parsed.get(operationIndex + 1);
                result += operation.apply(leftOperand, rightOperand);
            }
        }
        return result;
    }

    private List<Object> parse(String expression, List<Vector2> allElementIndexes, List<Vector2> numberIndexes, List<Vector2> nonNumberIndexes) {
        List<Object> result = new ArrayList<>();
        for (Vector2 location : allElementIndexes) {
            String str = expression.substring(location.first, location.second);
            if (numberIndexes.contains(location)) {
                double parsed = Double.parseDouble(str);
                result.add(parsed);
                continue;
            }
            if (nonNumberIndexes.contains(location)) {
                char opChar = str.replace(WHITESPACE, "").charAt(0);
                Operation op = Operation.parseChar(opChar);
                result.add(op);
            }
        }
        return result;
    }

    private List<Vector2> union(List<Vector2> numberIndexes, List<Vector2> nonNumberIndexes) {
        List<Vector2> result = new ArrayList<>(numberIndexes);
        result.addAll(nonNumberIndexes);
        return result.stream()
                .sorted()
                .toList();
    }

    private List<Vector2> toNonNumberIndexes(List<Vector2> numberIndexes, String expression) {
        List<Vector2> result = new ArrayList<>();
        for (int i = 0; i < numberIndexes.size() - 1; i++) {
            Vector2 numberLocation = numberIndexes.get(i);
            Vector2 nextNumberLocation = numberIndexes.get(i + 1);
            if (i == 0 && numberLocation.first > 0) {
                result.add(new Vector2(0, numberLocation.first));
                continue;
            }
            if (i == numberIndexes.size() - 1 && numberLocation.second < expression.length()) {
                result.add(new Vector2(numberLocation.second, expression.length()));
                continue;
            } else if (i == numberIndexes.size() - 1) {
                continue;
            }
            result.add(new Vector2(numberLocation.second, nextNumberLocation.first));
        }
        return result;
    }

    private List<Vector2> getNumberIndexes(String expression) {
        List<Vector2> result = new ArrayList<>();
        Matcher matcher = NUMBER_PATTERN.matcher(expression);
        while (matcher.find()) {
            MatchResult matchResult = matcher.toMatchResult();
            result.add(new Vector2(matchResult.start(), matchResult.end()));
        }
        return result;
    }

    private enum Operation {
        MULTIPLY('*'),
        DIVIDE('/'),
        ADD('+'),
        SUBTRACT('-');

        private final char symbol;

        Operation(char symbol) {
            this.symbol = symbol;
        }

        double apply(double leftOperand, double rightOperand) {
            return switch (this) {
                case MULTIPLY -> leftOperand * rightOperand;
                case DIVIDE -> leftOperand / rightOperand;
                case ADD -> leftOperand + rightOperand;
                case SUBTRACT -> leftOperand - rightOperand;
            };
        }

        static Operation parseChar(char symbol) {
            return Arrays.stream(values())
                    .filter(op -> op.symbol == symbol)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Such operation does not exists"));
        }
    }

    private record Vector2(int first, int second) implements Comparable<Vector2> {
        @Override
        public String toString() {
            return "{" + first + ", " + second + "}";
        }

        @Override
        public int compareTo(Vector2 other) {
            return Integer.compare(this.first, other.first);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Vector2 vector2 = (Vector2) o;

            if (first != vector2.first) return false;
            return second == vector2.second;
        }

        @Override
        public int hashCode() {
            int result = first;
            result = 31 * result + second;
            return result;
        }
    }
}
