package com.university;

import spos.lab1.demo.DoubleOps;

import com.university.F;
import com.university.G;
import com.university.FunctionManager;

import java.util.Arrays;
import java.util.ArrayList;

public class Functions {

    private Functions() {

    }

    static {
        functions = new ArrayList<FunctionManager>();
        addFunction(new F());
        addFunction(new G());
    }

    private static ArrayList<FunctionManager> functions;

    public static void addFunction(FunctionManager f) {
        try {
            functions.add(f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static double run(int functionCode, int x) throws Exception {
        return functions.get(functionCode).run(x);
    }


}
