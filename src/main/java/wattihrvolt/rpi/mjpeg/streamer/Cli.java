package wattihrvolt.rpi.mjpeg.streamer;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Command Line Interface
 */
public class Cli {
    private static final Logger log = Logger.getLogger(Cli.class.getName());
    private String[] args = null;
    private Options options = new Options();

    // defalut values
    private int port = 8080;
    private String width = "640";
    private String heigth = "480";
    private String rotation = "0";
    private boolean timestamp=false;

    public Cli(String[] args) {

        this.args = args;
        options.addOption("h", "help", false, "show help.");
        options.addOption("w", "width", true, "set the image width .");
        options.addOption("b", "heigth", true, "set the image heigth.");
        options.addOption("r", "rotation", true, "set image rotation in deg 0-359.");
        options.addOption("t", "timestamp", false, "include a timestamp in video output.");

        options.addOption("p", "port", true, "set the port.");

    }

    public void parse() {
        CommandLineParser parser = new BasicParser();

        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);

            if (cmd.hasOption("h"))
                help();

            if (cmd.hasOption("w")) {
                width = cmd.getOptionValue("w");
            }
            if (cmd.hasOption("b")) {
                heigth = cmd.getOptionValue("b");
            }
            if (cmd.hasOption("t")) {
                timestamp = true;
            }
            if (cmd.hasOption("r")) {
                rotation = cmd.getOptionValue("r");
            }

            if (cmd.hasOption("p")) {
                port = Integer.valueOf(cmd.getOptionValue("p"));
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


    public String getWidth() {
        return width;
    }


    public String getHeigth() {
        return heigth;
    }

    public String getRotation() {
        return rotation;
    }


    public boolean getTimestamp() {
        return timestamp;
    }
}