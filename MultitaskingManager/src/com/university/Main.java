package com.university;

import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.nio.*;
import java.net.*;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import spos.lab1.demo.DoubleOps;
import spos.lab1.demo.IntOps;
import spos.lab1.demo.Conjunction;
import spos.lab1.demo.Disjunction;

import com.university.MultitaskManager;
import com.university.FunctionProcess;

public class Main implements NativeKeyListener {
    private static UserInterface ui;

    public void nativeKeyPressed(NativeKeyEvent e) {
        if (e.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
            try {
                GlobalScreen.unregisterNativeHook();
                ui.close();
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

    public static void main(String[] args) {
        silentLogger();
        handleEscape();

        ui = new UserInterface();
        ui.runManager();
    }
}
