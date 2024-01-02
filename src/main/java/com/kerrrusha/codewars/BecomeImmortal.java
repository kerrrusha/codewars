package com.kerrrusha.codewars;

import java.util.ArrayList;
import java.util.List;

public class BecomeImmortal {
    public static long elderAge(long n, long m, long k, long newp) {
        List<List<Long>> magicRectangle = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            List<Long> row = new ArrayList<>();
            for (int j = 0; j < m; j++) {
                long xor = i ^ j;
                long res = xor - k;
                row.add(res < 0 ? 0 : res);
            }
            magicRectangle.add(row);
        }
        return magicRectangle.stream()
                .flatMap(List::stream)
                .mapToLong(Long::longValue)
                .sum() % newp;
    }
}
