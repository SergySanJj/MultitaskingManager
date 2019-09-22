package com.university;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.IntFunction;

public class FunctionManager<T, R> implements Callable<Double> {
    public FunctionManager(Function<Integer, Double> func) throws Exception{
        this.func = func;
    }

    void setParam(int x) {
        this.x = x;
    }

    @Override
    public Double call() {
        try {
            Double res = func.apply(x);
            return res;
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
            return -1.0;
        }
    }

    private Integer x;
    private Function<Integer, Double> func;
}
