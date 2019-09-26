package com.university;

import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class MainServer {
    private int tick = 0;
    private ServerSocketChannel serverSocketChannel;
    private SocketChannel fSocketChannel, gSocketChannel;

    public int getPort() {
        return serverSocketChannel.socket().getLocalPort();
    }

    MainServer() throws Exception {
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(0));

        // todo: init sockets
    }

    public void endServerWork() {
        try {
            serverSocketChannel.close();
            fSocketChannel.close();
            gSocketChannel.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doTick() throws InterruptedException {
        Thread.sleep(10);
        System.out.print("\rwaiting.. x" + Integer.toString(tick));
        tick++;
    }

    boolean isRunning() throws Exception{
        return (fSocketChannel.isOpen() || gSocketChannel.isOpen());
    }

    private void acceptSocketChannels() throws Exception {
        while (fSocketChannel == null || gSocketChannel == null) {
            if (fSocketChannel == null)
                fSocketChannel = serverSocketChannel.accept();
            if (gSocketChannel == null)
                gSocketChannel = serverSocketChannel.accept();
        }
    }
}
