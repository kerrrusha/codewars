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

import static java.lang.Math.min;

public class MathEvaluator {
    private static final String WHITESPACE = " ";
    private static final char OPENING_PARENTHESE_SYMBOL = '(';
    private static final char CLOSING_PARENTHESE_SYMBOL = ')';
    private static final char MULTIPLY_SYMBOL = '*';
    private static final char DIVIDE_SYMBOL = '/';
    private static final char ADD_SYMBOL = '+';
    private static final char SUBTRACT_SYMBOL = '-';
    private static final Pattern NUMBER_PATTERN = Pattern.compile("-?(\\d+(\\.\\d+)?)");
    private static final List<Character> PRIORITIZED_OPERATIONS = List.of(MULTIPLY_SYMBOL, DIVIDE_SYMBOL);
    
    public double calculate(String expression) {
        List<Object> parsed = parse(expression);

        if (parsed.size() == 1) {
            return (double) parsed.get(0);
        }

        Queue<Integer> prioritizedOperationsIndexes = getPrioritizedOperationsIndexes(parsed);
        while (!prioritizedOperationsIndexes.isEmpty()) {
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
            throw new IllegalStateException("Parsed list must contain only 1 element to extract result, but have: " + parsed.size());
        }
        return (Double) parsed.get(0);
    }

    private void replace(List<Object> parsed, int middleElementIndex, double result) {
        final int elementsToRemove = getElementsToRemove(parsed, middleElementIndex);
        int firstElementIndex = middleElementIndex - elementsToRemove / 2;

        remove(parsed, firstElementIndex, elementsToRemove);
        parsed.add(firstElementIndex, result);

        fixUnwiredOperations(parsed);
    }

    private void remove(List<Object> parsed, int removeFromIndex, double repeatAmount) {
        for (int i = 0; i < repeatAmount; i++) {
            parsed.remove(removeFromIndex);
        }
    }

    private int getElementsToRemove(List<Object> parsed, int middleElementIndex) {
        final int defaultElementsToRemove = 3;

        if (middleElementIndex - 2 < 0 || middleElementIndex + 2 >= parsed.size()) {
            return defaultElementsToRemove;
        }
        if (parsed.get(middleElementIndex - 2) instanceof OpeningParenthese
                && parsed.get(middleElementIndex + 2) instanceof ClosingParenthese) {
            int paranthesesOnTheLeft = 1;
            for (int i = middleElementIndex - 3; i >= 0; i--) {
                if (parsed.get(i) instanceof OpeningParenthese) {
                    paranthesesOnTheLeft++;
                    continue;
                }
                break;
            }

            int paranthesesOnTheRight = 1;
            for (int i = middleElementIndex + 3; i < parsed.size(); i++) {
                if (parsed.get(i) instanceof ClosingParenthese) {
                    paranthesesOnTheRight++;
                    continue;
                }
                break;
            }

            return 2 * min(paranthesesOnTheLeft, paranthesesOnTheRight) + defaultElementsToRemove;
        }
        return defaultElementsToRemove;
    }

    private Queue<Integer> getPrioritizedOperationsIndexes(List<Object> parsed) {
        Deque<Vector2> parentheseGroupsOrder = getParentheseGroupsOrder(parsed);

        Queue<Integer> result = new LinkedList<>();
        while (!parentheseGroupsOrder.isEmpty()) {
            Vector2 parentheseGroup = parentheseGroupsOrder.poll();
            Queue<Integer> operationIndexes = getPrioritizedOperationsIndexesInRange(parsed, parentheseGroup);
            operationIndexes.forEach(index -> {
                if (!result.contains(index)){
                    result.add(index);
                }
            });
        }

        return result;
    }

