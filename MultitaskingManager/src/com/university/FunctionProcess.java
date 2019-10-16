package com.university;

public class FunctionProcess {
    private static int port;
    private static FunctionServer functionServer;

    public static void main(String[] args) {
        port = Integer.parseInt(args[0]);

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
