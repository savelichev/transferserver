package net.anotheria.transferserver.server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Connects client to the server.
 */
public class Connector implements Runnable {

    /**
     * Server socket fot commands.
     */
    private ServerSocket commandServerSocket;
    /**
     * Server socket for data transfer.
     */
    private ServerSocket dataServerSocket;


    /**
     * Start up server for listening sockets for client connection.
     * Each client connection executes in new thread.
     */
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
