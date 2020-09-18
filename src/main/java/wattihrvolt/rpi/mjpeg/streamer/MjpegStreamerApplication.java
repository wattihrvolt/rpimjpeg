package wattihrvolt.rpi.mjpeg.streamer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import static java.net.InetAddress.getLocalHost;

/**
 * Simple Mjpeg Streamer from the Raspberry Pi Cam
 *
 */

public class MjpegStreamerApplication {

    public static void main(String[] args) throws IOException {

        final int nThreads = 5; // Max parallel clients

        Cli cli = new Cli(args);
        cli.parse();

        HttpServer server = HttpServer.create(new InetSocketAddress(cli.getPort()), 0);
        server.createContext("/", (rootHandler) -> {
            byte[] response = "<!DOCTYPE html><html><body><button onclick=\"window.open('./snapshot','_blank')\">Snapshot</button><img src=\"./mjpeg\"></body></html>".getBytes();
            rootHandler.sendResponseHeaders(200, response.length);
            final OutputStream os = rootHandler.getResponseBody();
            os.write(response);
            os.flush();
            rootHandler.close();
        });

        server.createContext("/mjpeg", (mjpegHandler) -> {
            Headers h = mjpegHandler.getResponseHeaders();
            h.set("Content-Type", "multipart/x-mixed-replace; boundary="+MjpegProducer.BOUNDARY);
            registerStreamConsumer(cli, mjpegHandler, StreamConsumer.Type.MULTIPART);
        });

        server.createContext("/snapshot", (mjpegHandler) -> {
            Headers h = mjpegHandler.getResponseHeaders();
            h.set("Content-Type", "application/octet-stream");
            h.add("Content-Disposition", "attachment; filename=\"snapshot.jpg\"");
            registerStreamConsumer(cli, mjpegHandler, StreamConsumer.Type.SNAPSHOT);
        });

        server.createContext("/stream", (mjpegHandler) -> {
            Headers h = mjpegHandler.getResponseHeaders();
            h.set("Content-Type", "video/x-motion-jpeg");
            registerStreamConsumer(cli, mjpegHandler, StreamConsumer.Type.STREAM);
        });

        // Use max. nThreads
        server.setExecutor( Executors.newFixedThreadPool(nThreads));
        server.start();

        String hostName;
        try {
            hostName = getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            hostName = "<HOSTNAME_OR_IP>";
        }

        System.out.println("Server is running on port: "+cli.getPort());
        System.out.println("Browser Url: http://"+hostName+":"+cli.getPort());
        System.out.println("Snapshot Url: http://"+hostName+":"+cli.getPort()+"/snapshot");
        System.out.println("Stream Url: http://"+hostName+":"+cli.getPort()+"/stream");
    }

    private static void registerStreamConsumer(Cli cli, HttpExchange mjpegHandler, StreamConsumer.Type type) throws IOException {
        mjpegHandler.sendResponseHeaders(200, 0);
        MjpegProducer mjpegProducer = MjpegProducer.getInstance(cli.getWidth(), cli.getHeigth(), cli.getRotation(), cli.getTimestamp());
        OutputStream os = mjpegHandler.getResponseBody();
        try {
            mjpegProducer.register(new StreamConsumer(os, type));
        } catch (IOException e) {
            e.printStackTrace();
            mjpegHandler.close();
            os.close();

        }
    }

}