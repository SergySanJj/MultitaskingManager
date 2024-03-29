package com.university.functions.wrappers;

public class HTest implements FunctionManager {

    @Override
    public double run(int x) throws Exception {
        if (x == 0)
            return 2.0;
        if (x == 1)
            return 10.0;
        if (x == 2) {
            Thread.sleep(1000);
            return 10.0;
        }
        if (x == 3) {
            return 1E-14;
        }
        if (x == 4) {
            hang();
            return 1.0;
        }
        if (x == 5) {
            Thread.sleep(1000);
            return 10.0;
        }
        if (x == 6) {
            hang();
            return 1.0;
        }
        if (x == 7) {
            Thread.sleep(1000);
            return 10.0;
        }
        if (x == 8) {
            return 1 / 0.0;
        }
        if (x == 9) {
            throw new Exception();
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
