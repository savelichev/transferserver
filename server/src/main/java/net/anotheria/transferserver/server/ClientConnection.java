package net.anotheria.transferserver.server;


import net.anotheria.transferserver.common.Request;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;


public class ClientConnection implements Runnable {


    private Socket clientCommandSocket;
    private Socket clientDataSocket;

    private final String serverFilesDir = "server_files/";

    private ObjectInputStream commandOIS;

    private ObjectOutputStream dataOOS;
    private ObjectInputStream dataOIS;


    public ClientConnection(Socket clientCommandSocket, Socket clientDataSocket) {
        this.clientCommandSocket = clientCommandSocket;
        this.clientDataSocket = clientDataSocket;
    }

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
                        File inFile = (File) dataOIS.readObject();
                        File newFile = new File(serverFilesDir, request.getFileName());
                        Files.copy(inFile.toPath(), (newFile.toPath()));
                        break;
                    case "GET":
                        File file = new File(serverFilesDir + request.getFileName());
                        dataOOS.writeObject(file);
                        dataOOS.flush();
                        break;
                    case "DIR":
                        String[] fileList = new File(serverFilesDir).list();
                        dataOOS.writeObject(fileList);
                        dataOOS.flush();
                        break;
                    default:
                        System.out.println("Incorrect command.");
                        break;
                }
                System.out.println("Waiting for a new command...");

            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void run() {
        work();
    }

}
