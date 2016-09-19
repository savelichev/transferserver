package net.anotheria.transferserver.client;


import net.anotheria.transferserver.common.Request;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Scanner;

/**
 * Client app for transfer files with server app. Client connext to two sockets, first for commands, second
 * for data transfer.
 */
public class Client {


    /**
     * Client files directory.
     */
    private final String clientDir = "client_files/";


    /**
     * Object Output Stream for client commands.
     */
    private ObjectOutputStream commandOOS;

    /**
     * Object Output Stream for data from client.
     */
    private ObjectOutputStream dataOOS;

    /**
     * Object Output Stream for data from server.
     */
    private ObjectInputStream dataOIS;


    /**
     * Scan commands from client and invoke relevant method for each command.
     * Commands:
     * "PUT + file name" - for uploading file on server.
     * "GET + file name" - for downloading file from server.
     * "DIR" - for receiving list of files on server.
     * "EXIT" - for exit.
     * <p>
     * Commands and data sending thought serialization.
     */
    public void work() {

        init();

        Scanner scanner = new Scanner(System.in);
        String[] commandLine;
        System.out.println("Enter new command...");

        while (!(commandLine = parseCommandLine(scanner.nextLine()))[0].equalsIgnoreCase("EXIT")) {
            String command = commandLine[0];
            String fileName = "";

            if (commandLine.length == 2) {
                fileName = commandLine[1];
            }

            switch (command) {
                case "PUT":
                    uploadFileOnServer(fileName);
                    break;
                case "GET":
                    downloadFileFromServer(fileName);
                    break;
                case "DIR":
                    getListFromServer();
                    break;
                default:
                    System.out.println("Incorrect command.");
                    break;
            }
            System.out.println("Enter new command...");
        }
        System.out.println("Bye...");
        scanner.close();
    }

    private void uploadFileOnServer(String fileName) {
        Request putRequest = new Request("PUT", fileName);

        try {
            commandOOS.writeObject(putRequest);
            commandOOS.flush();

            File file = new File(clientDir + fileName);
            dataOOS.writeObject(file);
            dataOOS.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void downloadFileFromServer(String fileName) {
        Request getRequest = new Request("GET", fileName);

        try {
            commandOOS.writeObject(getRequest);
            commandOOS.flush();

            File inFile = (File) dataOIS.readObject();
            File newFile = new File(clientDir, fileName);
            Files.copy(inFile.toPath(), (newFile.toPath()));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void getListFromServer() {

        Request dirRequest = new Request("DIR");

        try {
            commandOOS.writeObject(dirRequest);
            commandOOS.flush();

            String[] fileList = (String[]) dataOIS.readObject();
            for (String row : fileList) {
                System.out.println(row);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        try {
            Socket commandSocket = new Socket("localhost", 4444);
            Socket dataSocket = new Socket("localhost", 5555);

            commandOOS = new ObjectOutputStream(commandSocket.getOutputStream());

            dataOOS = new ObjectOutputStream(dataSocket.getOutputStream());
            dataOIS = new ObjectInputStream(dataSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String[] parseCommandLine(String inLine) {
        return inLine.split(" ");
    }


}
