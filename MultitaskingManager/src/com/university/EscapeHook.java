package com.university;

import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;

public class EscapeHook {
    public static GlobalKeyboardHook keyboardHook;

    public static void runHook() {
        boolean run = true;
        if (keyboardHook == null)
            keyboardHook = new GlobalKeyboardHook(true);

        keyboardHook.addKeyListener(new GlobalKeyAdapter() {

            @Override
            public void keyPressed(GlobalKeyEvent event) {
                if (event.getVirtualKeyCode() == GlobalKeyEvent.VK_ESCAPE) {
                    System.out.println("User pressed Esc");
                    Runner.forceFinish();
                }
            }

            @Override
            public void keyReleased(GlobalKeyEvent event) {
            }
        });

        try {
            while (true) {
                Thread.sleep(128);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
