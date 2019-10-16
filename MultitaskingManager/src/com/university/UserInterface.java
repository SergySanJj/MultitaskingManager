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
    private boolean finished = false;

    private Thread inputThread;
    private Thread runnerThread;

    private Runner parent;

    public boolean finished(){
        return finished;
    }

    UserInterface(Runner parent){
        this.parent = parent;
    }

    public void runManager(int fCode, int gCode) {
        inputX();

        runnerThread = new Thread(() -> startManager(fCode, gCode));
        runnerThread.start();


        if (Settings.usePrompts) {
            inputThread = new Thread(this::startUserPrompt);
            inputThread.start();
        }
        try {
            runnerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        restart();
        //System.exit(0);
    }

    private void startUserPrompt() {
        Scanner sc = new Scanner(System.in);
        while (currentState != Ccancel && currentState != CwithoutPrompt && !finished) {
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
            restart();
            //System.exit(0);
        }
        if (currentState == Ccancel){
            System.out.println("Canceling..");
            System.out.println(manager.getStatus());
            manager.close();
            restart();
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

    private void startManager(int fCode, int gCode) {
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
        double workedFor = (System.nanoTime() - manager.time()) / 1000000000.0;
        isResultReady = true;
        while (isCurrentlyPrompted) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Result: " + res);
        System.out.println("Total time: " + workedFor + " s");
        manager.close();
        //System.exit(0);
        restart();
    }

    public void restart(){
        inputThread.stop();
        runnerThread.stop();
        finished = true;
        //parent.retry();
        //System.exit(0);
    }
}
