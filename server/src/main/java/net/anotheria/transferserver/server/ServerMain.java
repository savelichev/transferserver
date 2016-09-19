package net.anotheria.transferserver.server;


public class ServerMain {

    public static void main(String[] args) {

        new Thread(new Connector()).start();
    }
}
