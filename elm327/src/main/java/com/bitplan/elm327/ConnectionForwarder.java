package com.bitplan.elm327;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

import javax.net.ServerSocketFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wf on 03.06.17.
 * server for Connections
 * allows serving local Connections e.g. via USB, Bluetooth
 * can also be used to forward on socket based connection to a different port
 */
public class ConnectionForwarder implements Debugable {
    ServerSocket serverSocket;
    Socket clientSocket=null;
    Piper piper=null;
    private Thread serverThread;
    public static final int MAX_CLIENTS=1;
    public static final int DEFAULT_PORT = 35000;
    int portNumber;
    Log log;

    boolean running = false;

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    /**
     * create a server socket for the given port
     *
     * @param pPort
     * @throws IOException
     */
    public void createServerSocket(int pPort) throws IOException {
        portNumber = pPort;
        ServerSocket lServerSocket = ServerSocketFactory.getDefault().createServerSocket(portNumber);
        setServerSocket(lServerSocket);
    }

    public Thread getServerThread() {
        return serverThread;
    }

    /**
     * start a Server for the given local Connection
     * @param con
     */
    public void startServer(final Connection con) {

        Runnable serverTask = new Runnable() {

            @Override
            public void run() {
                try {
                    if (isDebug())
                        log(
                                String.format("Waiting for client to connect via port %5d...",
                                        getServerSocket().getLocalPort()));
                    running = true;
                    while (running) {
                        Socket newClientSocket = getServerSocket().accept();
                        if (piper!=null) {
                            if (isDebug())
                                log("halting existing piper");
                            piper.halt();
                            if (clientSocket!=null)
                                clientSocket.close();
                        }
                        clientSocket=newClientSocket;
                        if (isDebug())
                            log(String.format(
                              "Accepting connection via port %5d", clientSocket.getPort()));
                        piper=new Piper(con.getInput(),con.getOutput(),clientSocket.getInputStream(),clientSocket.getOutputStream());
                        piper.setLog(getLog());
                        piper.pipe();

                    }
                    getServerSocket().close();
                } catch (IOException e) {
                    log.handle("Unable to process client request",e);
                }
            }
        };
        serverThread = new Thread(serverTask);
        serverThread.start();
    }

    /**
     * stop the server
     */
    public void stop() {
        running = false;
    }

    @Override
    public boolean isDebug() {
        return log != null;
    }

    @Override
    public void log(String msg) {
        if (log != null) {
            log.log(msg);
        }
    }

    @Override
    public void setLog(Log log) {
        this.log = log;
    }

    @Override
    public Log getLog() {
        return log;
    }

    @Override
    public void handle(String msg, Throwable th) {
        LogImpl.handle(log,msg,th);
    }
}
