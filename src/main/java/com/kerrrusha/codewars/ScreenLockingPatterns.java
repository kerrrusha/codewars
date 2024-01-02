package com.kerrrusha.codewars;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class ScreenLockingPatterns {
    private static final int ROWS = 3;
    private static final int COLS = 3;
    private static final char[][] LOCK_PATTERN = new char[][] {
            {'A', 'B', 'C'},
            {'D', 'E', 'F'},
            {'G', 'H', 'I'},
    };
    private static final List<Vector2> STRAIGHT_DELTAS = List.of(
            new Vector2(0, 1),
            new Vector2(0, -1),
            new Vector2(1, 0),
            new Vector2(-1, 0)
    );
    private static final List<Vector2> DIAGONAL_DELTAS = List.of(
            new Vector2(1, 1),
            new Vector2(1, -1),
            new Vector2(-1, 1),
            new Vector2(-1, -1)
    );

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
        return STRAIGHT_DELTAS.stream()
                .filter(delta -> positionIsValid(applyDeltaToPosition(currentPosition, delta)))
                .map(delta -> {
                    Vector2 closestPosition = applyDeltaToPosition(currentPosition, delta);
                    return positionIsNotUsed(closestPosition, pattern)
                            ? closestPosition
                            : applyDeltaToPosition(currentPosition, delta.createDoubled());
                })
                .filter(this::positionIsValid)
                .filter(position -> positionIsNotUsed(position, pattern))
                .collect(toList());
    }

    private Vector2 applyDeltaToPosition(Vector2 position, Vector2 delta) {
        return new Vector2(position.first + delta.first, position.second + delta.second);
    }

    private List<Vector2> getNeighboursDiagonalPositions(Pattern pattern) {
        Vector2 currentPosition = pattern.getLastPosition();
        return DIAGONAL_DELTAS.stream()
                .map(delta -> isCornerPosition(currentPosition) && centralPositionIsUsed(pattern) ? delta.createDoubled() : delta)
                .map(delta -> applyDeltaToPosition(currentPosition, delta))
                .filter(this::positionIsValid)
                .filter(ij -> positionIsNotUsed(ij, pattern))
                .collect(toList());
    }

    private boolean isCornerPosition(Vector2 position) {
        return position.first == 0 && position.second == 0 ||
                position.first == ROWS - 1 && position.second == 0 ||
                position.first == 0 && position.second == COLS - 1 ||
                position.first == ROWS - 1 && position.second == COLS - 1;
    }

    private boolean centralPositionIsUsed(Pattern pattern) {
        return pattern.contains(new Vector2(ROWS / 2, COLS / 2));
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
                .collect(toList());
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

    private record Vector2(int first, int second) {
        private Vector2 createDoubled() {
            return new Vector2(first * 2, second * 2);
        }

        @Override
        public String toString() {
            return "{" + first + ", " + second + "}";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Vector2 vector2 = (Vector2) o;

            if (first != vector2.first) return false;
            return second == vector2.second;
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

        private boolean contains(Vector2 position) {
            for (Vector2 patternPosition : pattern) {
                if (patternPosition.equals(position)) {
                    return true;
                }
            }
            return false;
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Pattern pattern1 = (Pattern) o;

            return Arrays.equals(pattern, pattern1.pattern);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(pattern);
        }
    }
}
