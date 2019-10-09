package com.university;

import java.util.Scanner;

public class UserInterface {

    private MultitaskManager manager;
    private int x;
    private final int Ccontinue = 1;
    private final int CwithoutPrompt = 2;
    private final int Ccancel = 3;

    private boolean isCurrentlyPrompted = false;
    private boolean isResultReady = false;
    private int currentState = Ccontinue;

    public void runManager(int fCode, int gCode) {
        inputX();

        Thread runnerThread = new Thread(() -> startManager(fCode, gCode));
        runnerThread.start();

        Thread inputThread = new Thread(this::startUserPrompt);
        inputThread.start();

        while (inputThread.isAlive() || runnerThread.isAlive()) {
        }

        System.out.println("Finishing..");
        System.exit(0);
    }

    private void startUserPrompt() {
        Scanner sc = new Scanner(System.in);
        while (currentState != Ccancel && currentState != CwithoutPrompt) {
            try {
                Thread.sleep(Settings.maxIdleTime);
            } catch (Exception e) {
                e.printStackTrace();
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
            System.out.println(manager.getStatus());
            manager.close();
            System.exit(0);
        }
    }

    public void close() {
        manager.close();
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
    }

    public void startManager(int fCode, int gCode) {
        manager = new MultitaskManager(this, fCode, gCode);
        try {
            manager.run(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pollZero() {
        pollResult("0.0");
    }

    public void pollResult(String res) {
        isResultReady = true;
        while (isCurrentlyPrompted) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Result: " + res);
        manager.close();
        System.exit(0);
    }
}
