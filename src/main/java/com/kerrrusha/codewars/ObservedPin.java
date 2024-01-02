package com.kerrrusha.codewars;

import java.util.*;
import java.util.stream.*;

public class ObservedPin {
    private static final char EMPTY_CHAR = '-';
    private static final char[][] KEYPAD = {
            {'1', '2', '3'},
            {'4', '5', '6'},
            {'7', '8', '9'},
            {EMPTY_CHAR, '0', EMPTY_CHAR}
    };

    public static List<String> getPINs(String observed) {
        //[1, 0]
        char[] observedDigits = observed.toCharArray();
        //[[1, 2, 4], [0, 8]]
        char[][] adjacent = toAdjacent(observedDigits);

        int resultSize = getResultSize(adjacent);
        String[] result = createStringArray(resultSize);
        for (char[] adjacentDigits : adjacent) {
            final int adjacentDigitsAmount = adjacentDigits.length;
            int repeats = resultSize / adjacentDigitsAmount;

            for (int j = 0; j < adjacentDigitsAmount; j++) {
                char adjacentDigit = adjacentDigits[j];
                for (int k = j * repeats; k < j * repeats + repeats; k++) {
                    result[(k) % resultSize] += adjacentDigit;
                }
            }
        }

        return Stream.of(result).toList();
    }

    static String[] createStringArray(int resultSize) {
        String[] result = new String[resultSize];
        for (int i = 0; i < resultSize; i++) {
            result[i] = "";
        }
        return result;
    }

    static char[][] toAdjacent(char[] observedDigits) {
        List<List<Character>> adjacent = new ArrayList<>();
        for (char digit : observedDigits) {
            int row = findRow(digit);
            int col = findCol(digit);
            if (col == -1 || row == -1) {
                throw new RuntimeException("WTF with observed digit '"+digit+"'???");
            }

            List<Character> adjacentDigits = new ArrayList<>();

            adjacentDigits.add(digit);
            adjacentDigits.add(getDigit(row + 1, col));
            adjacentDigits.add(getDigit(row - 1, col));
            adjacentDigits.add(getDigit(row, col + 1));
            adjacentDigits.add(getDigit(row, col - 1));

            adjacentDigits = adjacentDigits.stream()
                    .filter(ch -> !ch.equals(EMPTY_CHAR))
                    .toList();

            adjacent.add(adjacentDigits);
        }
        return toArray(adjacent);
    }

    static char[][] toArray(List<List<Character>> list) {
        char[][] result = new char[list.size()][];
        for (int i = 0; i < list.size(); i++) {
            result[i] = new char[list.get(i).size()];
            for (int j = 0; j < list.get(i).size(); j++) {
                result[i][j] = list.get(i).get(j);
            }
        }
        return result;
    }

    //Character.isDigit(ch1)
    static char getDigit(int row, int col) {
        if (row >= KEYPAD.length || col >= KEYPAD[0].length || row < 0 || col < 0) {
            return EMPTY_CHAR;
        }
        return KEYPAD[row][col];
    }

    static int findRow(char digit) {
        for (int i = 0; i < KEYPAD.length; i++) {
            for (int j = 0; j < KEYPAD[0].length; j++) {
                if (digit == KEYPAD[i][j]) {
                    return i;
                }
            }
        }
        return -1;
    }

    static int findCol(char digit) {
        for (char[] chars : KEYPAD) {
            for (int j = 0; j < KEYPAD[0].length; j++) {
                if (digit == chars[j]) {
                    return j;
                }
            }
        }
        return -1;
    }

    static int getResultSize(char[][] arr) {
        int result = 1;
        for (char[] subArr : arr) {
            result *= subArr.length;
        }
        return result;
    }

}
