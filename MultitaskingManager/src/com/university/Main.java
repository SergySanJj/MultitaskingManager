package com.university;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

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
        return ui.finished();
    }
}

public class Main implements NativeKeyListener {
    private static UserInterface ui;

    public static void run() {
        Runner runner = new Runner();
        while (true) {
            runner.run();
            while (!runner.finished()) {
                try {
                    Thread.sleep(100);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        }
    }

    public static void main(String[] args) {
        silentLogger();
        handleEscape();

        run();
    }


    public void nativeKeyPressed(NativeKeyEvent e) {
        if (e.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
            try {
                System.out.println("Finishing..");
                GlobalScreen.unregisterNativeHook();
                //ui.close();
                System.exit(0);
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
