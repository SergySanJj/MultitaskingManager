package com.university;

import com.university.FunctionManager;

public class MultitaskManager {
    private MultitaskManager() {

    }

    public MultitaskManager(FunctionManager f, FunctionManager g) {
        this.f = f;
        this.g = g;
    }

    public void run(int x) throws Exception {
        f.setParam(x);
        g.setParam(x);
        Double fRes = (Double) f.call();
        Double gRes = (Double) g.call();
        System.out.println(fRes * gRes);
    }

    private FunctionManager<Integer, Double> f;
    private FunctionManager<Integer, Double> g;

}
