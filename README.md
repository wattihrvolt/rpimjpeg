# rpimjpeg - Motion Jpeg Streamer for the RaspberryPi Cam

This is a simple POC for streaming mjpeg from the raspberry pi's camera.

## What you need 

- a Rapsberry Pi
- a Raspberry Pi Camera Model
- raspistill installed

## Notice 
The usage of com.sun.net.httpserver packages will decrease  compatibility.
There is no build in security.    


## RUN

After  running **git clone** and  **mvn package** (in the root directory of the project) there is a executable jar at /target/rpi.mjpeg.streamer-x.x.x.jar.


start the server:
```
java -cp rpi.mjpeg.streamer-0.0.1-SNAPSHOT.jar wattihrvolt.rpi.mjpeg.streamer.MjpegStreamer 8080
```
Connect to the server and view the mjpeg stream.
```
http://YOUR RASPBERRY PI's IP:8080
```

## Description

A typically mjpeg stream response looks like this
```
    		  
			HTTP/1.1 200 OK
			Content-Type: multipart/x-mixed-replace; boundary=someBoundary
			
			--someBoundary
			Content-Type: image/jpeg
			Content-length: 123
			
			[image 1 encoded jpeg data]
			
			
			--someBoundary
			Content-Type: image/jpeg
			Content-length: 456
			
			[image 2 encoded jpeg data]
			
			...
```

## TODO

Using raspivid for faster streaming
