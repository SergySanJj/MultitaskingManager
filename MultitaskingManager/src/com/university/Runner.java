package com.university;

import com.university.functions.wrappers.Functions;

import java.util.Scanner;

class Runner {
    public static String fCode = "f", gCode = "g";
    private static UserInterface ui;
    private static Thread hookThread;
    private static Thread uiThread;

    public static void run() {
        innitPromptSettings();
        innitFunctionCodes();

        while (true) {
            ui = new UserInterface(fCode, gCode);
            uiThread = new Thread(() -> {
                ui.runManager();
            });
            uiThread.start();
            while (!ui.isFinished()) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            uiThread.interrupt();
        }
    }

    public static void forceFinish() {
        if (isUIworking()) {
            ui.printCurrentStatus();
            ui.finish();
        }
    }

    public static boolean isUIworking() {
        return ui != null && ui.isxRetrieved();
    }

    private static void innitPromptSettings() {
        int escapeType;
        System.out.println("Exit by (1)Esc (2)Prompt:");
        Scanner sc = new Scanner(System.in);
        escapeType = sc.nextInt();

        if (escapeType == 1)
            Settings.usePrompts = false;
        else
            Settings.usePrompts = true;

        Settings.useEsc = !Settings.usePrompts;

        if (Settings.useEsc) {
            hookThread = new Thread(EscapeHook::runHook);
            hookThread.start();
        }
    }

    private static void innitFunctionCodes() {
        System.out.println("Choose 2 functions from available: ");
        Functions.printAvailableFunctions();
        Scanner sc = new Scanner(System.in);
        Runner.fCode = sc.next();
        Runner.gCode = sc.next();


    }
}
