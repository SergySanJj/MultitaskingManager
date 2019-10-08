package com.university;

import java.lang.Process;
import java.lang.ProcessBuilder;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;

public class MultitaskManager {

    private int fCode, gCode;
    private double[] results;
    private Process fProcess, gProcess;
    MainServer mainServer;

    private MultitaskManager() {
    }

    public MultitaskManager(int fCode, int gCode) {
        this.fCode = fCode;
        this.gCode = gCode;
        results = new double[2];
    }

    public void setFunctionResult(int functionNumber, double result) {
        results[functionNumber] = result;
    }

    public void run(int x) throws Exception {
        mainServer = new MainServer(this, x);

        startProcesses(mainServer.getPort());
        startServer();

        double result = 0;
        System.out.println("result " + Double.toString(result));

        endProcesses();
    }

    private void startServer() throws Exception {
        startProcesses(mainServer.getPort());
        mainServer.manageSelector();
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
