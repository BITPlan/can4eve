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
package com.bitplan.obdii.elm327;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.kohsuke.args4j.Option;

import com.bitplan.can4eve.VehicleGroup;
import com.bitplan.elm327.Connection;
import com.bitplan.elm327.LogImpl;
import com.bitplan.obdii.Main;

public class ElmSimulator extends Main {
  /**
   * current Version of the can4eve tool
   */
  public static final String VERSION = "0.0.1";

  public static final int DEFAULT_PORT = 35000;

  boolean running = false;

  @Option(name = "--port", aliases = {
      "--portnumber" }, usage = "port\nthe port to connect to")
  int portNumber = DEFAULT_PORT;

  @Option(name = "--vg", aliases = {
      "--vehicle-group" }, usage = "vehicleGroup\nthe vehicleGroup to connect to")
  String vehicleGroupName = "triplet";

  public static boolean verbose = true;
  private static ElmSimulator instance;
  private ServerSocket serverSocket;
  public Map<Integer, ELM327SimulatorConnection> simulatorConnectionsByPort = new HashMap<Integer, ELM327SimulatorConnection>();

  private Thread serverThread;

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
    setServerSocket(new ServerSocket(portNumber));
  }

  /**
   * start the Server
   */
  public void startServer() {
    // final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);

    Runnable serverTask = new Runnable() {

      @Override
      public void run() {
        try {
          if (verbose)
            System.out.println(
                String.format("ELM327 Simulator waiting for clients to connect via port %5d...",
                    getServerSocket().getLocalPort()));
          running = true;
          VehicleGroup vehicleGroup=null;
          try {
            vehicleGroup = VehicleGroup.get(vehicleGroupName);
          } catch (Exception e) {
            handle(e);
          }
          while (running) {
            Socket clientSocket = getServerSocket().accept();
            ELM327SimulatorConnection elm327SimulatorConnection = new ELM327SimulatorConnection(
                vehicleGroup);
            Connection con = elm327SimulatorConnection.getCon();
            con.setTitle(String.format("ELM327 Simulator on port %5d",clientSocket.getPort()));
            con.connect(clientSocket);
            if (debug)
              con.setLog(new LogImpl());
            con.setReceiveLineFeed(true);
            simulatorConnectionsByPort.put(clientSocket.getPort(),
                elm327SimulatorConnection);
            if (verbose)
              System.out.println(String.format(
                  "Accepting connection via port %5d", clientSocket.getPort()));
            // clientProcessingPool.execute(con);
            con.start();
          }
          getServerSocket().close();
        } catch (IOException e) {
          handle(e);
          System.err.println("Unable to process client request");
          e.printStackTrace();
        }
      }
    };
    serverThread = new Thread(serverTask);
    serverThread.start();
  }

  /**
   * return the connection for the given ClientSocket
   * 
   * @param clientSocket
   * @return the connection
   */
  public ELM327SimulatorConnection getSimulatorConnection(Socket clientSocket) {
    int clientPort = clientSocket.getLocalPort();
    ELM327SimulatorConnection con = simulatorConnectionsByPort.get(clientPort);
    return con;
  }

  /**
   * stop the simulator
   */
  public void stop() {
    running = false;
  }

  @Override
  public void work() throws Exception {
    if (this.showVersion || this.debug)
      showVersion();
    if (this.showHelp) {
      showHelp();
    } else {
      this.createServerSocket(portNumber);
      this.startServer();
      serverThread.join();
    }
  }

  /**
   * get the default instance of the ELMSimulator
   * 
   * @return the default instance with a server socket on the DEFAULT_PORT and
   *         already started
   * @throws IOException
   */
  public static ElmSimulator getInstance() throws IOException {
    if (instance == null) {
      instance = new ElmSimulator();
      instance.createServerSocket(DEFAULT_PORT);
      instance.startServer();
    }
    return instance;
  }

  /**
   * main routine
   * 
   * @param args
   */
  public static void main(String[] args) {
    ElmSimulator sim = new ElmSimulator();
    int result = sim.maininstance(args);
    if (!testMode)
      System.exit(result);
  }

}
