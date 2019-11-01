package com.university.manager;

import com.university.Settings;
import com.university.StrFunc;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

import static java.nio.channels.SelectionKey.OP_READ;

public class Server {
    private int x;
    private String fCode;
    private String gCode;

    private Stack<FunctionArgs> functionArgs;
    private ArrayList<FunctionChannel> functionChannels;
    private ServerSocketChannel serverSocketChannel;
    Selector selector;

    private boolean finished = false;
    private final Object finishMutex = new Object();

    private Map<String, String> results;

    public Server(int x, String fCode, String gCode) {
        this.fCode = fCode;
        this.gCode = gCode;
        this.x = x;

        results = new HashMap<>();
        functionChannels = new ArrayList<>();
        functionArgs = new Stack<>();
        functionArgs.push(new FunctionArgs(fCode, x));
        functionArgs.push(new FunctionArgs(gCode, x));

        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getPort() {
        return serverSocketChannel.socket().getLocalPort();
    }

    public void start() throws Exception {
        connectChannels();
        passArguments();
        // Listen respond with selector
        listenResults();
    }

    private void connectChannels() throws Exception {
        serverSocketChannel.configureBlocking(true);
        while (functionChannels.size() < 2) {
            SocketChannel socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(true);
            FunctionArgs fargs = functionArgs.pop();
            functionChannels.add(new FunctionChannel(socketChannel, fargs));
        }
        serverSocketChannel.close();
    }

    private void passArguments() throws Exception {
        for (FunctionChannel functionChannel : functionChannels) {
            if (!functionChannel.passedArgs) {
                ByteBuffer buff = ByteBuffer.allocate(256);
                CharBuffer cbuff = buff.asCharBuffer();
                cbuff.put(functionChannel.fargs.command());
                cbuff.flip();

                functionChannel.channel.write(buff);
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

    private void listenResults() {
        try {
            selector = Selector.open();
            for (FunctionChannel functionChannel : functionChannels) {
                functionChannel.channel.configureBlocking(false);
                functionChannel.channel.register(selector, OP_READ);
            }

            while (selector.isOpen() && !finished) {

                selector.select(Settings.maxIdleTime);
                Set<SelectionKey> readyKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = readyKeys.iterator();
                while (iterator.hasNext()) {

                    SelectionKey key = iterator.next();
                    iterator.remove();

                    try {
                        if (key != null && key.isReadable())
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void read(SelectionKey key) throws IOException {
        ByteBuffer buff = ByteBuffer.allocate(256);
        CharBuffer cbuff = buff.asCharBuffer();
        SocketChannel socketChannel = (SocketChannel) key.channel();

        socketChannel.read(buff);
        buff.flip();

        if (buff.remaining() == 0)
            return;
        String res = cbuff.toString();
        for (FunctionChannel functionChannel : functionChannels) {
            if (functionChannel.channel.equals(socketChannel)) {
                if (Settings.echo)
                    System.out.println("Recieved " + res);
                addResult(functionChannel.fargs.functionCode, res);
                pollResult();
            }
        }
    }

    private void addResult(String functionCode, String result) {
        String[] fRes = StrFunc.parseNumValues(result);
        if (fRes[0].equals("1")) {
            double res = Double.parseDouble(fRes[1]);
            if (Settings.echo)
                System.out.println(fRes[1]);
            if (Math.abs(res) < 1E-12) {
                res = 0.0;
            }
            results.put(functionCode, (Double.toString(res)));
        } else {
            results.put(functionCode, "NaN");
        }
    }

    private void pollResult() {
        if (results.size() == 2) {
            System.out.println("READY");
            try {
                finished = true;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        for (Map.Entry<String, String> entry : results.entrySet()) {

        }
    }

    class FunctionArgs {
        public int x;
        public String functionCode;

        FunctionArgs(String functionCode, int x) {
            this.functionCode = functionCode;
            this.x = x;
        }

        public String command() {
            return functionCode + " " + x;
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
    }

//    private void startUserPrompt() {
//        Scanner sc = new Scanner(System.in);
//        while (currentState != Ccancel && currentState != CwithoutPrompt) {
//            try {
//                Thread.sleep(Settings.maxIdleTime);
//            } catch (Exception e) {
//                inputThread.interrupt();
//                return;
//            }
//            System.out.println("Functions running for too long, options: \n" +
//                    "continue(1)\n" +
//                    "continue without prompt(2)\n" +
//                    "cancel(3)");
//            isCurrentlyPrompted = true;
//            int code = sc.nextInt();
//            isCurrentlyPrompted = false;
//            code = code % 4;
//            currentState = code;
//        }
//        if (currentState == Ccancel && !isResultReady) {
//            System.out.println("User chose to cancel");
//            printCurrentStatus();
//            finish();
//        }
//    }
}


