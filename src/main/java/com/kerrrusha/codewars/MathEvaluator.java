package com.kerrrusha.codewars;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MathEvaluator {
    private static final String WHITESPACE = " ";
    private static final char OPENING_PARENTHESE_SYMBOL = '(';
    private static final char CLOSING_PARENTHESE_SYMBOL = ')';
    private static final Pattern NUMBER_PATTERN = Pattern.compile("-?(\\d+(\\.\\d+)?)");
    private static final List<Operation> PRIORITIZED_OPERATIONS = List.of(Operation.MULTIPLY, Operation.DIVIDE);
    
    public double calculate(String expression) {
        List<Object> parsed = parse(expression);

        if (parsed.size() == 1) {
            return (double) parsed.get(0);
        }

        Queue<Integer> prioritizedOperationsIndexes = getPrioritizedOperationsIndexes(parsed);
        while(!prioritizedOperationsIndexes.isEmpty()) {
            int operationIndex = prioritizedOperationsIndexes.poll();
            Operation operation = (Operation) parsed.get(operationIndex);

            double leftOperand = (double) parsed.get(operationIndex - 1);
            double rightOperand = (double) parsed.get(operationIndex + 1);
            double result = operation.apply(leftOperand, rightOperand);

            replace(parsed, operationIndex, result);
            prioritizedOperationsIndexes = getPrioritizedOperationsIndexes(parsed);
        }
        return toResult(parsed);
    }

    private double toResult(List<Object> parsed) {
        if (parsed.size() != 1) {
            throw new RuntimeException("Parsed list must contain only 1 element to extract result, but have: " + parsed.size());
        }
        return (Double) parsed.get(0);
    }

    private void replace(List<Object> parsed, int middleElementIndex, double result) {
        final int elementsToRemove = getElementsToRemove(parsed, middleElementIndex);
        int firstElementIndex = middleElementIndex - elementsToRemove / 2;

        remove(parsed, firstElementIndex, elementsToRemove);
        parsed.add(firstElementIndex, result);
    }

    private void remove(List<Object> parsed, int removeFromIndex, double repeatAmount) {
        for (int i = 0; i < repeatAmount; i++) {
            parsed.remove(removeFromIndex);
        }
    }

    private int getElementsToRemove(List<Object> parsed, int middleElementIndex) {
        final int defaultElementsToRemove = 3;
        final int maxElementsToRemove = 5;

        if (parsed.size() < maxElementsToRemove) {
            return defaultElementsToRemove;
        }
        if (parsed.get(middleElementIndex - 2) instanceof Parenthese) {
            return maxElementsToRemove;
        }
        return defaultElementsToRemove;
    }

    private Queue<Integer> getPrioritizedOperationsIndexes(List<Object> parsed) {
        Deque<Vector2> parentheseGroupsOrder = getParentheseGroupsOrder(parsed);

        Queue<Integer> result = new LinkedList<>();
        while (!parentheseGroupsOrder.isEmpty()) {
            Vector2 parentheseGroup = parentheseGroupsOrder.poll();
            Queue<Integer> operationIndexes = getPrioritizedOperationsIndexesInRange(parsed, parentheseGroup);
            result.addAll(operationIndexes);
        }

        return result;
    }

    private Queue<Integer> getPrioritizedOperationsIndexesInRange(List<Object> parsed, Vector2 parentheseGroup) {
        Queue<Integer> result = new LinkedList<>();

        for (int i = parentheseGroup.first; i < parentheseGroup.second; i++) {
            Object element = parsed.get(i);
            if (element instanceof Operation operation && PRIORITIZED_OPERATIONS.contains(operation)) {
                int operationIndex = parsed.indexOf(element);
                result.add(operationIndex);
            }
        }
        for (int i = parentheseGroup.first; i < parentheseGroup.second; i++) {
            Object element = parsed.get(i);
            if (element instanceof Operation operation && !PRIORITIZED_OPERATIONS.contains(operation)) {
                int operationIndex = parsed.indexOf(element);
                result.add(operationIndex);
            }
        }

        return result;
    }

    private Deque<Vector2> getParentheseGroupsOrder(List<Object> parsed) {
        Deque<Vector2> parentheseGroupsOrder = new LinkedList<>();

        int expectedGroups = getExpectedGroups(parsed);
        int prevParentheseIndex = -1;
        while (parentheseGroupsOrder.size() < expectedGroups) {
            for (int i = 0; i < parsed.size(); i++) {
                if (contains(parentheseGroupsOrder, i)) {
                    continue;
                }
                Object element = parsed.get(i);
                if (!(element instanceof Parenthese parenthese)) {
                    continue;
                }
                if (parenthese.getSymbol() == OPENING_PARENTHESE_SYMBOL) {
                    prevParentheseIndex = parsed.indexOf(parenthese);
                    continue;
                }
                int currentParentheseIndex = parsed.indexOf(parenthese);
                parentheseGroupsOrder.add(new Vector2(prevParentheseIndex, currentParentheseIndex));
                break;
            }
        }

        Queue<Vector2> nonParentheseGroups = getNonParentheseGroups(parentheseGroupsOrder, parsed);
        parentheseGroupsOrder.addAll(nonParentheseGroups);

        return parentheseGroupsOrder;
    }

    private Queue<Vector2> getNonParentheseGroups(Deque<Vector2> parentheseGroupsOrder, List<Object> parsed) {
        Queue<Vector2> result = new LinkedList<>();

        Vector2 lastGroup = parentheseGroupsOrder.getLast();
        if (lastGroup.first > 0) {
            result.add(new Vector2(0, lastGroup.first));
        }
        if (lastGroup.second < parsed.size()) {
            result.add(new Vector2(lastGroup.second, parsed.size()));
        }

        return result;
    }

    private boolean contains(Queue<Vector2> parentheseGroupsOrder, int i) {
        return parentheseGroupsOrder.stream().anyMatch(pair -> pair.first == i || pair.second == i);
    }

    private int getExpectedGroups(List<Object> parsed) {
        return (int) (parsed.stream()
                        .filter(e -> e instanceof Parenthese)
                        .count() / 2);
    }

    private List<Object> parse(String expression) {
        List<Vector2> numberIndexes = getNumberIndexes(expression);
        List<Vector2> nonNumberIndexes = toNonNumberIndexes(numberIndexes, expression);
        List<Vector2> allElementIndexes = union(numberIndexes, nonNumberIndexes);

        List<Object> result = new ArrayList<>();
        for (Vector2 location : allElementIndexes) {
            String str = expression.substring(location.first, location.second);
            if (numberIndexes.contains(location)) {
                double parsed = Double.parseDouble(str);
                result.add(parsed);
                continue;
            }
            if (nonNumberIndexes.contains(location)) {
                String raw = str.replace(WHITESPACE, "");
                result.addAll(tryParseNonNumberElements(raw));
            }
        }
        return result;
    }

    private List<Object> tryParseNonNumberElements(String raw) {
        return Arrays.stream(raw.replace(WHITESPACE, "").chars().toArray())
                .mapToObj(e -> (char) e)
                .map(ch -> Operation.validChar(ch) ? Operation.parseChar(ch) : Parenthese.parse(ch))
                .toList();
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
                result.add(new Vector2(numberLocation.second, nextNumberLocation.first));
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

    private interface Parenthese {
        char getSymbol();
        
        static Parenthese parse(char symbol) {
            return switch (symbol) {
                case OPENING_PARENTHESE_SYMBOL -> new OpeningParenthese();
                case CLOSING_PARENTHESE_SYMBOL -> new ClosingParenthese();
                default -> throw new IllegalStateException("Unexpected value: " + symbol);
            };
        }
    }

    private static class OpeningParenthese implements Parenthese {
        @Override
        public char getSymbol() {
            return OPENING_PARENTHESE_SYMBOL;
        }

        @Override
        public String toString() {
            return "" + getSymbol();
        }
    }

    private static class ClosingParenthese implements Parenthese {
        @Override
        public char getSymbol() {
            return CLOSING_PARENTHESE_SYMBOL;
        }

        @Override
        public String toString() {
            return "" + getSymbol();
        }
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

        static boolean validChar(char charToCheck) {
            return Arrays.stream(values())
                    .map(e -> e.symbol)
                    .filter(ch -> ch == charToCheck)
                    .count() == 1;
        }

        static Operation parseChar(char symbol) {
            return Arrays.stream(values())
                    .filter(e -> e.symbol == symbol)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Such Operation does not exists: " + symbol));
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
