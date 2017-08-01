import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.net.ServerSocket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;

public class ServerBoardSend {

    public static void main(String[] args) {
	
	//we start the server

        TCPServerSend mServer = new TCPServerSend(new TCPServerSend.OnMessageReceived() {
                    @Override
                    //this method declared in the interface from TCPServer class is implemented here
                    //this method is actually a callback method, because it will run every time when it will be called from
                    //TCPServer class (at while)
                    public void messageReceived(String message) {}});
                mServer.start();
		
		// send the message to the client
		System.out.println("Trimitem la client mesajul");
      		mServer.sendMessage(args[args.length - 1]); //el porcentaje de good
    }
}
