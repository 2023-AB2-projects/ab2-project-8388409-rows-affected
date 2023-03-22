package server;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Host {
    JSONObject catalog = new JSONObject();

    public Host() {
        Create_load_catalog();
        Create_socket_communication();
    }
    private void Create_load_catalog(){
        System.out.println("Starting server...");

        JSONObject databases = new JSONObject();
        JSONArray databasesArray = new JSONArray();
        databases.put("Databases", databasesArray);


        try {
            Reader reader = new FileReader("Catalog.json");
            catalog = (JSONObject) new JSONParser().parse(reader);
        } catch (FileNotFoundException e) {
            try {
                FileWriter fileWriter = new FileWriter("Catalog.json");
                fileWriter.write(databases.toJSONString());
                fileWriter.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } catch (ParseException e) {
            System.out.println("Catalog is empty, initializing...");
            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter("Catalog.json");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            try {
                fileWriter.write(databases.toJSONString());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            try {
                fileWriter.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    private void Create_socket_communication(){
        int portNumber = 1234; // replace with your port number

        try (
                ServerSocket serverSocket = new ServerSocket(portNumber);
                Socket clientSocket = serverSocket.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))

        ) {
            System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

            // Send a welcome message to the client
            out.println("Welcome to the server!");

            String inputLine;
            while (true){
//                System.out.println("waiting.");
                if ((inputLine = in.readLine()) != null && inputLine.length() > 0) {

                    System.out.println("Received from client: " + inputLine);
//                    out.println("You said: " + inputLine);
                    if (inputLine.equals("EXIT")) {
                        break;
                    }

                    // parse the input
                    Parser parser = new Parser(inputLine);

                }

            }

            System.out.println("Client disconnected.");
        } catch (IOException e) {
            System.err.println("Exception caught when trying to listen on port " + portNumber + " or listening for a connection: " + e.getMessage());
        }
    }

    private void CREATE_DATABASE(){

    }

    public static void main(String[] args) {
        Host host = new Host();
    }
}
