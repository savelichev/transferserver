package net.anotheria.transferserver.common;

import java.io.Serializable;

public class Request implements Serializable {


    private static final long serialVersionUID = -1376342298112211206L;

    private String command;

    private String fileName;

    public Request(String command) {
        this.command = command;
    }

    public Request(String command, String fileName) {
        this.command = command;
        this.fileName = fileName;
    }

    public String getCommand() {
        return command;
    }

    public String getFileName() {
        return fileName;
    }
}
