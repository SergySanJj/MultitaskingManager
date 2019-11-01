package com.university.manager;

import com.university.Settings;
import com.university.functions.wrappers.Functions;

import java.util.Scanner;

class Runner {
    private static String fCode = null;
    private static String gCode = null;
    private static Manager manager;

    public static void run() {
        innitPromptSettings();
        innitFunctionCodes();

        while (true) {
            manager = new Manager(fCode, gCode);
            Thread managerThread = new Thread(() -> manager.run());
            managerThread.start();
            while (!manager.isFinished()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            managerThread.interrupt();
            System.out.println();
        }
    }

    public static void forceFinish() {
        System.out.println(manager.getStatus());
        manager.forceFinish();
    }

    public static boolean isActive() {
        if (manager == null)
            return false;
        return manager.isActive();
    }

    private static void innitPromptSettings() {
        int escapeType;
        System.out.println("Exit by (1)Esc (2)Prompt:");
        Scanner sc = new Scanner(System.in);
        escapeType = sc.nextInt();

        Settings.usePrompts = escapeType != 1;
        Settings.useEsc = !Settings.usePrompts;

        if (Settings.useEsc) {
            Thread hookThread = new Thread(EscapeHook::runHook);
            hookThread.start();
        }
    }

    private static void innitFunctionCodes() {
        Scanner sc = new Scanner(System.in);
        while (fCode == null || gCode == null || !(Functions.hasFunction(fCode) && Functions.hasFunction(gCode))) {
            System.out.println("Choose 2 functions from available: ");
            Functions.printAvailableFunctions();
            Runner.fCode = sc.next();
            Runner.gCode = sc.next();
        }

        if (Runner.fCode.equals(Runner.gCode)) {
            Runner.gCode = Functions.duplicateFunction(fCode);
        }
    }
}
