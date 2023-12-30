package com.kerrrusha.codewars;

public class BattleField {

    static int[][] field;
    static int rows;
    static int cols;

    static int battleships;
    static int cruisers;
    static int destroyers;
    static int submarines;

    static final int REQUIRED_BATTLESHIPS = 1;
    static final int REQUIRED_CRUISERS = 2;
    static final int REQUIRED_DESTROYERS = 3;
    static final int REQUIRED_SUBMARINES = 4;

    public static boolean fieldValidator(int[][] field) {
        initStaticFields(field);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (field[i][j] != 1) {
                    continue;
                }
                if (badCorner(i, j) || badEdge(i, j)) {
                    return false;
                }
            }
        }
        try {
            countShips();
            if (!shipsAmountIsValid()) {
                return false;
            }
        } catch (InvalidShipSizeException e) {
            return false;
        }
        return true;
    }

    private static boolean shipsAmountIsValid() {
        return battleships == REQUIRED_BATTLESHIPS &&
                cruisers == REQUIRED_CRUISERS &&
                destroyers == REQUIRED_DESTROYERS &&
                submarines == REQUIRED_SUBMARINES;
    }

    private static void countShips() {
        final int[][] fieldCopy = new int[rows][cols];
        for(int i = 0; i < rows; i++) {
            fieldCopy[i] = field[i].clone();
        }
        checkVertically(fieldCopy);
        checkHorizontally(fieldCopy);
    }

    private static void checkVertically(int[][] fieldCopy) {
        for (int j = 0; j < cols; j++) {
            for (int i = 0; i < rows; i++) {
                int cells = 0;
                while (fieldCopy[i + cells][j] == 1 && noHorizontalOverlaps(fieldCopy, i + cells, j)) {
                    fieldCopy[i + cells][j] = 0;
                    cells++;
                    if (i + cells >= rows) {
                        break;
                    }
                }
                if (cells == 0) {
                    continue;
                }
                analyzeCellsCount(cells);
                i += cells;
            }
        }
    }

    private static boolean noHorizontalOverlaps(int[][] fieldCopy, int i, int j) {
        if (j - 1 >= 0) {
            if (fieldCopy[i][j - 1] == 1) {
                return false;
            }
        }
        if (j + 1 < cols) {
            if (fieldCopy[i][j + 1] == 1) {
                return false;
            }
        }
        return true;
    }

    private static void checkHorizontally(int[][] fieldCopy) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int cells = 0;
                while (fieldCopy[i][j + cells] == 1 && noVerticalOverlaps(fieldCopy, i, j + cells)) {
                    fieldCopy[i][j + cells] = 0;
                    cells++;
                    if (j + cells >= cols) {
                        break;
                    }
                }
                if (cells == 0) {
                    continue;
                }
                analyzeCellsCount(cells);
                j += cells;
            }
        }
    }

    private static boolean noVerticalOverlaps(int[][] fieldCopy, int i, int j) {
        if (i - 1 >= 0) {
            if (fieldCopy[i - 1][j] == 1) {
                return false;
            }
        }
        if (i + 1 < rows) {
            if (fieldCopy[i + 1][j] == 1) {
                return false;
            }
        }
        return true;
    }

    private static void analyzeCellsCount(int cells) {
        switch (cells) {
            case 1 -> submarines++;
            case 2 -> destroyers++;
            case 3 -> cruisers++;
            case 4 -> battleships++;
            default -> throw new InvalidShipSizeException();
        }
    }

    private static void initStaticFields(int[][] field) {
        BattleField.field = field;
        rows = field.length;
        cols = field[0].length;

        battleships = 0;
        cruisers = 0;
        destroyers = 0;
        submarines = 0;
    }

    static boolean badCorner(int i, int j) {
        if (i - 1 >= 0 && j - 1 >= 0) {
            if (field[i - 1][j - 1] == 1) {
                return true;
            }
        }
        if (i - 1 >= 0 && j + 1 < cols) {
            if (field[i - 1][j + 1] == 1) {
                return true;
            }
        }
        if (i + 1 < rows && j + 1 < cols) {
            if (field[i + 1][j + 1] == 1) {
                return true;
            }
        }
        if (i + 1 < rows && j - 1 >= 0) {
            if (field[i + 1][j - 1] == 1) {
                return true;
            }
        }
        return false;
    }

    static boolean badEdge(int i, int j) {
        boolean hasVerticalOverlap = false;
        boolean hasHorizontalOverlap = false;
        if (i - 1 >= 0) {
            if (field[i - 1][j] == 1) {
                hasVerticalOverlap = true;
            }
        }
        if (j + 1 < cols) {
            if (field[i][j + 1] == 1) {
                hasHorizontalOverlap = true;
            }
        }
        if (i + 1 < rows) {
            if (field[i + 1][j] == 1) {
                hasVerticalOverlap = true;
            }
        }
        if (j - 1 >= 0) {
            if (field[i][j - 1] == 1) {
                hasHorizontalOverlap = true;
            }
        }
        return hasHorizontalOverlap && hasVerticalOverlap;
    }

    private static class InvalidShipSizeException extends RuntimeException {
    }

}