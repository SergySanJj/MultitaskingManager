package com.university;

import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import spos.lab1.demo.DoubleOps;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.EventListener;
import java.util.EventListenerProxy;
import java.util.Scanner;

public class UserInterface {
    private MultitaskManager manager;
    public void runManager() {
        manager = new MultitaskManager(0, 1);

        int x;
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter x:");
        if (sc.hasNextInt()) {
            x = sc.nextInt();

            try {
                manager.run(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("x must be an int value");
        }


        System.out.println("Finishing..");
    }

    public void close(){
        manager.close();
    }
}
