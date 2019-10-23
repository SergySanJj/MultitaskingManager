package com.university;

import java.util.Scanner;

public class UserInterface {
    private boolean finished = false;
    private boolean xRetrieved = false;

    private MultitaskManager manager;
    private int x;
    private static final int Ccontinue = 1;
    private static final int CwithoutPrompt = 2;
    private static final int Ccancel = 3;

    private boolean isCurrentlyPrompted = false;
    private boolean isResultReady = false;
    private int currentState = Ccontinue;

    private Thread inputThread;
    private Thread runnerThread;

    private String fCode, gCode;

    public UserInterface(String fCode, String gCode) {
        this.fCode = fCode;
        this.gCode = gCode;

    }

    public void runManager() {
        manager = new MultitaskManager(fCode, gCode);
        inputX();

        runnerThread = new Thread(() -> startManager());
        runnerThread.start();

        if (Settings.usePrompts) {
            inputThread = new Thread(this::startUserPrompt);
            inputThread.start();
        }

        while (!manager.isFinished()) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException ignored) {
            }
        }

        if (manager.checkResultReadines()) {
            String operationResult = manager.getResult();
            pollResult(operationResult);
        }

        if (!isFinished())
            finish();
    }

    private void startUserPrompt() {
        Scanner sc = new Scanner(System.in);
        while (currentState != Ccancel && currentState != CwithoutPrompt) {
            try {
                Thread.sleep(Settings.maxIdleTime);
            } catch (Exception e) {
                inputThread.interrupt();
                return;
            }
            System.out.println("Functions running for too long, options: \n" +
                    "continue(1)\n" +
                    "continue without prompt(2)\n" +
                    "cancel(3)");
            isCurrentlyPrompted = true;
            int code = sc.nextInt();
            isCurrentlyPrompted = false;
            code = code % 4;
            currentState = code;
        }
        if (currentState == Ccancel && !isResultReady) {
            System.out.println("Canceling..");
            printCurrentStatus();
            finish();
        }
    }

    public void printCurrentStatus() {
        if (manager != null)
            System.out.println(manager.getStatus());
    }

    private void inputX() {
        boolean inputed = false;
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter x:");
        do {
            try {
                String str = sc.next();
                x = Integer.parseInt(str);
                inputed = true;
            } catch (Exception e) {
                System.out.println("x must be an int value");
            }
        } while (!inputed);

        xRetrieved = true;
    }

    private void startManager() {
        try {
            manager.run(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public synchronized void pollResult(String res) {
        double workedFor = (System.nanoTime() - manager.time()) / 1000000000.0;
        isResultReady = true;
        while (isCurrentlyPrompted) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        System.out.println("Result: " + res);
        System.out.println("Total time: " + workedFor + " s");

        finish();
    }

    public synchronized boolean isFinished() {
        return finished;
    }

    public synchronized boolean isxRetrieved() {
        return xRetrieved;
    }

    public void finish() {
        manager.finish();
        if (runnerThread != null)
            runnerThread.interrupt();
        if (inputThread != null)
            inputThread.interrupt();
        finished = true;
    }
}
