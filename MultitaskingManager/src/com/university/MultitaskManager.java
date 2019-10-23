package com.university;

import javafx.util.Pair;

import java.util.*;

public class MultitaskManager {

    private boolean finished = false;
    private double operationResult;

    private String fCode, gCode;
    private Map<String, String> results;
    private Process fProcess, gProcess;
    private MainServer mainServer;
    private long time;

    private Thread serverThread;

    public MultitaskManager(String fCode, String gCode) {
        this.fCode = fCode;
        this.gCode = gCode;
        results = new HashMap<>();
    }

    private synchronized void updateResults() {
        results = mainServer.getResults();
    }

    public synchronized boolean checkResultReadines() {
        updateResults();
        String operationRes = tryDoOperation();
        if (operationRes.equals("NaN"))
            return false;
        if ((results.containsKey(fCode) && results.containsKey(gCode))
                || operationRes.equals("0.0"))
            return true;

        return false;
    }

    private synchronized String tryDoOperation() {
        double res = 1;
        boolean hasNaN = false;
        for (Map.Entry<String, String> el : results.entrySet()) {
            if (el.getValue().equals("NaN"))
                hasNaN = true;
            else {
                double val = Double.parseDouble(el.getValue());
                if (val < 1e-7)
                    return "0.0";
                res *= val;
            }
        }
        if (Math.abs(res) > 1E-13 && hasNaN)
            return "UNDEFINED";
        return Double.toString(res);
    }

    public void run(int x) throws Exception {
        mainServer = new MainServer(fCode, gCode, x);

        time = System.nanoTime();
        startProcesses(mainServer.getPort());
        startServer();
        while (!checkResultReadines()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }
        operationResult = Double.parseDouble(tryDoOperation());

        finish();
        //mainServer.finish();
    }

    private void startServer() {
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

    public String getStatus() {
        StringBuilder s = new StringBuilder();
        for (Map.Entry<String, String> el : results.entrySet())
            s.append(el.getKey()).append(" ").append(el.getValue()).append("\n");
        return "Functions status: \n" + s.toString();
        //return results.size() + " Functions finished executuon with values: " + s.toString();
    }

    public long time() {
        return this.time;
    }

    public synchronized boolean isFinished() {
        return finished;
    }

    public void finish() {
        endProcesses();
        serverThread.interrupt();

        finished = true;
    }

    public double getResult() {
        return operationResult;
    }
}
