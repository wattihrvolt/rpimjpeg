package wattihrvolt.rpi.mjpeg.streamer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

/**
 * Captures the output from the raspivid process, insert multipart boundaries and stream the content to all registered
 * clients The processing starts if at least one client registers and ends up when the last client leaves.
 */
public class MjpegProducer {

  public static final String BOUNDARY = "123456789000000000000987654321";

  private static final Logger log = Logger.getLogger(Cli.class.getName());
  private List<StreamConsumer> consumer = new CopyOnWriteArrayList<StreamConsumer>();
  private static MjpegProducer instance;
  private ProcessBuilder pb;
  private Process p = null;

  private MjpegProducer(String width, String height, String rotation, boolean timestamp) {
    if (timestamp) {
      pb = new ProcessBuilder(Arrays
          .asList("raspivid", "-cd", "MJPEG", "-w", "" + width, "-h", "" + height, "-rot", "" + rotation, "-a", "12",
              "-t", "0", "-o", "-"));
    } else {
      pb = new ProcessBuilder(Arrays
          .asList("raspivid", "-cd", "MJPEG", "-w", "" + width, "-h", "" + height, "-rot", "" + rotation, "-t", "0",
              "-o", "-"));
    }
  }

  public static MjpegProducer getInstance(String width, String heigth, String rotation, boolean timestamp) {
    if (MjpegProducer.instance == null) {
      MjpegProducer.instance = new MjpegProducer(width, heigth, rotation, timestamp);
    }
    return MjpegProducer.instance;
  }

  public void remove(StreamConsumer b) throws IOException {
    consumer.remove(b);
  }

  public void register(StreamConsumer b) throws IOException {
    consumer.add(b);
    if (consumer.size() == 1) {
      // start the producing thread
      new Thread(() -> {
        try {
          writeMjpegStream();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }).start();
    }
  }

  private void writeMjpegStream() throws IOException {
    byte[] buffer = new byte[2048];
    byte lastchar = 0x00;
    Process p = pb.start();
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); InputStream is = p.getInputStream()) {
      while (true) {
        int read = is.read(buffer);
          if (read == -1) {
              break;
          }
        for (int i = 0; i < read; i++) {
          baos.write(buffer[i]);
          if (Byte.toUnsignedInt(buffer[i]) == 0xd9 && Byte.toUnsignedInt(lastchar) == 0xff) {
            for (StreamConsumer sc : consumer) {
              try {
                OutputStream os = sc.getOutputStream();
                // end of frame
                if (StreamConsumer.Type.MULTIPART.equals(sc.getType())) {
                  os.write(("--" + BOUNDARY + "\r\n" + "Content-Type:image/jpeg\r\n" + "Content-Length:" + baos.size()
                      + "\r\n\r\n").getBytes());
                  os.write(baos.toByteArray());
                  os.write(("\r\n\r\n").getBytes());
                  os.flush();
                } else if (StreamConsumer.Type.SNAPSHOT.equals(sc.getType())) {
                  log.info("Write snapshot: " + baos.size() + " bytes");
                  os.write(baos.toByteArray());
                  os.flush();
                  os.close();
                  remove(sc);
                } else if (StreamConsumer.Type.STREAM.equals(sc.getType())) {
                  os.write(baos.toByteArray());
                  os.write(0xffd9);
                  os.flush();
                } else {
                  os.close();
                  remove(sc);
                }
              } catch (Exception e) {
                log.info("Error write to consumer ---" + e.getMessage());
                remove(sc);
                if (consumer.isEmpty()) {
                  return;
                }
              }
            }
            baos.reset();
          }
          lastchar = buffer[i];
        }
      }
    }
    p.destroy();
  }
}
