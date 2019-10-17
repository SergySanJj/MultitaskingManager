package com.university;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class EscapeListener implements NativeKeyListener {
    private static EscapeListener escapeListener;

    public void nativeKeyPressed(NativeKeyEvent e) {
        //System.out.println("Key Pressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
        if (e.getKeyCode() == NativeKeyEvent.VC_END) {

            System.out.println("User pressed End");
            if (Main.getRunner() != null) {
                Main.getRunner().forceFinish();
                Main.getRunner().restart();
            }

        }
    }

    public void nativeKeyReleased(NativeKeyEvent e) {
    }

    public void nativeKeyTyped(NativeKeyEvent e) {
    }

    public static void handleEscape() {
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());

            System.exit(1);
        }
        if (escapeListener == null)
            escapeListener = new EscapeListener();
        GlobalScreen.addNativeKeyListener(escapeListener);
    }
}
