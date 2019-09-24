package com.university;

import spos.lab1.demo.DoubleOps;

import javax.swing.*;

public class FunctionProcess {
    public static void main(String[] args) {
        String message;

        try {
            message = "I am processing function " + args[0] +
                    " with argument " + args[1] +
                    " connecting to port " + args[2];
        } catch (Exception e) {
            message = "Something went wrong";
        }
        JOptionPane.showMessageDialog(null, message);

        functionCode = Integer.parseInt(args[0]);
        x = Integer.parseInt(args[1]);
        port = Integer.parseInt(args[2]);

        try {
            result = Functions.run(functionCode, x);
        } catch (Exception e) {
            String errorMessage =
                    "Function " + Integer.toString(functionCode) + " throwed exception";
            JOptionPane.showMessageDialog(null, errorMessage);
            e.printStackTrace();
            return;
        }
        message = "Function " + Integer.toString(functionCode) + " resulted " + Double.toString(result);
        JOptionPane.showMessageDialog(null, message);
    }

    private static int functionCode;
    private static int x;
    private static int port;
    private static double result = -1.0;
}
