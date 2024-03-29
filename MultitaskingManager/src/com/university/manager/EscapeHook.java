package com.university.manager;

import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;

public class EscapeHook {
    private static GlobalKeyboardHook keyboardHook;

    public static void runHook() {
        if (keyboardHook == null)
            keyboardHook = new GlobalKeyboardHook(true);

        keyboardHook.addKeyListener(new GlobalKeyAdapter() {

            @Override
            public void keyPressed(GlobalKeyEvent event) {
                if (event.getVirtualKeyCode() == GlobalKeyEvent.VK_ESCAPE) {
                    if (Runner.isActive()) {
                        System.out.println("User pressed Esc");
                        Runner.forceFinish();
                    }
                } else if (event.getVirtualKeyCode() == GlobalKeyEvent.VK_HOME) {
                    System.exit(0);
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
