package server;

import server.jacksonclasses.Table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class Message implements Serializable {

    private String messageUser;
    private String messageKlient;
    private String messageServer;
    private int klientID;
    private ArrayList<String> databases;
    private final ArrayList<Table> tables;

    public Message() {

        messageKlient = "";
        messageServer = "";
        messageUser = "";
        klientID = -1;
        databases = new ArrayList<>();
        tables = new ArrayList<>();
    }

    public String getMessageUser() {
        return messageUser;
    }

    public String getMessageKlient() {
        return messageKlient;
    }

    public String getMessageServer() {
        return messageServer;
    }

    public int getKlientID() {
        return klientID;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public void setMessageKlient(String messageKlient) {
        this.messageKlient = messageKlient;
    }

    public void setMessageServer(String messageServer) {
        this.messageServer = messageServer;
    }

    public void setKlientID(int klientID) {
        this.klientID = klientID;
    }

    public void setDatabases(ArrayList<String> databases) {
        this.databases = databases;
    }

    public boolean isMessageUserEmpy() {
        return messageUser.equals("");
    }

    public boolean isMessageKlientEmpy() {
        return messageKlient.equals("");
    }

    public boolean isMessageServerEmpy() {
        return messageServer.equals("");
    }

    public boolean isDatabasesEmpty() {
        return databases.isEmpty();
    }

    public boolean isTablesEmpty() {
        return tables.isEmpty();
    }

    public Collection<String> getDatabases() {
        return databases;
    }

}
