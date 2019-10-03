package com.university;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class FunctionServer {
    private static int port;

    private SocketChannel mainServer;
    private BufferedReader in;
    private BufferedWriter out;

    private Selector selector;
    private SocketChannel channel;

    public FunctionServer(int port) throws Exception {
        this.port = port;
        InetSocketAddress socketAddr = new InetSocketAddress("localhost", port);
        mainServer = SocketChannel.open(socketAddr);
    }

    public void listen() throws Exception {
        selector = Selector.open();
        channel.configureBlocking(false);
        //todo: here
    }

    public int listenFunctionCode() throws Exception {
        ByteBuffer buf = ByteBuffer.allocate(48);
        buf.clear();
        mainServer.read(buf);
        String v = new String(buf.array(), "ASCII");
        JOptionPane.showMessageDialog(null, v);
        return Integer.parseInt(buf.toString());
    }

    public int listenArgument() {
        return 0;
    }

    public void endWork() {
        try {
            mainServer.close();
            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
