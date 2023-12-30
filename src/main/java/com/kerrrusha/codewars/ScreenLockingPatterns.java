package com.kerrrusha.codewars;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ScreenLockingPatterns {
    private static final int ROWS = 3;
    private static final int COLS = 3;
    private static final char[][] LOCK_PATTERN = new char[][] {
            {'A', 'B', 'C'},
            {'D', 'E', 'F'},
            {'G', 'H', 'I'},
    };

    public int calculateCombinations(char startPosition, int patternLength) {
        if (patternLength <= 0 || patternLength > 9) {
            return 0;
        }
        if (patternLength == 1) {
            return 1;
        }

        Set<Pattern> allPossiblePatterns = Set.of(createStartPattern(startPosition, patternLength));
        for (int i = 1; i < patternLength; i++) {
            allPossiblePatterns = calculateNextStep(allPossiblePatterns);
        }

        return allPossiblePatterns.size();
    }

    private Pattern createStartPattern(char startPosition, int patternLength) {
        Pattern startPattern = new Pattern(patternLength);
        startPattern.add(getPositionByLetter(startPosition));
        return startPattern;
    }

    private Set<Pattern> calculateNextStep(Set<Pattern> allPossiblePatterns) {
        Set<Pattern> result = new HashSet<>();

        for (Pattern pattern : allPossiblePatterns) {
            List<Vector2> nextPositions = getNextPositions(pattern);
            Set<Pattern> continuedPatterns = new HashSet<>();
            for (Vector2 newPosition : nextPositions) {
                Pattern continuedPattern = pattern.clone();
                continuedPattern.add(newPosition);
                continuedPatterns.add(continuedPattern);
            }
            result.addAll(continuedPatterns);
        }

        return result;
    }

    private List<Vector2> getNextPositions(Pattern pattern) {
        List<Vector2> result = new ArrayList<>();

        result.addAll(getNeighboursStraightPositions(pattern));
        result.addAll(getNeighboursDiagonalPositions(pattern));
        result.addAll(getOverpointPositions(pattern));

        return result;
    }

    private List<Vector2> getNeighboursStraightPositions(Pattern pattern) {
        Vector2 currentPosition = pattern.getLastPosition();
        return Stream.of(
                new Vector2(currentPosition.first, currentPosition.second + 1),
                new Vector2(currentPosition.first, currentPosition.second - 1),
                new Vector2(currentPosition.first + 1, currentPosition.second),
                new Vector2(currentPosition.first - 1, currentPosition.second)
        )
                .filter(this::positionIsValid)
                .filter(ij -> positionIsNotUsed(ij, pattern))
                .collect(Collectors.toList());
    }

    private List<Vector2> getNeighboursDiagonalPositions(Pattern pattern) {
        Vector2 currentPosition = pattern.getLastPosition();
        return Stream.of(
                        new Vector2(currentPosition.first + 1, currentPosition.second + 1),
                        new Vector2(currentPosition.first + 1, currentPosition.second - 1),
                        new Vector2(currentPosition.first - 1, currentPosition.second + 1),
                        new Vector2(currentPosition.first - 1, currentPosition.second - 1)
                )
                .filter(this::positionIsValid)
                .filter(ij -> positionIsNotUsed(ij, pattern))
                .collect(Collectors.toList());
    }

    private List<Vector2> getOverpointPositions(Pattern pattern) {
        Vector2 currentPosition = pattern.getLastPosition();
        return Stream.of(
                        new Vector2(currentPosition.first + 1, currentPosition.second + 2),
                        new Vector2(currentPosition.first + 1, currentPosition.second - 2),
                        new Vector2(currentPosition.first - 1, currentPosition.second + 2),
                        new Vector2(currentPosition.first - 1, currentPosition.second - 2),
                        new Vector2(currentPosition.first + 2, currentPosition.second + 1),
                        new Vector2(currentPosition.first + 2, currentPosition.second - 1),
                        new Vector2(currentPosition.first - 2, currentPosition.second + 1),
                        new Vector2(currentPosition.first - 2, currentPosition.second - 1)
                )
                .filter(this::positionIsValid)
                .filter(ij -> positionIsNotUsed(ij, pattern))
                .collect(Collectors.toList());
    }

    private boolean positionIsValid(Vector2 ij) {
        return ij.first >= 0 && ij.first < ROWS
                && ij.second >= 0 && ij.second < COLS;
    }

    private boolean positionIsNotUsed(Vector2 ij, Pattern pattern) {
        for (int i = 0; i < pattern.size(); i++) {
            if (pattern.get(i).equals(ij)) {
                return false;
            }
        }
        return true;
    }

    private Vector2 getPositionByLetter(char letter) {
        for (int i = 0; i < LOCK_PATTERN.length; i++) {
            for (int j = 0; j < LOCK_PATTERN[i].length; j++) {
                if (LOCK_PATTERN[i][j] == letter) {
                    return new Vector2(i, j);
                }
            }
        }
        throw new RuntimeException("Can't find letter in lock pattern: " + letter);
    }

    private static class Vector2 {
        private final int first;
        private final int second;

        private Vector2(int first, int second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public String toString() {
            return "{" + first + ", " + second + "}";
        }
    }

    private static class Pattern implements Cloneable {
        private final Vector2[] pattern;

        private Pattern(int size) {
            pattern = new Vector2[size];
        }

        private Pattern(Pattern other) {
            this.pattern = Arrays.copyOf(other.pattern, other.pattern.length);
        }

        private void add(Vector2 element) {
            if (size() >= pattern.length) {
                throw new RuntimeException("Pattern is full already");
            }
            pattern[size()] = element;
        }

        private Vector2 get(int index) {
            return pattern[index];
        }

        private Vector2 getLastPosition() {
            if (size() == 0) {
                throw new RuntimeException("Pattern is empty");
            }
            return pattern[size() - 1];
        }

        private int size() {
            int result = 0;
            for (Vector2 vector2 : pattern) {
                if (vector2 == null) {
                    break;
                }
                result++;
            }
            return result;
        }

        @Override
        public Pattern clone() {
            return new Pattern(this);
        }

        @Override
        public String toString() {
            return Arrays.toString(pattern);
        }
    }
}
