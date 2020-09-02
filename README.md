# rpimjpeg - Motion Jpeg Streamer for the RaspberryPi Cam

This is a simple solution for streaming mjpeg from the raspberry pi's camera.

## What you need 

- a Rapsberry Pi
- a Raspberry Pi Camera Model
- raspistill/raspivid installed
- java 8 jdk (oracle) installed, com.sun.net.httpserver packages will be used.


## Build and RUN

After  running **git clone** and  **mvn clean compile assembly:single** (in the root directory of the project) there is a executable jar at /target/rpi.mjpeg.streamer-0.0.1-SNAPSHOT-jar-with-dependencies.jar.


start the server:
```
with default values:

java -cp rpi.mjpeg.streamer-0.0.1-SNAPSHOT-jar-with-dependencies.jar wattihrvolt.rpi.mjpeg.streamer.MjpegStreamer

or with parameters
java -cp rpi.mjpeg.streamer-0.0.1-SNAPSHOT-jar-with-dependencies.jar wattihrvolt.rpi.mjpeg.streamer.MjpegStreamer -p 8080 -w 640 -b 480

use -h for help.
```

## View 

Connect to the server and view the mjpeg stream.
Open the url in your browser.

http://YOUR RASPBERRY PI's IP:PORT


## Notice 

There is no build in security.    

