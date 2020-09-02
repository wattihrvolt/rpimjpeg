package wattihrvolt.rpi.mjpeg.streamer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class RpiCam {

	public byte[] snapshot(int width, int height, int rotation) throws IOException, InterruptedException {
		ProcessBuilder pb = new ProcessBuilder(Arrays.asList("raspistill","-o","-v","-w",""+ width,"-h","" + height,"-rot","" + rotation,"-t","100","-e","jpg"));
		Process p = pb.start();
		InputStream is = p.getInputStream();
		byte[] buffer = new byte[2048];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		while (true) {
			int read = is.read(buffer);
			if (read == -1)
				break;
			baos.write(buffer, 0, read);
		}
		is.close();
		return baos.toByteArray();
	}
	
	public void writeMjpegStream(int width, int height, int rotation, OutputStream os) throws IOException {
		
		ProcessBuilder pb = new ProcessBuilder(Arrays.asList("raspivid","-cd","MJPEG","-w",""+ width,"-h","" + height,"-rot","" + rotation,"-t","0","-o","-"));
		Process p = pb.start();
		InputStream is = p.getInputStream();
		byte[] buffer = new byte[2048];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte lastchar = 0x00;
		while (true) {
			int read = is.read(buffer);
			if (read == -1)
				break;
			for (int i= 0;i< read ;i++ ) {
				baos.write(buffer[i]);
				if(Byte.toUnsignedInt(buffer[i]) == 0xd9 && Byte.toUnsignedInt(lastchar) == 0xff) {
					// end of frame
					os.write(("--123456789000000000000987654321\r\n" + "Content-Type:image/jpeg\r\n" + "Content-Length:" + baos.size() + "\r\n\r\n").getBytes());
					os.write(baos.toByteArray());
					os.write(("\r\n\r\n").getBytes());
					os.flush();
					baos.reset();
				}
				lastchar = buffer[i];
			}
		}
		baos.close();
		os.close();
		is.close();
		
	}

}
