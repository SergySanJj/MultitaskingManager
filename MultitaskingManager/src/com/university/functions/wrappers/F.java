package com.university.functions.wrappers;

import spos.lab1.demo.DoubleOps;

public class F implements FunctionManager {
    public double run(int x) throws Exception{
        return DoubleOps.funcF(x);
    }
}

