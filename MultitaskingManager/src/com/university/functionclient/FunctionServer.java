package com.university.functionclient;

import com.university.StrFunc;
import com.university.functions.wrappers.Functions;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;


public class FunctionServer {
    private static int port;
    private SocketChannel channel;
    private String msg;
    private String functionCode;
    private int x;

    public FunctionServer(int port) {
        FunctionServer.port = port;
    }

    public void start() throws Exception {
        connect();
        read();
        processing();
        write();
    }

    private void connect() throws Exception {
        channel = SocketChannel.open();
        channel.configureBlocking(true);
        channel.connect(new InetSocketAddress("localhost", port));
        while (!channel.isConnected()) ;
        System.out.println("Connection established");
    }

    private void write() throws IOException {
        if (msg != null) {
            ByteBuffer buff = ByteBuffer.allocate(256);
            CharBuffer cbuff = buff.asCharBuffer();
            cbuff.put(msg);
            cbuff.flip();

            channel.write(buff);
            System.out.println("Result sent");
            msg = null;
            endWork();
        }
    }


    private void read() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(256);
        CharBuffer cbuff = buffer.asCharBuffer();

        channel.read(buffer);
        if (buffer.equals(ByteBuffer.allocate(256).clear()))
            return;
        String fargs = cbuff.toString();

        System.out.println("Recieved: " + fargs);

        String[] ff = StrFunc.parseNumValues(fargs);
        functionCode = StrFunc.parseFunctionWithoutCopyLabel(ff[0]);
        System.out.println(functionCode);
        x = Integer.parseInt(ff[1]);
    }

    private void processing(){
        System.out.println("Running..");
        startProcessing();
        System.out.println("Got result " + msg);
    }


    private void startProcessing() {
        try {
            double res = runFunction(functionCode, x);
            msg = "1 " + String.format("%.12f", res);
        } catch (Exception e) {
            msg = "0 -1.0";
        }
    }

    private double runFunction(String functionCode, int x) throws Exception {
        return Functions.run(functionCode, x);
    }

    public void endWork() {
        try {
            System.out.println("Finishing..");
            channel.close();
            Runtime.getRuntime().exec("taskkill /f /im cmd.exe");
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
