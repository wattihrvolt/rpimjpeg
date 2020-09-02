package wattihrvolt.rpi.mjpeg.streamer;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Cli {
 private static final Logger log = Logger.getLogger(Cli.class.getName());
 private String[] args = null;
 private Options options = new Options();


 private int port=8080;
 private int width=640;
 private int heigth=480;
 
 public Cli(String[] args) {

  this.args = args;
  
 

  options.addOption("h", "help", false, "show help.");
  options.addOption("w", "width", true, "set the image width .");
  options.addOption("b", "heigth",true, "set the image heigth.");
  options.addOption("p","port", true, "set the port." );

 }

 public void parse() {
  CommandLineParser parser = new BasicParser();

  CommandLine cmd = null;
  try {
   cmd = parser.parse(options, args);

   if (cmd.hasOption("h"))
    help();

   if (cmd.hasOption("w")) {
    width = Integer.valueOf(cmd.getOptionValue("w"));
    log.log(Level.INFO, "setting width: " + width);
   }
   if (cmd.hasOption("b")) {
	   heigth = Integer.valueOf(cmd.getOptionValue("b"));
	    log.log(Level.INFO, "setting heigth" + heigth);
	   }
   if(cmd.hasOption("p")) {
	   port = Integer.valueOf(cmd.getOptionValue("p"));
	   log.log(Level.INFO, "Setting port" + port);
   }
   /*else {
    log.log(Level.SEVERE, "Missing options");
    help();
   }*/

  } catch (ParseException e) {
   log.log(Level.SEVERE, "Failed to parse comand line properties", e);
   help();
  }
 }

 private void help() {
  // This prints out some help
  HelpFormatter formater = new HelpFormatter();

  formater.printHelp("Main", options);
  System.exit(0);
 }
 
 public int getPort() {
	return port;
}


public int getWidth() {
	return width;
}


public int getHeigth() {
	return heigth;
}

 
}