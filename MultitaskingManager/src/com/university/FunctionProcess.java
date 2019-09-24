package com.university;

import spos.lab1.demo.DoubleOps;

import javax.swing.*;
import java.io.*;
import java.lang.reflect.GenericDeclaration;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.*;

public class FunctionProcess {
    public static void main(String[] args) {
        String message;
        port = Integer.parseInt(args[0]);

        try {
            mainServer = new Socket("localhost", port);

            in = new BufferedReader(new InputStreamReader(mainServer.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(mainServer.getOutputStream()));

            result = Functions.run(functionCode, x);
        } catch (Exception e) {
            String errorMessage =
                    "Function " + Integer.toString(functionCode) + " throwed exception " + e;
            JOptionPane.showMessageDialog(null, errorMessage);
            e.printStackTrace();
            return;
        }
        message = "Function " + Integer.toString(functionCode) + " resulted " + Double.toString(result);
        //JOptionPane.showMessageDialog(null, message);

        try {
            out.write(Double.toString(result));

            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

        endWork();
    }

    private static void endWork(){
        try {
            mainServer.close();
            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int functionCode;
    private static int x;
    private static int port;
    private static double result = -1.0;

    private static Socket mainServer;
    private static BufferedReader in;
    private static BufferedWriter out;
}
