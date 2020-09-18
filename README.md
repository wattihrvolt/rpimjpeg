# rpimjpeg - Motion Jpeg Streamer for the RaspberryPi Cam

... a simple solution for streaming mjpeg from the raspberry pi's camera. 

## Features

- no additional server software required
- supports up to 5 clients in parallel (configurable)
- command line interface for inital settings (width, heigth...)


## What you need 

### to run

- a Rapsberry Pi (testet with B+ and Zero W)
- a Raspberry Pi Camera Model
- raspistill/raspivid installed (comes with raspbian, if pi's cam  enabled)

### to build
- java 8 jdk (oracle) installed, com.sun.net.httpserver packages will be used.
- maven 

### to view 
- a Browser
- or vlc (for example)


## Build and RUN

After  running **git clone** and  **mvn clean compile assembly:single** (in the root directory of the project) there is a executable jar at /target/rpi.mjpeg.streamer-0.0.1-SNAPSHOT-jar-with-dependencies.jar.


### start the server

with default values: (resolution: 640x480, port: 8080)

```java -cp rpi.mjpeg.streamer-0.0.1-SNAPSHOT-jar-with-dependencies.jar wattihrvolt.rpi.mjpeg.streamer.MjpegStreamerApplication```

or with parameters: (resolution: 1024x800, port: 8090)

```java -cp rpi.mjpeg.streamer-0.0.1-SNAPSHOT-jar-with-dependencies.jar wattihrvolt.rpi.mjpeg.streamer.MjpegStreamerApplication -p 8090 -w 1024 -b 800```

use -h for help.


## View 

### Open the url in your browser:

```http://<HOSTNAME_OR_IP>:PORT```

or to directly take a snapshot

```http://<HOSTNAME_OR_IP>:PORT/shapshot```

### connect to the mjepg stream
for example via vlc - open NetworkStream:

```http://<HOSTNAME_OR_IP>:PORT/stream```


## Optional - Run as service

To start this as a service, you can use the following start/stop template
 
* sudo vi /etc/init.d/rpicam

* edit content of /etc/init.d/rpicam

```
#!/bin/sh
### BEGIN INIT INFO
# Provides:          rpi.mjpeg.streamer
# Required-Start:    
# Required-Stop:     
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
# Short-Description: rpicam service
# Description:       longer description here
### END INIT INFO

SERVICE_NAME=RpiCam
PATH_TO_JAR=/home/pi/rpi.mjpeg.streamer-0.0.1-SNAPSHOT-jar-with-dependencies.jar
MAIN_CLASS=wattihrvolt.rpi.mjpeg.streamer.MjpegStreamerApplication
PARAMETERS='-w 1024 -b 800 -t -p 8080'
PID_PATH_NAME=/tmp/RpiCam-pid
case $1 in
    start)
        echo "Starting $SERVICE_NAME ..."
        if [ ! -f $PID_PATH_NAME ]; then
            nohup java -cp $PATH_TO_JAR $MAIN_CLASS $PARAMETERS /tmp 2>> /dev/null >> /dev/null &
            echo $! > $PID_PATH_NAME
            echo "$SERVICE_NAME started ..."
        else
            echo "$SERVICE_NAME is already running ..."
        fi
    ;;
    stop)
        if [ -f $PID_PATH_NAME ]; then
            PID=$(cat $PID_PATH_NAME);
            echo "$SERVICE_NAME stoping ..."
            kill $PID;
            echo "$SERVICE_NAME stopped ..."
            rm $PID_PATH_NAME
        else
            echo "$SERVICE_NAME is not running ..."
        fi
    ;;
    restart)
        if [ -f $PID_PATH_NAME ]; then
            PID=$(cat $PID_PATH_NAME);
            echo "$SERVICE_NAME stopping ...";
            kill $PID;
            echo "$SERVICE_NAME stopped ...";
            rm $PID_PATH_NAME
            echo "$SERVICE_NAME starting ..."
            nohup java -cp $PATH_TO_JAR $MAIN_CLASS $PARAMETERS /tmp 2>> /dev/null >> /dev/null &
            echo $! > $PID_PATH_NAME
            echo "$SERVICE_NAME started ..."
        else
            echo "$SERVICE_NAME is not running ..."
        fi
    ;;
esac 

```

* sudo chmod +x /etc/init.d/rpicam 

* sudo update-rc.d rpicam defaults

rpicam starts in the specified runlevels

you can use:

```
sudo /etc/init.d/rpicam start
sudo /etc/init.d/rpicam stop
sudo /etc/init.d/rpicam restart 
```
to start stop and restart the service


