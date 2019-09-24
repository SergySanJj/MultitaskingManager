package com.university;

import java.io.IOException;
import java.lang.Process;
import java.lang.ProcessBuilder;
import java.net.ServerSocket;

import com.university.FunctionManager;

public class MultitaskManager {
    private MultitaskManager() {

    }

    public MultitaskManager(int fCode, int gCode) {
        this.fCode = fCode;
        this.gCode = gCode;
    }

    public void run(int x) throws InterruptedException {
        double fRes = 0.;
        double gRes = 0.;

        ServerSocket fSocket;
        ServerSocket gSocket;
        try {
            fSocket = new ServerSocket(0);
            gSocket = new ServerSocket(0);
        } catch (IOException e) {
            System.out.println("Can't create server sockets");
            e.printStackTrace();
            return;
        }

        ProcessBuilder fProcessBuilder = startProcessing(0, x, fSocket.getLocalPort());
        ProcessBuilder gProcessBuilder = startProcessing(1, x, gSocket.getLocalPort());

        Process fProcess, gProcess;

        try {
            fProcess = fProcessBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try {
            gProcess = gProcessBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        int tryCount = 1;
        while (fProcess.isAlive() || gProcess.isAlive()) {
            Thread.sleep(500);
            System.out.print("\rwaiting.. x" + Integer.toString(tryCount));
            tryCount++;
        }
        System.out.println();
        gProcess.destroy();
        fProcess.destroy();

        //System.out.println(fRes * gRes);
    }

    private ProcessBuilder startProcessing(int functionCode, int x, int port) {
        String[] startOptions = new String[]{
                "java", "-Dname=function" + Integer.toString(functionCode),
                "-cp", System.getProperty("java.class.path", "."), FunctionProcess.class.getName(),
                Integer.toString(functionCode), Integer.toString(x), Integer.toString(port), "END"
        };
        return new ProcessBuilder(startOptions);
    }

    private int fCode, gCode;
}
