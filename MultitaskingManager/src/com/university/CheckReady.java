package com.university;
import java.nio.channels.SelectableChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.nio.channels.Pipe;
import java.io.IOException;

import java.nio.channels.SocketChannel;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;

import java.nio.ByteBuffer;

import java.util.Set;

class CheckReady {
    static Selector selector;

    static String opNames(int op) {
        String names = "";
        String prefix = "";
        if ((op & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
            names = "read";
            prefix = "-";
        }
        if ((op & SelectionKey.OP_WRITE) == SelectionKey.OP_WRITE) {
            names += prefix;
            names += "write";
            prefix = "-";
        }
        if ((op & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
            names += prefix;
            names += "accept";
            prefix = "-";
        }
        if ((op & SelectionKey.OP_CONNECT) == SelectionKey.OP_CONNECT) {
            names += prefix;
            names += "connect";
        }

        return names;
    }

    static void checkOnce(SelectionKey key, String name, int op, int timeoutMillis) throws IOException {
        SelectableChannel channel = key.channel();
        System.out.println(name + " channel is " + ((key.readyOps() & op) != 0 ? "" : "not ") + opNames(op) + "-able");
        long startTimeMillis = System.currentTimeMillis();

        int selected = key.selector().select(timeoutMillis);
        Set<SelectionKey> selectedKeys = key.selector().selectedKeys();
        switch (selected) {
            case 0:
                System.out.print("nothing was");
                break;

            case 1:
                System.out.print((selectedKeys.contains(key) ? "this" : "other") +  " key was");
                break;

            default:
                System.out.println((selectedKeys.contains(key) ? "extra" : "other") + " keys were");
        }
        System.out.println(" selected in " + (System.currentTimeMillis() - startTimeMillis) + " out of " + timeoutMillis + " millis");

        startTimeMillis = System.currentTimeMillis();
        if (selected > 0) {
            int reselected = key.selector().select();
            switch (reselected) {
                case 0:
                    System.out.print("no more keys were ");
                    break;

                case 1:
                    System.out.print(selectedKeys.contains(key) ? "this key was re-" : "other key was ");
                    break;

                default:
                    System.out.println((selectedKeys.contains(key) ? "extra" : "other") + " keys were ");
            }
            long intervalMillis = System.currentTimeMillis() - startTimeMillis;
            System.out.println("selected on a second call"
                    + " (" + ((intervalMillis < 2) ? "returned instantly" : "took " + intervalMillis + " millis")
                    + ")");
        }
	/*
	if (selected == 0)
	    return;
	    */

        for (SelectionKey iKey: selectedKeys) {
            switch (op) {
                case SelectionKey.OP_READ:
                    if (iKey.isReadable() && channel instanceof ReadableByteChannel) {
                        ByteBuffer buffer = ByteBuffer.allocate(512);
                        int total = 0;
                        int count;
                        while((count = ((ReadableByteChannel) channel).read(buffer)) > 0)
                            total += count;
                        buffer.flip();
                        System.out.println("read: " + total + " byte(s)");
                    }
                    break;

                case SelectionKey.OP_WRITE:
                case SelectionKey.OP_ACCEPT:
                case SelectionKey.OP_CONNECT:
                    break;
            }
        }
        selectedKeys.remove(key);
        if (key.isReadable()) {
            key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
            selected = key.selector().selectNow();
            if (selectedKeys.contains(key))
                System.out.println("cleaning: key successfully selected, as " + opNames(key.readyOps()) + "-able");
            key.interestOps(SelectionKey.OP_READ);
        }
        //selectedKeys.remove(key);
        //selectedKeys.clear();
    }

    static void checkOnce(SelectionKey key, String name, int op) throws IOException {
        checkOnce(key, name, op, 3_000);
    }

    static void checkChannel(SelectableChannel channel, String name, int op) throws IOException {
        selector = Selector.open();
        channel.configureBlocking(false);
        SelectionKey key = channel.register(selector, op);
        checkOnce(key, name, op);
        checkOnce(key, name, op, 2_000);
        checkOnce(key, name, op, 1_000);
        System.out.println("check is finished");

        //key.cancel();
        selector.close();
    }

    public static void main(String[] agrs) throws IOException {
        Pipe pipe = Pipe.open();

        //checkChannel(pipe.source(), "pipe source", SelectionKey.OP_READ);
        //checkChannel(pipe.sink(), "pipe sink", SelectionKey.OP_WRITE);

        System.out.println("******* socket channel ********");

        final int port = 7777;
        Thread client = new Thread() {
            @Override
            public void run() {
                try {
                    SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("localhost", port));
                    sleep(7_000);
                    ByteBuffer buff = ByteBuffer.allocate(64);
                    buff.clear();
                    buff.put("abc".getBytes());
                    buff.flip();
                    socketChannel.write(buff);
                    sleep(4_500);
                }
                catch (IOException e) {
                    System.err.println("failed to connect to server");
                    e.printStackTrace();
                }
                catch (InterruptedException e) {
                    System.out.println("client is interrupted");
                }
            }
        };

        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress(port));

        client.start();
        SelectableChannel channel = serverSocket.accept();
        checkChannel(channel, "socket", SelectionKey.OP_READ);
        System.out.println();

        System.out.println("second attempt");
        checkChannel(channel, "socket", SelectionKey.OP_READ);
        System.out.println();

        System.out.println("third attempt");
        checkChannel(channel, "socket", SelectionKey.OP_READ);
        System.out.println();
    }
}
