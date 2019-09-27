package com.university;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
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
        Thread.sleep(100);
        System.out.print("\rwaiting.. x" + Integer.toString(tick));
        tick++;
    }

    boolean isRunning() {
        try {
            return (fSocketChannel.isOpen() || gSocketChannel.isOpen());
        } catch (Exception e) {
            return false;
        }
    }

    public void acceptSocketChannels() throws Exception {
        while (fSocketChannel == null || gSocketChannel == null) {
            if (fSocketChannel == null)
                fSocketChannel = serverSocketChannel.accept();
            if (gSocketChannel == null)
                gSocketChannel = serverSocketChannel.accept();
        }
    }

    public void sendFunctionCodes(int fCode, int gCode) throws Exception {
        sendFunctionCode(fCode, fSocketChannel);
        sendFunctionCode(gCode, gSocketChannel);
    }

    private void sendFunctionCode(int functionCode, SocketChannel channel) throws Exception {
        ByteBuffer buf = ByteBuffer.allocate(48);
        buf.clear();
        buf.put(Integer.toString(functionCode).getBytes());

        while (buf.hasRemaining()) {
            channel.write(buf);
        }
    }

    public void sendFunctionArgument(int x) {

    }
}
