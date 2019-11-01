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
    private static final int Ccontinue = 1;
    private static final int CwithoutPrompt = 2;
    private static final int Ccancel = 3;
    private int currentState = Ccontinue;
    Scanner sc;

    private int x;
    private String fCode;
    private String gCode;

    private Stack<FunctionArgs> functionArgs;
    private ArrayList<FunctionChannel> functionChannels;
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private long startMillis;

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
        sc = new Scanner(System.in);

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
            startMillis = System.currentTimeMillis();
            long lastTime = System.currentTimeMillis();
            while (selector.isOpen() && !finished) {
                if (Settings.usePrompts && currentState == Ccontinue) {
                    if (System.currentTimeMillis() - lastTime >= Settings.maxIdleTime) {
                        startUserPrompt();
                        lastTime = System.currentTimeMillis();
                    }
                }
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

                if (currentState == Ccancel && !finished) {
                    System.out.println("User chose to cancel");
                    System.out.println(getStatus());
                    finished = true;
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
        if (checkResultReadiness()) {
            double workedFor = (System.currentTimeMillis() - startMillis) / 1000.0;
            System.out.println("Result: " + tryDoOperation());
            System.out.println("Total time: " + workedFor + " s");
            finished = true;
        }
    }

    private boolean checkResultReadiness() {
        String operationRes = tryDoOperation();
        if (operationRes.equals("NaN"))
            return false;
        return (results.containsKey(fCode) && results.containsKey(gCode))
                || operationRes.equals("0.0");
    }

    private String tryDoOperation() {
        double res = 1;
        boolean hasNaN = false;
        for (Map.Entry<String, String> el : results.entrySet()) {
            if (el.getValue().equals("NaN"))
                hasNaN = true;
            else {
                double val = Double.parseDouble(el.getValue());
                if (val < 1e-7)
                    return "0.0";
                res *= val;
            }
        }
        if (Math.abs(res) > 1E-13 && hasNaN)
            return "UNDEFINED";
        return Double.toString(res);
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

    public void forceFinish() {
        finished = true;
    }

    public String getStatus() {
        List<String> allFunctions = new ArrayList<>() {
            {
                add(fCode);
                add(gCode);
            }
        };
        StringBuilder s = new StringBuilder();
        s.append("Result can't be computed due to: \n");
        for (String fc : allFunctions) {
            if (!results.containsKey(fc)) {
                s.append("   ").append(fc).append(" has not finished\n");
            }
        }
        if (results.size() > 0)
            s.append("However next functions managed to finish: \n");
        for (Map.Entry<String, String> el : results.entrySet())
            s.append("   ").append(el.getKey()).append(" has finished with result: ").append(el.getValue()).append("\n");
        return s.toString();
    }

    private void startUserPrompt() {
        System.out.println("Functions running for too long, options: \n" +
                "continue(1)\n" +
                "continue without prompt(2)\n" +
                "cancel(3)");
        int code = sc.nextInt();
        code = code % 4;
        currentState = code;
    }
}


