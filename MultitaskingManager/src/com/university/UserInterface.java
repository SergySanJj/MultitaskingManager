package com.university;

import spos.lab1.demo.DoubleOps;

import java.util.Arrays;
import java.util.Scanner;

public class UserInterface {

    public void runManager() {
        MultitaskManager manager = new MultitaskManager(0, 1);

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
}
