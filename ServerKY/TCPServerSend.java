import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The class extends the Thread class so we can receive and send messages at the same time
 */
public class TCPServerSend extends Thread {

    public static final int SERVERPORT = 55160;
    private boolean running = false;
    private PrintWriter mOut;
    private OnMessageReceived messageListener;
    static  String Ranswer;

    public static void main(String[] args) {
	Ranswer = args[args.length - 1];
	//we start the server
        TCPServerSend mServer = new TCPServerSend(new TCPServerSend.OnMessageReceived() {
                    @Override
                    //this method declared in the interface from TCPServer class is implemented here
                    //this method is actually a callback method, because it will run every time when it will be called from
                    //TCPServer class (at while)
                    public void messageReceived(String message) {}});
	       
	mServer.start();
        

    }

    /**
     * Constructor of the class
     * @param messageListener listens for the messages
     */
    public TCPServerSend(OnMessageReceived messageListener) {
        this.messageListener = messageListener;
    }

    /**
     * Method to send the messages from server to client
     * @param message the message sent by the server
     */
    public void sendMessage(String message){
        if (mOut != null && !mOut.checkError()) {
	    System.out.println("mout in not null and it doesn't give error");
            mOut.println(message);
	    System.out.println("message : " + message);
            mOut.flush();
	    System.out.println("flushed!");
        }
    }

    @Override
    public void run() {
        super.run();

        running = true;

        try {
            System.out.println("S: Connecting...");

            //create a server socket. A server socket waits for requests to come in over the network.
            ServerSocket serverSocket = new ServerSocket(SERVERPORT);

            //create client socket... the method accept() listens for a connection to be made to this socket and accepts it.
            Socket client = serverSocket.accept();
            System.out.println("S: Receiving...");

            try {

                //sends the message to the client
                mOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
		sendMessage(Ranswer);
		
                //read the message received from client
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                //in this while we wait to receive messages from client (it's an infinite loop)
                //this while it's like a listener for messages
                if (running) {
                    String message = in.readLine();

                    if (message != null && messageListener != null) {
                        //call the method messageReceived from ServerBoard class
                        messageListener.messageReceived(message);
                    }
                } 
		String message = in.readLine();

                if (message != null && messageListener != null) {
                    //call the method messageReceived from ServerBoard class
                    messageListener.messageReceived(message);
                } 

            } catch (Exception e) {
                System.out.println("S: Error");
                e.printStackTrace();
            } finally {
                client.close();
                System.out.println("S: Done.");
            }

        } catch (Exception e) {
            System.out.println("S: Error");
            e.printStackTrace();
        }

    }

    //Declare the interface. The method messageReceived(String message) will must be implemented in the ServerBoard
    //class at on startServer button click
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }



}
