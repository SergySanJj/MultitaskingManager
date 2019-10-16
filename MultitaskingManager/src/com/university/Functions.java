package com.university;

import com.university.functions.wrappers.*;

import java.util.ArrayList;

public class Functions {

    private Functions() {

    }

    static {
        functions = new ArrayList<>();
        addFunction(new F());
        addFunction(new G());
        addFunction(new FTest());
        addFunction(new GTest());
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
