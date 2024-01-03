package com.kerrrusha.codewars;

public class BecomeImmortal {
    public static long elderAge(long n, long m, long k, long newp) {
        long result = 0;
        while (n > 0) {
            if (n < m) {
                long temp = n;
                n = m;
                m = temp;
            }
            long shift = 1;
            for (; 2 * shift <= n; shift *= 2) {}
            if (m <= shift) {
                result = (result + m % newp * sumRow(shift, k, newp)) % newp;
                k -= shift;
            } else {
                result = (result + shift % newp * sumRow(shift, k, newp)) % newp;
                result = (result + (n + m - 2 * shift) % newp * sumRow(shift, k - shift, newp)) % newp;
                m -= shift;
            }
            n -= shift;
        }
        return result;
    }

    private static long sumRow(long shift, long k, long newp) {
        long n = 0 < k ? 0 : -k;
        shift = shift < k ? 0 : shift - k;
        if ((shift - n) % 2 == 0) {
            return (shift - n) / 2 % newp * ((shift + n - 1) % newp) % newp;
        }
        return (((shift - n - 1) / 2 % newp * ((shift + n) % newp) % newp) + n) % newp;
    }
}
