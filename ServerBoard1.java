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

public class ServerBoard1 {

    public static void main(String[] args) {

		try {
            ServerSocket s = new ServerSocket(0);
			System.out.println("listening on port: " + s.getLocalPort());
        }
        catch (IOException e) {
            System.out.println("Exception " + "Finding an open port failed: " + e.toString());
        }
/*

        sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // get the message from the text view
                String messageText = message.getText();
                // add message to the message area
                messagesArea.append("\n" + messageText);
                // send the message to the client
                mServer.sendMessage(messageText);
                // clear text
                message.setText("");
            }
        }); */
	
	//we start the server

        TCPServer mServer = new TCPServer(new TCPServer.OnMessageReceived() {
                    @Override
                    //this method declared in the interface from TCPServer class is implemented here
                    //this method is actually a callback method, because it will run every time when it will be called from
                    //TCPServer class (at while)
                    public void messageReceived(String message) {
						BufferedWriter bw = null;
						FileWriter fw = null;

						//pattern: day + " " + good/bad + " " + tomorrowAverageTemp + " " + todayAverageTemp
						Pattern p = Pattern.compile("[1-9] [a-zA-Z]* [-+]?[0-9]*\\.[0-9]+ [-+]?[0-9]*\\.[0-9]+"); // 
						Matcher m = p.matcher(message);
						String realData = "";

						List<String> lines = new ArrayList<String>();
						while (m.find()) {
							//System.out.println("Found: " + m.group());
							realData = realData + m.group() + System.getProperty("line.separator");
							lines.add(m.group());
						}

						try {


							fw = new FileWriter("data3.txt");
							bw = new BufferedWriter(fw);
							bw.write(realData);

							System.out.println("Done");

						} catch (IOException e) {

							e.printStackTrace();

						} finally {

							try {

								if (bw != null)
									bw.close();

								if (fw != null)
									fw.close();

							} catch (IOException ex) {

								ex.printStackTrace();

							}

						}

					
                    }
                });
                mServer.start();
    }
}
