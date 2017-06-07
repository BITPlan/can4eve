package com.bitplan.elm327;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by wf on 03.06.17.
 */

public class ConnectionImpl extends Thread implements Connection {

    boolean sendLineFeed = true;
    boolean receiveLineFeed = false;

    InputStream input;
    OutputStream output;
    long timeOut = DEFAULT_TIMEOUT;
    Log log;
    boolean running = false;
    long pauses = 0;

    public static long DEFAULT_TIMEOUT = 250; // 250 millisecs
    final static int BUFFER_SIZE = 8192;

    // regular expression to check that an elm327 response is "complete"
    // while there is no linefeed received
    final static Pattern snippetPattern = Pattern.compile(
      "(.*\\>)|STOPPED|BUFFER FULL|CAN ERROR",
      Pattern.MULTILINE | Pattern.DOTALL);

    LinkedBlockingQueue<String> responses = new LinkedBlockingQueue<String>();

    public boolean isSendLineFeed() {
        return sendLineFeed;
    }

    public void setSendLineFeed(boolean sendLineFeed) {
        this.sendLineFeed = sendLineFeed;
    }

    public boolean isReceiveLineFeed() {
        return receiveLineFeed;
    }

    public void setReceiveLineFeed(boolean receiveLineFeed) {
        this.receiveLineFeed = receiveLineFeed;
    }

    @Override
    public InputStream getInput() {
        return input;
    }

    @Override
    public void setInput(InputStream input) {
        this.input = input;
    }

    @Override
    public OutputStream getOutput() {
        return output;
    }

    @Override
    public void setOutput(OutputStream output) {
        this.output = output;
    }



    @Override
    public void run() {
        // prepare infinite loop
        Reader in = null;
        try {
            in = new InputStreamReader(this.getInput(), "UTF-8");
            BufferedReader reader = new BufferedReader(in);
            final char[] buffer = new char[BUFFER_SIZE];

            String line = "";
            running = true;
            if (this.isDebug()) {
                log("ready for reading");
            }
            while (running) {
                if (this.receiveLineFeed) {
                    line = reader.readLine();
                    if (line.length()>0)
                        this.addResponse(line);
                } else {
                    // while initializing there may be no linefeeds yet
                    // we have to read the stream char for char ...
                    // is there data in the input stream?
                    if (!reader.ready()) {
                        // wait a very small of time (0,2 milliseconds) - to not overload
                        // the CPU too much with
                        // busy polling
                        int microsecs = 200;
                        pause(0, microsecs);
                        pauses += microsecs;
                    } else {
                        int readCount = reader.read(buffer);
                        if (readCount > 0) {
                            StringBuffer strBuf = new StringBuffer();
                            strBuf.append(buffer, 0, readCount);
                            String snippet = strBuf.toString();
                            if (isDebug()) {
                                log(String.format("read %3d char '%s'",readCount,snippet));
                            }
                            addSnippet(snippet);
                        }
                    }
                }
            }
        } catch (Throwable th) {
            handle("run failed", th);
        }
    }

    String response="";
    /**
     * we are not in receiveLineFeed Mode and might receive any kind of snippets
     * that might be just part of a reponse or span multiple responses - we have to
     * look at the snippet and split it up into responses
     * @param snippet
     */
    public void addSnippet(String snippet) {
        response+=snippet;
        Matcher m=snippetPattern.matcher(response);
        boolean responseComplete=m.find();
        if (responseComplete) {
            if (isDebug()) {
                log (String.format("complete response is '%s' with %2d groups",response.replace("\r\n", "|CRLF|"),m.groupCount()));
            }
            synchronized (this) {
                responses.add(response);
                response = "";
            }
        }
    }

    /**
     * add the given string to the responses
     * @param response
     */
    public void addResponse(String response) {
        if (isDebug()) {
            log("response is: '"+response+"'");
        }
        responses.add(response);
    }

    /**
     * pause for the given number of milliseconds
     *
     * @param millis
     */
    public void pause(long millis, int nanos) {
        try {
            Thread.sleep(millis, nanos);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    @Override
    public void halt() {
        running = false;
    }

    @Override
    public void close() throws IOException {
        halt();
        if (getInput()!=null)
            getInput().close();
        if (getOutput()!=null) {
            getOutput().close();
        }
    }

    @Override
    public void connect(Connection conn) throws IOException {
       throw new RuntimeException("not implemented");
    }

    @Override
    public void connect(Socket socket) throws IOException {
        setInput(socket.getInputStream());
        setOutput(socket.getOutputStream());
    }

    @Override
    public void connect(File device) throws IOException {
        setInput(new FileInputStream(device));
        setOutput(new FileOutputStream(device));
    }

    @Override
    public Packet output(String msg) throws IOException {
        Packet result = new PacketImpl();
        result.setData(msg);
        if (this.getOutput() != null) {
            if (msg != null) {
                if (isDebug()) {
                    log(String.format("sending %3d chars: '%s' %s",msg.length(),msg,sendLineFeed?"CRLF":""));
                }
                if (sendLineFeed)
                    msg += "\r\n";
                // this.currentOutput = msg;
                getOutput().write(msg.getBytes());
                getOutput().flush();
                result.updateTimeStamp();
            }
        }
        return result;
    }

    @Override
    public Packet send(String msg) throws IOException {
        Packet request = output(msg);
        return getResponse(request);
    }

    @Override
    public long getTimeout() {
        return timeOut;
    }

    @Override
    public void setTimeout(long timeout) {
        this.timeOut = timeout;
    }

    @Override
    public Packet getResponse(Packet request) {
        Packet response = new PacketImpl();
        String data = null;
        try {
            data = responses.poll(timeOut, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            // ignore - handle like timeOut
        }
        response.setData(data);
        response.updateTimeStamp();
        response.setRequest(request);
        response.setResponse(response);
        request.setResponse(response);
        return response;
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