    private Queue<Integer> getPrioritizedOperationsIndexesInRange(List<Object> parsed, Vector2 parentheseGroup) {
        Queue<Integer> result = new LinkedList<>();

        for (int i = parentheseGroup.first; i < parentheseGroup.second; i++) {
            Object element = parsed.get(i);
            if (element instanceof Operation operation && PRIORITIZED_OPERATIONS.contains(operation.getSymbol())) {
                int operationIndex = parsed.indexOf(element);
                result.add(operationIndex);
            }
        }
        for (int i = parentheseGroup.first; i < parentheseGroup.second; i++) {
            Object element = parsed.get(i);
            if (element instanceof Operation operation && !PRIORITIZED_OPERATIONS.contains(operation.getSymbol())) {
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

        if (parentheseGroupsOrder.isEmpty()) {
            result.add(new Vector2(0, parsed.size()));
            return result;
        }

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

        fixUnwiredOperations(result);
        return result;
    }

    private void fixUnwiredOperations(List<Object> parsed) {
        for (int i = 0; i < parsed.size() - 1; i++) {
            if (parsed.get(i) instanceof OpeningParenthese && parsed.get(i + 1) instanceof SubtractOperation) {
                parsed.set(i + 1, -1d);
                parsed.add(i + 2, new MultiplyOperation());
            }
        }
        for (int i = 0; i < parsed.size() - 1; i++) {
            if (parsed.get(i) instanceof Double && parsed.get(i + 1) instanceof Double) {
                parsed.add(i + 1, new AddOperation());
            }
        }

        List<Integer> indexesToRemove = new ArrayList<>();
        for (int i = 0; i < parsed.size() - 2; i++) {
            Object first = parsed.get(i);
            Object second = parsed.get(i + 1);
            Object third = parsed.get(i + 2);
            if (first instanceof OpeningParenthese
                    && second instanceof Double
                    && third instanceof ClosingParenthese) {
                indexesToRemove.add(i);
                indexesToRemove.add(i + 2);
            }
        }
        for (int i = 0; i < parsed.size() - 2; i++) {
            Object first = parsed.get(i);
            Object second = parsed.get(i + 1);
            Object third = parsed.get(i + 2);
            if (first instanceof Operation
                    && second instanceof SubtractOperation
                    && third instanceof Double) {
                indexesToRemove.add(i + 1);
                parsed.set(i + 2, -1 * (Double) parsed.get(i + 2));
            }
        }
        for (int i = 0; i < parsed.size() - 1; i++) {
            Object first = parsed.get(i);
            Object second = parsed.get(i + 1);
            if (first instanceof OpeningParenthese
                    && second instanceof ClosingParenthese) {
                indexesToRemove.add(i);
                indexesToRemove.add(i + 1);
            }
        }

        List<Object> result = new ArrayList<>();
        for (int i = 0; i < parsed.size(); i++) {
            if (indexesToRemove.contains(i)) {
                continue;
            }
            result.add(parsed.get(i));
        }

        parsed.clear();
        parsed.addAll(result);

        indexesToRemove = new ArrayList<>();
        for (int i = 0; i < parsed.size() - 1; i++) {
            Object first = parsed.get(i);
            Object second = parsed.get(i + 1);
            if (first instanceof SubtractOperation
                    && second instanceof SubtractOperation) {
                indexesToRemove.add(i);
                parsed.set(i + 1, new AddOperation());
            }
        }

        result = new ArrayList<>();
        for (int i = 0; i < parsed.size(); i++) {
            if (indexesToRemove.contains(i)) {
                continue;
            }
            result.add(parsed.get(i));
        }

        parsed.clear();
        parsed.addAll(result);
    }

    private List<Object> tryParseNonNumberElements(String raw) {
        return Arrays.stream(raw.replace(WHITESPACE, "").chars().toArray())
                .mapToObj(e -> (char) e)
                .map(ch -> Operation.validChar(ch) ? Operation.parse(ch) : Parenthese.parse(ch))
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
        for (int i = 0; i < numberIndexes.size() + 1; i++) {
            Vector2 prev = i - 1 >= 0 ? numberIndexes.get(i - 1) : null;
            Vector2 next = i < numberIndexes.size() ? numberIndexes.get(i) : null;

            int from = prev == null ? 0 : prev.second;
            int to = next == null ? expression.length() : next.first;
            result.add(new Vector2(from, to));
        }
        return result.stream().distinct().toList();
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

    private interface Operation {
        char getSymbol();

        double apply(double leftOperand, double rightOperand);

        static boolean validChar(char charToCheck) {
            return List.of(
                    MULTIPLY_SYMBOL,
                    DIVIDE_SYMBOL,
                    ADD_SYMBOL,
                    SUBTRACT_SYMBOL
            ).contains(charToCheck);
        }

        static Operation parse(char symbol) {
            return switch (symbol) {
                case MULTIPLY_SYMBOL -> new MultiplyOperation();
                case DIVIDE_SYMBOL -> new DivideOperation();
                case ADD_SYMBOL -> new AddOperation();
                case SUBTRACT_SYMBOL -> new SubtractOperation();
                default -> throw new IllegalStateException("Unexpected value: " + symbol);
            };
        }
    }

    private static class MultiplyOperation implements Operation {
        @Override
        public char getSymbol() {
            return MULTIPLY_SYMBOL;
        }

        @Override
        public double apply(double leftOperand, double rightOperand) {
            return leftOperand * rightOperand;
        }

        @Override
        public String toString() {
            return "" + getSymbol();
        }
    }

    private static class DivideOperation implements Operation {
        @Override
        public char getSymbol() {
            return DIVIDE_SYMBOL;
        }

        @Override
        public double apply(double leftOperand, double rightOperand) {
            return leftOperand / rightOperand;
        }

        @Override
        public String toString() {
            return "" + getSymbol();
        }
    }

    private static class AddOperation implements Operation {
        @Override
        public char getSymbol() {
            return ADD_SYMBOL;
        }

        @Override
        public double apply(double leftOperand, double rightOperand) {
            return leftOperand + rightOperand;
        }

        @Override
        public String toString() {
            return "" + getSymbol();
        }
    }

    private static class SubtractOperation implements Operation {
        @Override
        public char getSymbol() {
            return SUBTRACT_SYMBOL;
        }

        @Override
        public double apply(double leftOperand, double rightOperand) {
            return leftOperand - rightOperand;
        }

        @Override
        public String toString() {
            return "" + getSymbol();
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
