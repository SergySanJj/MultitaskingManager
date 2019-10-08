package com.university;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import static java.nio.channels.SelectionKey.OP_READ;
import static java.nio.channels.SelectionKey.OP_WRITE;

public class MainServer {
    private int tick = 0;
    private ServerSocketChannel serverSocketChannel;
    private ArrayList<FunctionChannel> functionChannels;
    private Selector selector;
    private MultitaskManager parentManager;
    private Stack<FunctionArgs> functionArgs;

    MainServer(MultitaskManager parent, int x) throws Exception {
        parentManager = parent;
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(0));
        functionChannels = new ArrayList<>();
        functionArgs = new Stack<>();
        functionArgs.push(new FunctionArgs(0, x));
        functionArgs.push(new FunctionArgs(1, x));
    }

    public int getPort() {
        return serverSocketChannel.socket().getLocalPort();
    }

    public void endServerWork() {
        try {
            serverSocketChannel.close();
            for (FunctionChannel channel : functionChannels)
                channel.channel.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean isRunning() {
        try {
            for (FunctionChannel channel : functionChannels)
                if (channel.channel.isOpen())
                    return true;
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    class FunctionArgs {
        public int x;
        public int functionCode;

        FunctionArgs(int functionCode, int x) {
            this.functionCode = functionCode;
            this.x = x;
        }

        public String commandX() {
            return "x" + Integer.toString(x);
        }

        public String commandFunction() {
            return "f" + Integer.toString(functionCode);
        }

        public String command() {
            return Integer.toString(functionCode) + " " + Integer.toString(x);
        }
    }

    class FunctionChannel {
        private SocketChannel channel;
        public FunctionArgs fargs;
        public boolean hasX;
        public boolean hasFunctionCode;
        public boolean passedArgs = false;

        FunctionChannel(SocketChannel channel, FunctionArgs fargs) {
            this.channel = channel;
            this.fargs = fargs;
        }

        public SocketChannel socketChannel() {
            return channel;
        }
    }

    public void manageSelector() throws Exception {
        selector = Selector.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

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
                    if (key.isAcceptable() && !functionArgs.isEmpty()) {
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();

                        SocketChannel client = server.accept();

                        FunctionArgs fargs = functionArgs.pop();
                        functionChannels.add(new FunctionChannel(client, fargs));
                        client.configureBlocking(false);
                        // Accepts client and registers it with the selector
                        client.register(selector, OP_READ);
                        System.out.println("Accepted connection from " + client);
                    }

                    // Checks if the socket is ready for writing data
                    if (key.isWritable()) {
                        SocketChannel client = (SocketChannel) key.channel();

                        for (FunctionChannel functionChannel : functionChannels) {
                            if (functionChannel.channel.equals(client)) {
                                if (!functionChannel.passedArgs) {
                                    ByteBuffer buff = ByteBuffer.allocate(32);
                                    buff.clear();
                                    buff.put(functionChannel.fargs.command().getBytes());
                                    client.write(buff);
                                    if (buff.remaining() > 0) {
                                        System.out.println("Buffer cap info");
                                        break;
                                    }
                                    System.out.println("Function info " + functionChannel.fargs.command() + " sent");

                                    functionChannel.passedArgs = true;
                                }
                            }
                        }

                        key.interestOps(OP_WRITE);
                    }

                    if (key.isReadable()) {
                        ByteBuffer readBuffer = ByteBuffer.allocate(32);
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        readBuffer.clear();
                        int numRead = socketChannel.read(readBuffer);
                        String res = StandardCharsets.UTF_8.decode(readBuffer).toString();
                        for (FunctionChannel functionChannel : functionChannels) {
                            if (functionChannel.channel.equals(socketChannel)) {
                                parentManager.setFunctionResult(functionChannel.fargs.functionCode, 0.0);
                                System.out.println("Recieved " + res);
                            }
                        }

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

    private void sendFunctionCode(int functionCode, SocketChannel channel) throws Exception {
        ByteBuffer buf = ByteBuffer.allocate(48);
        buf.clear();
        buf.put(Integer.toString(functionCode).getBytes());

        while (buf.hasRemaining()) {
            channel.write(buf);
        }
    }


}
