package com.university;

import java.io.*;
import java.lang.Process;
import java.lang.ProcessBuilder;
import java.net.ServerSocket;
import java.net.Socket;

public class MultitaskManager {
    private MultitaskManager() {

    }

    public MultitaskManager(int fCode, int gCode) {
        this.fCode = fCode;
        this.gCode = gCode;
    }

    public void run(int x) throws InterruptedException {
        try {
            serverSocket = new ServerSocket(0);
        } catch (IOException e) {
            System.out.println("Can't create server sockets");
            e.printStackTrace();
            return;
        }

        ProcessBuilder fProcessBuilder = createFunctionProcessBuilder(0, x, serverSocket.getLocalPort());
        ProcessBuilder gProcessBuilder = createFunctionProcessBuilder(1, x, serverSocket.getLocalPort());

        try {
            fProcess = fProcessBuilder.start();
            gProcess = gProcessBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        startServer();

        double result = 0;
        System.out.println("result " + Double.toString(result));
    }

    private ProcessBuilder createFunctionProcessBuilder(int functionCode, int x, int port) {
        String[] startOptions = new String[]{
                "java", "-Dname=function" + Integer.toString(functionCode),
                "-cp", System.getProperty("java.class.path", "."), FunctionProcess.class.getName(),
                Integer.toString(functionCode), Integer.toString(x), Integer.toString(port), "END"
        };
        return new ProcessBuilder(startOptions);
    }

    public void startServer() throws InterruptedException {
        try {
            fSocket = serverSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            gSocket = serverSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            fInBuffer = new BufferedReader(new InputStreamReader(fSocket.getInputStream()));
            fOutBuffer = new BufferedWriter(new OutputStreamWriter(fSocket.getOutputStream()));

            gInBuffer = new BufferedReader(new InputStreamReader(gSocket.getInputStream()));
            gOutBuffer = new BufferedWriter(new OutputStreamWriter(gSocket.getOutputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (fProcess.isAlive() || gProcess.isAlive()) {

            try {
                String word = fInBuffer.readLine();
                System.out.println("output1 " + word);
            } catch (IOException e) {
                e.printStackTrace();
            }


            Thread.sleep(10);
            System.out.print("\rwaiting.. x" + Integer.toString(tick));
            tick++;
        }
        System.out.println();
        gProcess.destroy();
        fProcess.destroy();

        try {
            serverSocket.close();
            fSocket.close();
            gSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int fCode, gCode;
    private double fRes = 0.;
    private double gRes = 0.;

    private int tick = 0;
    private ServerSocket serverSocket;
    private Socket fSocket, gSocket;
    private Process fProcess, gProcess;
    private BufferedReader fInBuffer, gInBuffer;
    private BufferedWriter fOutBuffer, gOutBuffer;

}
