package com.university.manager;

import com.university.Settings;
import com.university.functionclient.FunctionProcess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Manager {
    private int x;
    private String fCode;
    private String gCode;

    private boolean finished = false;
    private final Object finishMutex = new Object();

    private Process fProcess, gProcess;

    public Manager(String fCode, String gCode) {
        this.fCode = fCode;
        this.gCode = gCode;
    }

    public boolean isFinished() {
        synchronized (finishMutex) {
            return finished;
        }
    }

    public void run() {
        inputX();
        try {
            Server server = new Server(x, fCode, gCode);
            startProcesses(server.getPort());
            server.start();
            // finish processes
            endProcesses();
            synchronized (finishMutex) {
                finished = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void inputX() {
        boolean inputted = false;
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter x:");
        do {
            try {
                String str = sc.next();
                x = Integer.parseInt(str);
                inputted = true;
            } catch (Exception e) {
                System.out.println("x must be an int value");
            }
        } while (!inputted);
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
}
