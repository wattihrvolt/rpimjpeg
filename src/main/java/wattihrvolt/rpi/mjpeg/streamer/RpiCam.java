package wattihrvolt.rpi.mjpeg.streamer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class RpiCam {

	public byte[] snapshot(int width, int height) throws IOException, InterruptedException {
		ProcessBuilder pb = new ProcessBuilder(Arrays.asList("raspistill","-o","-v","-w",""+ width,"-h","" + height,"-t","100","-e","jpg"));
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

}
