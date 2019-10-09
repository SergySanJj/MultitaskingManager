package com.university;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import static java.nio.channels.SelectionKey.OP_READ;
import static java.nio.channels.SelectionKey.OP_WRITE;

public class MainServer {
    private ServerSocketChannel serverSocketChannel;
    private ArrayList<FunctionChannel> functionChannels;
    private Selector selector;
    private MultitaskManager parentManager;
    private Stack<FunctionArgs> functionArgs;

    MainServer(MultitaskManager parent, int fCode, int gCode, int x) throws Exception {
        parentManager = parent;
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(0));
        functionChannels = new ArrayList<>();
        functionArgs = new Stack<>();
        functionArgs.push(new FunctionArgs(fCode, x));
        functionArgs.push(new FunctionArgs(gCode, x));
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

    public void manageSelector() throws Exception {
        selector = Selector.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (selector.isOpen()) {
            selector.select();
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                try {
                    if (!functionArgs.isEmpty())
                        if (key.isAcceptable())
                            accept(key);
                    if (key.isWritable())
                        write(key);
                    if (key.isReadable())
                        read(key);
                } catch (IOException e) {
                    key.cancel();
                    try {
                        key.channel().close();
                    } catch (IOException e1) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    private void read(SelectionKey key) throws IOException {
        ByteBuffer buff = ByteBuffer.allocate(256);
        CharBuffer cbuff = buff.asCharBuffer();
        SocketChannel socketChannel = (SocketChannel) key.channel();

        int numRead = socketChannel.read(buff);
        buff.flip();

        if (buff.remaining() == 0)
            return;
        String res = cbuff.toString();
        for (FunctionChannel functionChannel : functionChannels) {
            if (functionChannel.channel.equals(socketChannel)) {
                if (Settings.echo)
                    System.out.println("Recieved " + res);
                parentManager.setFunctionResult(functionChannel.fargs.functionCode, res);
            }
        }
    }

    private void write(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();

        for (FunctionChannel functionChannel : functionChannels) {
            if (functionChannel.channel.equals(client)) {
                if (!functionChannel.passedArgs) {
                    ByteBuffer buff = ByteBuffer.allocate(256);
                    CharBuffer cbuff = buff.asCharBuffer();
                    cbuff.put(functionChannel.fargs.command());
                    cbuff.flip();

                    client.write(buff);
                    if (buff.remaining() > 0) {
                        System.out.println("Buffer cap info");
                        break;
                    }
                    if (Settings.echo)
                        System.out.println("Function info " + cbuff.toString() + " sent");

                    functionChannel.passedArgs = true;
                }
            }
        }

        key.interestOps(OP_READ);
    }

    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel server = (ServerSocketChannel) key.channel();

        SocketChannel client = server.accept();

        FunctionArgs fargs = functionArgs.pop();
        functionChannels.add(new FunctionChannel(client, fargs));
        client.configureBlocking(false);
        client.register(selector, OP_WRITE);
        if (Settings.echo)
            System.out.println("Accepted connection from " + client);
    }

    class FunctionArgs {
        public int x;
        public int functionCode;

        FunctionArgs(int functionCode, int x) {
            this.functionCode = functionCode;
            this.x = x;
        }

        public String command() {
            return Integer.toString(functionCode) + " " + Integer.toString(x);
        }
    }

    class FunctionChannel {
        private SocketChannel channel;
        public FunctionArgs fargs;
        public boolean passedArgs = false;

        FunctionChannel(SocketChannel channel, FunctionArgs fargs) {
            this.channel = channel;
            this.fargs = fargs;
        }

        public SocketChannel socketChannel() {
            return channel;
        }
    }
}
