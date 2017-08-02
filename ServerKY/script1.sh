#!/bin/bash

javac ServerBoard1.java TCPServer.java
java ServerBoard1 TCPServer
RASPUNS=`Rscript trees.R | grep "Your day will be good with a percentage of"`
echo $RASPUNS
javac TCPServerSend.java
java TCPServerSend $RASPUNS
