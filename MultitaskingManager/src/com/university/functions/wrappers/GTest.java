package com.university.functions.wrappers;

public class GTest implements FunctionManager {

    @Override
    public double run(int x) throws Exception {
        if (x == 0)
            return 2.0;
        if (x == 1)
            return 0.0;
        if (x == 2) {
            Thread.sleep(1000);
            return 2.0;
        }
        if (x == 3) {
            Thread.sleep(2000);
            return 0.0;
        }
        if (x == 4) {
            return 0.0;
        }
        if (x == 5) {
            return 0.0;
        }
        return 1.0;
    }
}
