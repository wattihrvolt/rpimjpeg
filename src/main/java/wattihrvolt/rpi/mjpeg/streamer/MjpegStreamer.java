package wattihrvolt.rpi.mjpeg.streamer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

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
		
		int port = args.length == 1?Integer.parseInt(args[0]):80;
		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
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
			while (true) {
				try {
					byte[] img = cam.snapshot(640, 480);
					os.write(("--123456789000000000000987654321\r\n" + "Content-Type:image/jpeg\r\n" + "Content-Length:" + img.length + "\r\n\r\n").getBytes());
					os.write(img);
					os.write(("\r\n\r\n").getBytes());
					os.flush();
				} catch (InterruptedException e) {
					e.printStackTrace();
					mjpegHandler.close();
					os.close();
				} 
			}
		});

		server.setExecutor(null);
		server.start();
		System.out.println("Server is running on port: "+port);
	}

}