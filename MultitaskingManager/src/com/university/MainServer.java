package com.university;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class MainServer {
    private int tick = 0;
    private ServerSocketChannel serverSocketChannel;
    private SocketChannel fSocketChannel, gSocketChannel;
    private Selector selector;

    public int getPort() {
        return serverSocketChannel.socket().getLocalPort();
    }

    MainServer() throws Exception {
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(0));
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

    public void manageSelector() throws Exception {
        selector = Selector.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        final ByteBuffer msg = ByteBuffer.wrap("Hi!\r\n".getBytes());

        while (true) {

            // Waits for new events to process; blocks until the next incoming event
            selector.select();

            // Obtains all SelectionKey instances that received events
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                try {
                    // Checks if the event is a new connection ready to be accepted
                    if (key.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();

                        SocketChannel client = server.accept();
                        client.configureBlocking(false);
                        // Accepts client and registers it with the selector
                        client.register(
                                selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ, msg);
                        System.out.println("Accepted connection from " + client);
                    }
                    // Checks if the socket is ready for writing data
                    if (key.isWritable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        while (buffer.hasRemaining()) {
                            // Writes data to the connected client
                            if (client.write(buffer) == 0) {
                                break;
                            }
                        }
                        // Closes the connection
                        client.close();
                    }
                } catch (IOException e) {
                    key.cancel();
                    try {
                        key.channel().close();
                    } catch (IOException e1) {
                    }
                }
            }
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
