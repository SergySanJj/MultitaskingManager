package com.university;

import java.lang.Process;
import java.lang.ProcessBuilder;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;

public class MultitaskManager {

    private int fCode, gCode;
    private double fRes = 0.;
    private double gRes = 0.;
    private Process fProcess, gProcess;

    private MultitaskManager() {
    }

    public MultitaskManager(int fCode, int gCode) {
        this.fCode = fCode;
        this.gCode = gCode;
    }

    public void run(int x) throws Exception {
        MainServer mainServer = new MainServer();

        startProcesses(mainServer.getPort());
        startServer();

        double result = 0;
        System.out.println("result " + Double.toString(result));

        endProcesses();
    }

    public void startServer() throws Exception {
        MainServer mainServer = new MainServer();
        startProcesses(mainServer.getPort());

//        mainServer.acceptSocketChannels();
//        System.out.println("Channels accepted");

        mainServer.manageSelector();


        //mainServer.sendFunctionCodes(0, 1);
        //System.out.println("Args sent");

        while (fProcess.isAlive()) {

        }

        System.out.println();
    }

    private void startProcesses(int port) throws Exception {
        ProcessBuilder fProcessBuilder = createFunctionProcessBuilder(port);
        ProcessBuilder gProcessBuilder = createFunctionProcessBuilder(port);

        fProcess = fProcessBuilder.start();
        gProcess = gProcessBuilder.start();

    }

    private ProcessBuilder createFunctionProcessBuilder(int port) {
        String[] startOptions = new String[]{
                "java",
                "-cp", System.getProperty("java.class.path", "."), FunctionProcess.class.getName(),
                Integer.toString(port), "END"
        };
        return new ProcessBuilder(startOptions);
    }

    private void endProcesses() {
        gProcess.destroy();
        fProcess.destroy();
    }

    public void close() {
        endProcesses();
    }
}
