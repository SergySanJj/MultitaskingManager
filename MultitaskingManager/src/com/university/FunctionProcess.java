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
            functionServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        functionServer.endWork();
    }
}
