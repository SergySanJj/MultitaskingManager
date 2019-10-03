package com.university.functions.wrappers;

public class FTest implements FunctionManager {

    @Override
    public double run(int x) throws Exception {
        if (x == 0)
            return 2.0;
        if (x == 1)
            return 0.0;
        if (x == 2) {
            Thread.sleep(2000);
            return 2.0;
        }
        if (x == 3) {
            Thread.sleep(1000);
            return 0.0;
        }
        if (x == 4) {
            int i = 0;
            while (i > -1) {
                i = (i + 1) % 10;
            }
            return 1.0;
        }
        if (x == 5) {
            throw new Exception();
        }
        return 1.0;
    }
}
