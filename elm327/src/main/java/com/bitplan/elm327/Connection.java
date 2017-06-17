/**
 *
 * This file is part of the https://github.com/BITPlan/can4eve open source project
 *
 * Copyright 2017 BITPlan GmbH https://github.com/BITPlan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *  You may obtain a copy of the License at
 *
 *  http:www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
    
    public String getTitle();

    public void setTitle(String title);

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
     * pause for given amount of time
     * @param millis
     * @param nanos
     */
    public void pause(long millis, int nanos);
    
    /**
     * get a response
     * @param request - the request to get the response for
     * @return the packet which is returned for the request
     */
    public Packet getResponse(Packet request);
    
    /**
     * get the responseHandler for this communication (if any)
     * @return - the ResponseHandler or null if there is none
     */
    public ResponseHandler getResponseHandler();
    
    /**
     * set the responseHandler for this communication
     * @param handler - the new ResponseHandler
     */
    public void setResponseHandler(ResponseHandler handler);
    
    public boolean isHandleResponses();

    public void setHandleResponses(boolean handleResponses);

    public void close() throws IOException;
    
    /**
     * am I  alive?
     * @return whether the connection Thread is alive
     */
    public boolean isAlive();
}
