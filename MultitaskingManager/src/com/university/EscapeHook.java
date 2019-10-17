package com.university;

import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;

import java.util.Map;

public class EscapeHook {
    public static GlobalKeyboardHook keyboardHook;

    public static void runHook() {
        boolean run = true;
        if (keyboardHook == null)
            keyboardHook = new GlobalKeyboardHook(true);

        keyboardHook.addKeyListener(new GlobalKeyAdapter() {

            @Override
            public void keyPressed(GlobalKeyEvent event) {
                //System.out.println(event);
                if (event.getVirtualKeyCode() == GlobalKeyEvent.VK_LEFT) {
                    System.out.println("l");
                }

                if (event.getVirtualKeyCode() == GlobalKeyEvent.VK_ESCAPE) {
                    System.out.println("User pressed Esc");
                    new Thread(() -> {
                        Runner.forceFinish();
                        Runner.restart();
                    }).start();

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
