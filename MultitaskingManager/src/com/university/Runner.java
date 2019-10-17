package com.university;

import com.university.functions.wrappers.Functions;

import java.util.Scanner;

class Runner {
    public static String fCode = "f", gCode = "g";
    private static UserInterface ui;
    private static Thread hookThread;

    public static void run() {
        innitPromptSettings();
        innitFunctionCodes();

        ui = new UserInterface();
        ui.runManager(fCode, gCode);
    }

    public static void forceFinish() {
        if (ui != null)
            ui.printCurrentStatus();
    }

    public static void restart() {
        ui.restart();
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
