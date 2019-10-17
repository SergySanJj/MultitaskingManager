package com.university;

import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;

import java.util.Map;
import java.util.Scanner;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

class Runner {
    private static UserInterface ui;

    public static void run() {
        ui = new UserInterface();
        ui.runManager(2, 3);
    }


    public static void forceFinish() {
        if (ui != null)
            ui.printCurrentStatus();
    }

    public static void restart() {
        ui.close();
        run();
    }
}

public class Main {
    private static Thread hookThread;

    public static void main(String[] args) {
        silentLogger();
        innitPromptSettings();
        run();
    }

    public static void run() {
        Runner.run();
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

    public static void silentLogger() {
        Handler[] handlers = Logger.getLogger("").getHandlers();
        for (Handler handler : handlers) {
            handler.setLevel(Level.OFF);
        }
    }
}
