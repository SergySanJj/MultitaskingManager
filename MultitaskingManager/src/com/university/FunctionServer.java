package com.university;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static java.nio.ByteBuffer.allocate;
import static java.nio.channels.SelectionKey.*;

public class FunctionServer {
    private static int port;

    private ByteBuffer buffer = allocate(16);
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
        selector = Selector.open();
        //todo: here
        channel.register(selector, OP_CONNECT);
        channel.connect(new InetSocketAddress("localhost", port));


        while (true) {
            selector.select();
            for (SelectionKey selectionKey : selector.selectedKeys()) {
                if (selectionKey.isConnectable()) {
                    channel.finishConnect();
                    selectionKey.interestOps(OP_READ);
                } else if (selectionKey.isReadable()) {
                    buffer.clear();
                    channel.read(buffer);
                    System.out.println("Recieved = " + new String(buffer.array()));
                    String fargs = new String(buffer.array());
                    JOptionPane.showMessageDialog(null, fargs);

                    String[] ff = fargs.split(" ", 2);
                    functionCode = Integer.parseInt(ff[0]);
                    x = Integer.parseInt(ff[1]);

                    new Thread(() -> {
                        startProcessing();
                        selectionKey.interestOps(OP_WRITE);
                    }).start();


                } else if (selectionKey.isWritable()) {
                    String line = queue.poll();
                    if (line != null) {
                        channel.write(ByteBuffer.wrap(line.getBytes()));
                    }
                    selectionKey.interestOps(OP_READ);
                    channel.close();
                }
            }
        }

    }

    private void startProcessing() {
        try {
            double res = runFunction(functionCode, x);
            msg = "1 " + Double.toString(res);
        } catch (Exception e) {
            msg = "0";
        }
    }

    private double runFunction(int functionCode, int x) throws Exception {
        return Functions.run(functionCode, x);
    }

    public void endWork() {
        try {
            channel.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
