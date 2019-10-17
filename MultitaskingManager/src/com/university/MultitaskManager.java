package com.university;

import java.util.ArrayList;
import java.util.Arrays;

public class MultitaskManager {

    private String fCode, gCode;
    private ArrayList<String> results;
    private Process fProcess, gProcess;
    private MainServer mainServer;
    private UserInterface parentUI;
    private long time;

    private Thread serverThread;

    public MultitaskManager(UserInterface parentUI, String fCode, String gCode) {
        this.fCode = fCode;
        this.gCode = gCode;
        this.parentUI = parentUI;
        results = new ArrayList<>();
    }

    public void setFunctionResult(String functionCode, String result) {
        String[] fRes = StrFunc.parseNumValues(result);
        if (fRes[0].equals("1")) {
            double res = Double.parseDouble(fRes[1]);
            if (Settings.echo)
                System.out.println(fRes[1]);
            results.add((Double.toString(res)));
            if (Math.abs(res) < 1E-12) {
                parentUI.pollZero();
            }
        } else {
            results.add("NaN");
        }
        if (checkResultReadines()) {
            parentUI.pollResult(operationRes());
        }
    }

    private boolean checkResultReadines() {
        return results.size() == 2;
    }

    private String operationRes() {
        double res = 1;
        boolean hasNaN = false;
        for (String el : results) {
            if (el.equals("NaN"))
                hasNaN = true;
            else
                res *= Double.parseDouble(el);
        }
        if (Math.abs(res) > 1E-13 && hasNaN)
            return "UNDEFINED";
        return Double.toString(res);
    }

    public void run(int x) throws Exception {
        mainServer = new MainServer(this, fCode, gCode, x);

        time = System.nanoTime();
        startProcesses(mainServer.getPort());
        startServer();
        while (serverThread.isAlive()) {

        }
    }

    private void startServer() throws Exception {
        serverThread = new Thread(() -> {
            try {
                mainServer.manageSelector();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        serverThread.start();
    }

    private void startProcesses(int port) throws Exception {
        ProcessBuilder fProcessBuilder = createFunctionProcessBuilder(port);
        ProcessBuilder gProcessBuilder = createFunctionProcessBuilder(port);

        fProcess = fProcessBuilder.start();
        gProcess = gProcessBuilder.start();
    }

    private ProcessBuilder createFunctionProcessBuilder(int port) {
        ArrayList<String> startOptions = new ArrayList<>();
        if (Settings.echo)
            startOptions.addAll(Arrays.asList("cmd", "/c", "start", "cmd", "/k"));

        startOptions.addAll(Arrays.asList("java",
                "-cp", System.getProperty("java.class.path", "."), FunctionProcess.class.getName(),
                Integer.toString(port), "END"));

        return new ProcessBuilder(startOptions);
    }

    private void endProcesses() {
        killProcess(fProcess);
        killProcess(gProcess);
    }

    private void killProcess(Process process) {
        if (process == null)
            return;
        if (process.isAlive()) {
            process.destroyForcibly();
        }
    }

    public void close() {
        endProcesses();
        if (serverThread != null)
            serverThread.interrupt();
        Thread.currentThread().interrupt();
    }

    public String getStatus() {
        StringBuilder s = new StringBuilder();
        for (String el : results)
            s.append(el).append(" ");
        return results.size() + " Functions finished executuon with values: " + s.toString();
    }

    public long time() {
        return this.time;
    }

    public void clearResults(){
        results.clear();
    }
}
