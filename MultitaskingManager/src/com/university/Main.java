package com.university;

import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.Callable;

import spos.lab1.demo.DoubleOps;
import spos.lab1.demo.IntOps;
import spos.lab1.demo.Conjunction;
import spos.lab1.demo.Disjunction;

import com.university.MultitaskManager;

public class Main {

    public static void main(String[] args) throws Exception {
        FunctionManager<Integer, Double> f = new FunctionManager<Integer, Double>((Integer i) -> {
            try {
                return DoubleOps.funcF(i);
            } catch (Exception e) {
                System.out.println(Arrays.toString(e.getStackTrace()));
                return -1.0;
            }
        });
        FunctionManager<Integer, Double> g = new FunctionManager<Integer, Double>((Integer i) -> {
            try {
                return DoubleOps.funcG(i);
            } catch (Exception e) {
                System.out.println(Arrays.toString(e.getStackTrace()));
                return -1.0;
            }
        });


        MultitaskManager manager = new MultitaskManager(f, g);

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
