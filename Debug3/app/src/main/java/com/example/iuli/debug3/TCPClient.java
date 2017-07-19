package com.example.iuli.debug3;

        import android.os.AsyncTask;

        import java.io.*;
        import java.io.ByteArrayOutputStream;
        import java.net.*;

public class TCPClient extends AsyncTask<String, Integer, String> {

    private final static String serverIP = "192.168.1.225";
    private final static int serverPort = 5037; //TODO NU STIU CARE II PORTUL CORECT!?
    private final static String fileOutput =  "data.txt"; //"C:\\testout.pdf";

    @Override
    protected String doInBackground(String... location) {
        byte[] aByte = new byte[1];
        int bytesRead;

        Socket clientSocket = null;
        InputStream is = null;

        try {
            clientSocket = new Socket( serverIP , serverPort );
            is = clientSocket.getInputStream();
        } catch (IOException ex) {
            // Do exception handling
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        if (is != null) {

            FileOutputStream fos = null;
            BufferedOutputStream bos = null;
            try {
                fos = new FileOutputStream( fileOutput );
                bos = new BufferedOutputStream(fos);
                bytesRead = is.read(aByte, 0, aByte.length);

                do {
                    baos.write(aByte);
                    bytesRead = is.read(aByte);
                } while (bytesRead != -1);

                bos.write(baos.toByteArray());
                bos.flush();
                bos.close();
                clientSocket.close();
            } catch (IOException ex) {
                // Do exception handling
            }
        }
        return "";
    }
}