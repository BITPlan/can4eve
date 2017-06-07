package com.bitplan.caneve;

import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.util.logging.Logger;
import org.kohsuke.args4j.CmdLineException;
;

/**
 * generic abstract Main class
 */
public abstract class Main {
  public static boolean testMode;
  protected static int exitCode;
  protected CmdLineParser parser;
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.obdii");
  @Option(name = "-h", aliases = {"--help"}, usage = "help\nshow this usage")
  protected boolean showHelp = false;

  @Option(name = "-d", aliases = {
    "--debug"}, usage = "debug\ncreate additional debug output if this switch is used")
  protected boolean debug = false;

  @Option(name = "-v", aliases = {
    "--version"}, usage = "showVersion\nshow current version if this switch is used")
  protected boolean showVersion = false;

  String VERSION = "0.0.1";
  String name = this.getClass().getSimpleName();
  String github;

  /**
   * show the Version
   */
  public void showVersion() {
    System.err.println(name + ": " + VERSION);
    System.err.println();
    if (github != null) {
      System.err.println(" github: " + github);
    }
  }

  /**
   * display usage
   *
   * @param msg - a message to be displayed (if any)
   */
  public void usage(String msg) {
    System.err.println(msg);
    showVersion();
    System.err.println("  usage: java "+name);
    parser.printUsage(System.err);
    exitCode = 1;
  }

  /**
   * show Help
   */
  public void showHelp() {
    usage("Help");
  }

  /**
   * handle the given Throwable
   *
   * @param t the Throwable to handle
   */
  public void handle(Throwable t) {
    System.out.flush();
    ErrorHandler.handle(t);
  }

  /**
   * main routine
   *
   * @param args - the command line arguments
   * @return - the exit statusCode
   */
  public int maininstance(String[] args) {
    parser = new CmdLineParser(this);
    try {
      parser.parseArgument(args);
      work();
      exitCode = 0;
    } catch (CmdLineException e) {
      // handling of wrong arguments
      usage(e.getMessage());
    } catch (Exception e) {
      handle(e);
      // System.exit(1);
      exitCode = 1;
    }
    return exitCode;
  }

  public abstract void work() throws Exception;

}
