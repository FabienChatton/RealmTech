package ch.realmtech.benchmark;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;

public class OptionalTest {
    private final static int MAGIC_NUMBER = 727;

    @AfterEach
    void after() {
        System.gc();
    }

    @Test
    void nullCheck() {
        Random random = new Random(0);
        long t1 = System.currentTimeMillis();
        int n = 0;
        for (int i = 0; i < 1_000_000; i++) {
            int j;
            if (random.nextBoolean()) {
                j = MAGIC_NUMBER;
            } else {
                j = 0;
            }
            if (j == MAGIC_NUMBER) {
                n++;
            }
        }
        System.out.println(n);
        long t2 = System.currentTimeMillis();
        System.out.println("null check " + (t2 - t1));
    }

    @Test
    void Optional() {
        Random random = new Random(0);
        long t1 = System.currentTimeMillis();
        int n = 0;
        for (int i = 0; i < 1_000_000; i++) {
            Optional<Integer> j;
            if (random.nextBoolean()) {
                j = Optional.of(MAGIC_NUMBER);
            } else {
                j = Optional.empty();
            }
            if (j.isPresent()) {
                n++;
            }
        }
        System.out.println(n);
        long t2 = System.currentTimeMillis();
        System.out.println("Optional " + (t2 - t1));
    }

    @Test
    void OptionalInt() {
        Random random = new Random(0);
        long t1 = System.currentTimeMillis();
        int n = 0;
        for (int i = 0; i < 1_000_000; i++) {
            OptionalInt j;
            if (random.nextBoolean()) {
                j = OptionalInt.of(MAGIC_NUMBER);
            } else {
                j = OptionalInt.empty();
            }
            if (j.isPresent()) {
                n++;
            }
        }
        System.out.println(n);
        long t2 = System.currentTimeMillis();
        System.out.println("OptionalInt " + (t2 - t1));
    }

    @Test
    void tryCatch() {
        Random random = new Random(0);
        long t1 = System.currentTimeMillis();
        int n = 0;
        for (int i = 0; i < 1_000_000; i++) {
            int j;
            try {
                if (random.nextBoolean()) {
                    j = MAGIC_NUMBER;
                } else {
                    throw new Exception();
                }
                n++;
            } catch (Exception e) {

            }
        }
        System.out.println(n);
        long t2 = System.currentTimeMillis();
        System.out.println("try catch " + (t2 - t1));
    }
}
