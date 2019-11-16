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

class CheckReady {
    static Selector selector;

    static String opName(int op) {
        switch (op) {
            case SelectionKey.OP_READ:
                return "read";
            case SelectionKey.OP_WRITE:
                return "write";
            case SelectionKey.OP_ACCEPT:
                return "accept";
            case SelectionKey.OP_CONNECT:
                return "connect";
        }
        return "";
    }

    static void checkChannel(SelectableChannel channel, String name, int op) throws IOException {
        selector = Selector.open();
        channel.configureBlocking(false);
        SelectionKey key = channel.register(selector, op);
        System.out.println(name + " channel is initially " + ((key.readyOps() & op) != 0 ? "" : "not ") + opName(op) + "able");
        int selected = selector.select(2000);
        System.out.println(selected + " key" + (selected == 1 ? "" : "s") + " w" + (selected == 1 ? "as" : "ere") + " selected");
        System.out.println(name + " channel is now " + ((key.readyOps() & op) != 0 ? "" : "not ") + opName(op) + "able");
        System.out.println();
        //key.cancel();
        selector.close();
    }

    public static void main(String[] agrs) throws IOException {
        Pipe pipe = Pipe.open();


        checkChannel(pipe.source(), "pipe source", SelectionKey.OP_READ);
        checkChannel(pipe.sink(), "pipe sink", SelectionKey.OP_WRITE);

        final int port = 7777;
        Thread client = new Thread() {
            @Override
            public void run() {
                try {
                    //sleep(5);
                    SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("localhost", port));
                    sleep(1_500);
                    ByteBuffer buff = ByteBuffer.allocate(64);
                    buff.clear();
                    buff.put("abc".getBytes());
                    buff.flip();
                    socketChannel.write(buff);
                } catch (IOException e) {
                    System.err.println("failed to connect to server");
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    System.out.println("client is interrupted");
                }
            }
        };

        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress(port));

        client.start();
        SelectableChannel channel = serverSocket.accept();
        long timerStart = System.currentTimeMillis();
        checkChannel(channel, "socket", SelectionKey.OP_READ);
		System.out.println("Time: " + (System.currentTimeMillis() - timerStart));
		timerStart = System.currentTimeMillis();
        checkChannel(channel, "socket", SelectionKey.OP_READ);
		System.out.println("Time: " + (System.currentTimeMillis() - timerStart));

	}
}


