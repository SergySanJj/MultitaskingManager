package com.university;

import javax.swing.*;

public class FunctionProcess {
    private static int functionCode;
    private static int x;
    private static int port;
    private static double result = -1.0;

    private static FunctionServer functionServer;

    public static void main(String[] args) {
        String message;
        port = Integer.parseInt(args[0]);

        try {
            functionServer = new FunctionServer(port);
            functionCode = functionServer.listenFunctionCode();
            x = functionServer.listenArgument();

            new Thread(() -> {
                try {
                    runFunction();
                } catch (Exception e) {

                }
            }).start();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "error");
            e.printStackTrace();
        }

        functionServer.endWork();
    }

    private static void runFunction() throws Exception {
        result = Functions.run(functionCode, x);
    }
}
