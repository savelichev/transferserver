package net.anotheria.transferserver.server;


import net.anotheria.transferserver.common.Request;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;


/**
 * Class for dialog with client. Invokes in new Thread for each client connection.
 * Wait for commands from client and send data back.
 */
public class ClientConnection implements Runnable {


    private Socket clientCommandSocket;
    private Socket clientDataSocket;

    /**
     * Server files directory.
     */
    private final String serverFilesDir = "server_files/";

    /**
     * Object Input Stream for commands from client.
     */
    private ObjectInputStream commandOIS;

    /**
     * Object Output Stream for data transfer to client.
     */
    private ObjectOutputStream dataOOS;

    /**
     * Object Input stream for data transfer from client.
     */
    private ObjectInputStream dataOIS;


    public ClientConnection(Socket clientCommandSocket, Socket clientDataSocket) {
        this.clientCommandSocket = clientCommandSocket;
        this.clientDataSocket = clientDataSocket;
    }

    /**
     * Wait for commands from client and invoke relevant method for each command.
     * Commands:
     * "PUT + file name" - for uploading file on server.
     * "GET + file name" - for sending file to client.
     * "DIR" - for sending to client list of files.
     * "EXIT" - for exit.
     * <p>
     * Commands and data sending thought serialization.
     */
    private void work() {

        try {
            commandOIS = new ObjectInputStream(clientCommandSocket.getInputStream());

            dataOOS = new ObjectOutputStream(clientDataSocket.getOutputStream());
            dataOIS = new ObjectInputStream(clientDataSocket.getInputStream());

            Request request = null;

            while ((request = (Request) commandOIS.readObject()) != null) {

                String command = request.getCommand();

                switch (command) {
                    case "PUT":
                        uploadFileOnServer(request);
                        break;
                    case "GET":
                        sendFileToClient(request);
                        break;
                    case "DIR":
                        sentListOfFilesToClient();
                        break;
                    default:
                        System.out.println("Incorrect command.");
                        break;
                }
                System.out.println("Waiting for a new command...");
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                clientCommandSocket.close();
                clientDataSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }

    private void sentListOfFilesToClient() {
        String[] fileList = new File(serverFilesDir).list();
        try {
            dataOOS.writeObject(fileList);
            dataOOS.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendFileToClient(Request request) {
        File file = new File(serverFilesDir + request.getFileName());
        try {
            dataOOS.writeObject(file);
            dataOOS.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void uploadFileOnServer(Request request) {
        File inFile = null;
        try {
            inFile = (File) dataOIS.readObject();
            File newFile = new File(serverFilesDir, request.getFileName());
            Files.copy(inFile.toPath(), (newFile.toPath()));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        work();
    }

}
