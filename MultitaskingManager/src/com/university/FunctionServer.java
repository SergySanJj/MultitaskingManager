package com.university;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static java.lang.Character.isDigit;
import static java.nio.ByteBuffer.allocate;
import static java.nio.channels.SelectionKey.*;

import static com.university.StrFunc.*;

public class FunctionServer {
    private static int port;

    private Selector selector;
    private SocketChannel channel;

    String msg;
    private int functionCode, x;

    BlockingQueue<String> queue = new ArrayBlockingQueue<>(2);

    public FunctionServer(int port) {
        this.port = port;
    }

    public void start() throws Exception {
        channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(new InetSocketAddress("localhost", port));

        selector = Selector.open();
        channel.register(selector, OP_CONNECT);

        while (selector.isOpen()) {
            selector.select();

            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                iterator.remove();

                if (!selectionKey.isValid())
                    break;

                else if (selectionKey.isConnectable() && !channel.isConnected()) {
                    connect(selectionKey);
                } else if (selectionKey.isReadable()) {
                    read(selectionKey);
                } else if (selectionKey.isWritable()) {
                    write();

                }
            }
        }

    }

    private void write() throws IOException {
        if (msg != null) {
            ByteBuffer buff = ByteBuffer.allocate(32);
            CharBuffer cbuff = buff.asCharBuffer();
            cbuff.put(msg);
            cbuff.flip();

            channel.write(buff);
            System.out.println("Result sent");
            msg = null;
            endWork();
        }
    }


    private void read(SelectionKey selectionKey) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(32);
        CharBuffer cbuff = buffer.asCharBuffer();

        int numRead = channel.read(buffer);
        if (buffer.equals(ByteBuffer.allocate(32).clear()))
            return;
        String fargs = cbuff.toString();

        System.out.println("Recieved: " + fargs);

        String[] ff = StrFunc.parseNumValues(fargs);
        functionCode = Integer.parseInt(ff[0]);
        x = Integer.parseInt(ff[1]);

        new Thread(this::processing).start();
        selectionKey.interestOps(OP_WRITE);
    }

    private void processing() {
        System.out.println("Running..");
        startProcessing();
        try {
            channel.register(selector, OP_WRITE);
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        }
        System.out.println("Got result " + msg);
    }


    private void connect(SelectionKey selectionKey) throws IOException {
        channel.finishConnect();
        selectionKey.interestOps(OP_READ);
        System.out.println("Connection established");
    }

    private void startProcessing() {
        try {
            double res = runFunction(functionCode, x);
            msg = "1 " + Double.toString(res);
        } catch (Exception e) {
            msg = "0 -1.0";
        }
    }

    private double runFunction(int functionCode, int x) throws Exception {
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
