package com.university.functionclient;

public class FunctionProcess {
    private static FunctionServer functionServer;

    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> System.exit(0)));

        try {
            functionServer = new FunctionServer(port);
            functionServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        functionServer.endWork();
    }
}
