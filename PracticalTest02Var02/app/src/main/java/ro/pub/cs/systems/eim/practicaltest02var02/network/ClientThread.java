package ro.pub.cs.systems.eim.practicaltest02var02.network;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import ro.pub.cs.systems.eim.practicaltest02var02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02var02.general.Utilities;

public class ClientThread extends Thread {

    private String address;
    private int port;
    private String info;
    private TextView infoTextView;

    private Socket socket;

    public ClientThread(String address, int port, String info, TextView infoTextView) {
        this.address = address;
        this.port = port;
        this.info = info;
        this.infoTextView = infoTextView;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);
            if (socket == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Could not create socket!");
                return;
            }
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            printWriter.println(info);
            printWriter.flush();
            String information;
            while ((information = bufferedReader.readLine()) != null) {
                final String finalizedInformation = information;
                infoTextView.post(new Runnable() {
                   @Override
                    public void run() {
                       infoTextView.setText(infoTextView.getText().toString() + finalizedInformation);
                   }
                });
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }

}
