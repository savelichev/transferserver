package net.anotheria.transferserver.server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Connector implements Runnable {

    private ServerSocket commandServerSocket;
    private ServerSocket dataServerSocket;


    public synchronized void startUpCommandServerSocket() {

        try {
            commandServerSocket = new ServerSocket(4444);
            dataServerSocket = new ServerSocket(5555);

            while (true) {
                Socket clientCommandSocket = commandServerSocket.accept();
                Socket clientDataSocket = dataServerSocket.accept();
                new Thread(new ClientConnection(clientCommandSocket, clientDataSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    @Override
    public void run() {
        startUpCommandServerSocket();
    }
}
