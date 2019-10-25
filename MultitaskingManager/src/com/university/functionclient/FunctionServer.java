package com.university.functionclient;

import com.university.StrFunc;
import com.university.functions.wrappers.Functions;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import static java.nio.channels.SelectionKey.*;

public class FunctionServer {
    private static int port;
    private Selector selector;
    private SocketChannel channel;
    private String msg;
    private String functionCode;
    private int x;

    public FunctionServer(int port) {
        FunctionServer.port = port;
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


    private void read(SelectionKey selectionKey) throws IOException {
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
