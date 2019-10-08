package com.university;

import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import spos.lab1.demo.DoubleOps;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.EventListener;
import java.util.EventListenerProxy;
import java.util.Scanner;

public class UserInterface {

    private MultitaskManager manager;
    private final int maxIdleTime = 1000;
    private int x;
    private final int Ccontinue = 1;
    private final int CwithoutPrompt = 2;
    private final int Ccancel = 3;

    private int currentState = Ccontinue;

    public void runManager() {
        inputX();

        Thread runnerThread = new Thread(this::bladeRunner);
        runnerThread.start();

        Thread inputThread = new Thread(() -> {
            Scanner sc = new Scanner(System.in);
            while (currentState != Ccancel && currentState != CwithoutPrompt) {
                try {
                    Thread.sleep(maxIdleTime);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("Functions running for too long, options: \n" +
                        "continue(1)\n" +
                        "continue without prompt(2)\n" +
                        "cancel(3)");
                int code = sc.nextInt();
                code = code % 4;
                currentState = code;
            }
            if (currentState == Ccancel) {
                System.out.println("Finishing..");
                manager.close();
                System.exit(0);
            }
        });
        inputThread.start();

        while (inputThread.isAlive() || runnerThread.isAlive()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Finishing..");
        System.exit(0);
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

    public void bladeRunner() {
        manager = new MultitaskManager(this, 0, 1);
        try {
            manager.run(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pollZero() {
        System.out.println("Result: 0.0");
        System.exit(0);
    }

    public void pollResult(double res) {
        System.out.println("Result: " + Double.toString(res));
        System.exit(0);
    }
}
