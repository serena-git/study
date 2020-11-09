package utils;

import java.util.Random;

public class Timers {
    private static final Random random = new Random();
    public static void delay() {
        int delay = 500 + random.nextInt(2000); // 0.5 ~ 2.5 sec.
        try {
            Thread.sleep(1000); // block for 1sec.
        } catch (InterruptedException exception) {
            throw new RuntimeException(exception);
        }
    }
}
