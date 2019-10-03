package com.university.functions.wrappers;

import spos.lab1.demo.DoubleOps;

public class G implements FunctionManager {
    public double run(int x) throws Exception{
        return DoubleOps.funcG(x);
    }
}
