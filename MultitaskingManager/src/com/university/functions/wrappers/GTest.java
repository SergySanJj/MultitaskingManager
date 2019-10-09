package com.university.functions.wrappers;

public class GTest implements FunctionManager {

    @Override
    public double run(int x) throws Exception {
        if (x == 0)
            return 2.5;
        if (x == 1) {
            Thread.sleep(1000);
            return 11.0;
        }
        if (x == 2) {
            return 11.0;
        }
        if (x == 3) {
            hang();
            return 1.0;
        }
        if (x == 4) {
            Thread.sleep(1000);
            return 0.0;
        }
        if (x == 5) {
            hang();
            return 0.0;
        }
        if (x == 6) {
            throw new Exception();
        }
        if (x == 7) {
            Thread.sleep(10000);
            return 11.0;
        }
        return 1.0;
    }

    private void hang() {
        int i = 0;
        while (i > -1) {
            i = (i + 1) % 10;
        }
    }
}
