package com.university;

import java.util.Scanner;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

class Runner {
    private UserInterface ui;

    public void run() {
        ui = new UserInterface(this);
        ui.runManager(2, 3);
    }


    public void forceFinish() {
        if (ui != null)
            ui.printCurrentStatus();
    }

    public void restart() {
        ui.close();
        run();
    }
}

public class Main {
    private static Runner runner;

    public static void main(String[] args) {
        silentLogger();
        innitPromptSettings();
        run();
    }

    public static Runner getRunner(){
        return runner;
    }

    public static void run() {
        runner = new Runner();
        runner.run();
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

        if (Settings.useEsc)
            EscapeListener.handleEscape();
    }

    public static void silentLogger() {
        Handler[] handlers = Logger.getLogger("").getHandlers();
        for (Handler handler : handlers) {
            handler.setLevel(Level.OFF);
        }
    }
}
