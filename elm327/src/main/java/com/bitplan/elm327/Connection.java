package com.bitplan.elm327;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by wf on 03.06.17.
 */

public interface Connection extends Debugable, Runnable {

    public boolean isSendLineFeed();

    public void setSendLineFeed(boolean sendLineFeed);

    public boolean isReceiveLineFeed();

    public void setReceiveLineFeed(boolean receiveLineFeed);

    /**
     * get the InputStream
     * @return - the current InputStream
     */
    public InputStream getInput();

    /**
     * set the InputStream
     * @param input - the new InputStream
     */
    public void setInput(InputStream input);

    /**
     * get the OutputStream
     * @return - the current OutputStream
     */
    public OutputStream getOutput();

    /**
     * set the OutputStream
     * @param output - the new OutputStream
     */
    public void setOutput(OutputStream output);

    /**
     * start communicating via this connection on a separate thread
     * that reads from the input and potentially writes to the output (e.g. if echo is on)
     */
    public void start();

    /**
     * halt the connection
     */
    public void halt();

    /**
     * connect my input to the output of another connection and the output of the
     * other connection to my input
     * @param conn
     * @throws IOException it there is a problem
     */
    public void connect(Connection conn) throws IOException;

    /**
     * connect me to the given socket
     * @param socket
     * @throws IOException
     */
    public void connect(Socket socket) throws IOException;

    /**
     * connect me to the given device
     * @param device
     * @throws IOException
     */
    public void connect(File device) throws IOException;

    /**
     * output the given message to the output
     * @param msg
     * @throws IOException
     */
    public Packet output(String msg) throws IOException;

    /**
     * send the given message to the output
     * and get the response
     * @param msg
     * @throws IOException
     */
    public Packet send(String msg) throws IOException;

    /**
     * @return the timeout
     */
    public long getTimeout();

    /**
     * @param timeout the timeout to set
     */
    public void setTimeout(long timeout);
    /**
     * get a response
     * @param request - the request to get the response for
     * @return the packet which is returned for the request
     */
    public Packet getResponse(Packet request);

    public void close() throws IOException;
}
