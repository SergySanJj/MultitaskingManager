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
    UserInterface parentUI;

    private MultitaskManager() {
    }

    public MultitaskManager(UserInterface parentUI, int fCode, int gCode) {
        this.fCode = fCode;
        this.gCode = gCode;
        this.parentUI = parentUI;
        results = new double[]{0.0, 0.0};
    }

    public void setFunctionResult(int functionNumber, String result) {
        String[] fRes = StrFunc.parseNumValues(result);
        if (fRes[0].equals("1")) {
            results[functionNumber] = Integer.parseInt(fRes[1]);
            if (Math.abs(results[functionNumber]) < Double.MIN_VALUE) {
                parentUI.pollZero();
            }
        }
        if (checkResultReadines()) {
            parentUI.pollResult(operationRes());
        }
    }

    private boolean checkResultReadines() {
        for (double el : results) {
            if (el == 0.0) {
                return false;
            }
        }
        return true;
    }

    private double operationRes() {
        double res = 1;
        for (double el : results)
            res *= el;
        return res;
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
                "cmd", "/c", "start", "cmd", "/k",
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
