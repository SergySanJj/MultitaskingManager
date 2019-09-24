package com.university;

import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.nio.*;
import java.net.*;

import spos.lab1.demo.DoubleOps;
import spos.lab1.demo.IntOps;
import spos.lab1.demo.Conjunction;
import spos.lab1.demo.Disjunction;

import com.university.MultitaskManager;
import com.university.FunctionProcess;

public class Main {


    public static void main(String[] args) {
        try {
            Functions.run(0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        UserInterface ui = new UserInterface();
        ui.runManager();
    }
}
