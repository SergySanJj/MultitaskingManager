package com.university;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.util.Scanner;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

class Runner {
    private UserInterface ui;

    public void run() {
        ui = new UserInterface(this);
        ui.runManager(2, 3);
    }

    public boolean finished() {
        if (ui == null)
            return true;
        return ui.finished();
    }

    public void forceFinish() {
        ui.printCurrentStatus();
        ui.close();
    }

    public void restart() {
        ui.close();
        run();
    }
}

public class Main implements NativeKeyListener {
    private static Runner runner;

    public static void run() {
        runner = new Runner();
        runner.run();
    }

    public static void main(String[] args) {
        silentLogger();
        innitPromptSettings();
        run();
    }

    private static void innitPromptSettings() {
        int escapeType = 1;
        System.out.println("Exit by (1)Esc (2)Prompt:");
        Scanner sc = new Scanner(System.in);
        escapeType = sc.nextInt();
        if (escapeType == 1) {
            Settings.usePrompts = false;
            Settings.useEsc = true;
        } else {
            Settings.usePrompts = true;
            Settings.useEsc = false;
        }
        if (Settings.useEsc)
            handleEscape();
    }


    public void nativeKeyPressed(NativeKeyEvent e) {
        if (e.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
            try {
                System.out.println("User pressed Esc");
                //GlobalScreen.unregisterNativeHook();

                runner.forceFinish();
                //System.exit(0);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    public void nativeKeyReleased(NativeKeyEvent e) {
    }

    public void nativeKeyTyped(NativeKeyEvent e) {
    }

    public static void silentLogger() {
        Handler[] handlers = Logger.getLogger("").getHandlers();
        for (int i = 0; i < handlers.length; i++) {
            handlers[i].setLevel(Level.OFF);
        }
    }

    public static void handleEscape() {
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());

            System.exit(1);
        }

        GlobalScreen.addNativeKeyListener(new Main());
    }
}
