package com.university.functions.wrappers;

import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class Functions {

    private Functions() {

    }

    static {
        functions = new TreeMap<>();
        addFunction("f", new F());
        addFunction("g", new G());
        addFunction("h", new HTest());
        addFunction("r", new RTest());
    }

    private static Map<String, FunctionManager> functions;

    public static void addFunction(String functionCode, FunctionManager f) {
        try {
            functions.put(functionCode, f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static double run(String functionCode, int x) throws Exception {
        return getFunctionManager(functionCode).run(x);
    }

    public static FunctionManager getFunctionManager(String functionCode) {
        return functions.get(functionCode);
    }

    public static String duplicateFunction(String functionCode) {
        String duplicateFunctionName = functionCode + "_copy";
        addFunction(duplicateFunctionName, getFunctionManager(functionCode));

        return duplicateFunctionName;
    }

    public static void printAvailableFunctions() {
        for (Map.Entry<String, FunctionManager> f : functions.entrySet()) {
            System.out.print(f.getKey() + " ");
        }
        System.out.println();
    }
}
