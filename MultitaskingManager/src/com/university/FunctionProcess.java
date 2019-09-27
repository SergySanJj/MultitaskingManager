package com.university;

public class FunctionProcess {
    public static void main(String[] args) {
        String message;
        port = Integer.parseInt(args[0]);

        try {
            functionServer = new FunctionServer(port);
            functionCode = functionServer.listenFunctionCode();
            x = functionServer.listenArgument();

            result = Functions.run(functionCode, x);
        } catch (Exception e) {
            e.printStackTrace();
        }

        functionServer.endWork();
    }

    private static void runFunction() throws Exception {
        result = Functions.run(functionCode, x);
    }


    private static int functionCode;
    private static int x;
    private static int port;
    private static double result = -1.0;


    private static FunctionServer functionServer;
}
