package wattihrvolt.rpi.mjpeg.streamer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpServer;

/**
 * POC for a Simple Mjpeg Streamer from the Raspberry Pi Cam
 * 
 * Note: Make the package accessible! (com/sun/net/httpserver/**)
 * https://stackoverflow.com/questions/13155734/eclipse-cant-recognize-com-sun-net-httpserver-httpserver-package
 * and consider the the use of sun.* packages - this may result in poor portability
 */

public class MjpegStreamer {
	private static RpiCam cam = new RpiCam();

	public static void main(String[] args) throws IOException {
		
		Cli cli = new Cli(args);
		cli.parse();
		
		HttpServer server = HttpServer.create(new InetSocketAddress(cli.getPort()), 0);
		server.createContext("/", (rootHandler) -> {
			byte[] response = "<!DOCTYPE html><html><body><img src=\"./mjpeg\"></body></html>".getBytes();
			rootHandler.sendResponseHeaders(200, response.length);
			final OutputStream os = rootHandler.getResponseBody();
			os.write(response);
			os.flush();
			rootHandler.close();
		});

		server.createContext("/mjpeg", (mjpegHandler) -> {
			Headers h = mjpegHandler.getResponseHeaders();
			h.set("Content-Type", "multipart/x-mixed-replace; boundary=123456789000000000000987654321");
			mjpegHandler.sendResponseHeaders(200, 0);
			OutputStream os = mjpegHandler.getResponseBody();
			try {
				cam.writeMjpegStream(cli.getWidth(), cli.getHeigth(),0, os);
			} catch (IOException e) {
				e.printStackTrace();
				mjpegHandler.close();
				os.close();
			} 
		});

		server.setExecutor( Executors.newFixedThreadPool(5));
		server.start();
			
		//new Thread(()->{server.start();}).start();
		
		//Runtime.getRuntime().addShutdownHook(new OnShutdown());

			 
		
		System.out.println("Server is running on port: "+cli.getPort());
	}
	
	/*
    static void shutdown() {

        try { 
            System.out.println("Shutting down TestServer.");            
            serverInstance.httpServer.stop(0);

        } catch (Exception e) {
            e.printStackTrace();
        }

        synchronized (serverInstance) {
            serverInstance.notifyAll();
        }

    }
    */

}
